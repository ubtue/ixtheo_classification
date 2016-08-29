package de.tuebingen.uni.ub.hc.Pipeline;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import de.tuebingen.uni.ub.hc.Corpus.IxTheoCorpus;
import de.tuebingen.uni.ub.hc.Corpus.IxTheoRecord;
import de.tuebingen.uni.ub.hc.enums.IxTheoAnnotation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Give the corpus to this class to extract tables for the machine learner or
 * for statistics
 * 
 * @author heike cardoso
 *
 */
public class Writer {
    

    public static void printARFFImpFeatures(IxTheoCorpus corpus, String pathname, IxTheoAnnotation IxTheo_Anno) {
        try(FileWriter writer = new FileWriter(new File(pathname))) {
            // toWrite.append("PPN, title, author, IXTheo_Annotation\n");
            writer.write(
                    "@RELATION IX_Theo_Anno\n@ATTRIBUTE title STRING \n@ATTRIBUTE subtitle  STRING \n@ATTRIBUTE authorGND  STRING  \n@ATTRIBUTE secondAuthorGND  STRING\n@ATTRIBUTE class   {1,0}\n@DATA\n");
            for (IxTheoRecord f : corpus) {
                writer.write("'" + f.getTitle().replaceAll("\\p{Punct}", ""));
                writer.write("','" + f.getSubtitle().replaceAll("\\p{Punct}", ""));
                writer.write("'," + f.getAuthorGND().replaceAll("\\D", "") + ",");
                writer.write(f.getSecAuthorGND().replaceAll("\\D", "") + ",");
                if (f.getIxTheoAnnoSet().contains(IxTheo_Anno)) {
                    writer.write("1");
                } else {
                    writer.write("0");
                }
                writer.write("\n");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printIxTheoCategoryFrequenciesTable(IxTheoCorpus corpus, String pathname) throws IOException {
        FileWriter writer  = new FileWriter(new File(pathname));
        for (IxTheoAnnotation ita : corpus.getIxTheoAnnoCount().keySet()) {
            writer.write(ita + ", " + corpus.getIxTheoAnnoCount().get(ita) + "\n");
        }
        writer.flush();
        writer.close();
    }

    public static void writeArfflemmaVector(IxTheoCorpus corpus, String pathname, IxTheoAnnotation IxTheo_Anno)
            throws IOException {
        FileWriter writer  = new FileWriter(new File(pathname));
        writer.write("@RELATION ");
        writer.write(IxTheo_Anno.toShortString());
        writer.write("\n");
        // writer.write("PPN, ");

        // append all words as header
        // writer.write(corpus.printStringLemmaVector());
        for (final String s : corpus.getLemmaStringVector()) {
            writer.write("@ATTRIBUTE ");
            writer.write(s.replaceAll("\\W", ""));
            writer.write(" NUMERIC\n");
        }
        // append class to classify
        writer.write("@ATTRIBUTE class {y,n}\n@DATA\n");
        for (IxTheoRecord f : corpus) {

            // System.out.println(f.getTitle());
            // writer.write(f.getPpnNumber());
            // writer.write(",");
            writer.write(f.lemmaVectortoString());
            if (f.getIxTheoAnnoSet().contains(IxTheo_Anno)) {
                writer.write("y");
            } else {
                writer.write("n");
            }
            writer.write("\n");
        }
        writer.flush();
        writer.close();
    }

    public static void writeLemmaArff(IxTheoCorpus corpus, String pathname) throws IOException {
        FileWriter writer  = new FileWriter(new File(pathname));
        for(String s : corpus.getNeStringVector()){
            writer.write(s + "\n");
        }
        writer.flush();
        writer.close();
    }

    public static void writeNeArffWithWeka(IxTheoCorpus corpus, String pathname, IxTheoAnnotation anno) throws IOException {
        FastVector atts;
        FastVector attVals;
        Instances data;
        double[] vals;
        double[] valsRel;
        int i;
        FileWriter writer  = new FileWriter(new File(pathname));
        ArrayList<String> alphabetVector = corpus.getNeStringVector();

        // Fill attribute vector for file header
        // add all nes as attributes
        atts = new FastVector();
        for (String epsilon : alphabetVector) {
            atts.addElement(new Attribute(epsilon));
        }
        // add IxTheoAnnotation
        attVals = new FastVector();
        attVals.addElement(anno.toShortString());
        attVals.addElement("not_" + anno.toShortString());
        atts.addElement(new Attribute("IxTheoAnnotation", attVals));

        // 2. create Instances object
        data = new Instances("IxTheoRelation", atts, 0);

        for (IxTheoRecord record : corpus) {
            vals = new double[data.numAttributes()]; // important: needs NEW
                                                     // array!
            // - numeric
            int vectorIndex = 0;
            for (String ne : alphabetVector) {
                if(record.getNeSet() == null){
                    vals[vectorIndex] = 0;
                }
                else if (record.getNeSet().contains(ne)) {
                    vals[vectorIndex] = 1;
                } else {
                    vals[vectorIndex] = 0;
                }
                vectorIndex += 1;
            }
            String result = "not_" + anno.toShortString();
            if (record.getIxTheoAnnoSet().contains(anno)) {
                result = anno.toShortString();
            }
            vals[data.numAttributes() - 1] = attVals.indexOf(result);
            data.add(new Instance(1.0, vals));
        }
        writer.write(data.toString());
//        System.out.println(data.toString());
        writer.flush();
        writer.close();
    }
    
    public static void writeLemmaArffWithWeka(IxTheoCorpus corpus, String pathname, IxTheoAnnotation anno) throws IOException {
        FastVector atts;
        FastVector attVals;
        Instances data;
        double[] vals;
        double[] valsRel;
        int i;
        FileWriter writer  = new FileWriter(new File(pathname));
        ArrayList<String> alphabetVector = corpus.getLemmaStringVector();

        // Fill attribute vector for file header
        // add all nes as attributes
        atts = new FastVector();
        for (String epsilon : alphabetVector) {
            atts.addElement(new Attribute(epsilon));
        }
        // add IxTheoAnnotation
        attVals = new FastVector();
        attVals.addElement(anno.toShortString());
        attVals.addElement("not_" + anno.toShortString());
        atts.addElement(new Attribute("IxTheoAnnotation", attVals));

        // 2. create Instances object
        data = new Instances("IxTheoRelation", atts, 0);

        System.out.println(corpus.getNumRecordsInCorpus() + " * " + alphabetVector.size() + " * 64 Bit" + " = " + ((corpus.getNumRecordsInCorpus() / 1024.0) * (alphabetVector.size() / 1024.0) * 64 / 1024) + " GB");

        for (IxTheoRecord record : corpus) {
            vals = new double[data.numAttributes()]; // important: needs NEW
                                                     // array!
            // - numeric
            int vectorIndex = 0;
            for (String ne : alphabetVector) {
                if(record.getLemmaSet() == null){
                    vals[vectorIndex] = 0;
                }
                else if (record.getLemmaSet().contains(ne)) {
                    vals[vectorIndex] = 1;
                } else {
                    vals[vectorIndex] = 0;
                }
                vectorIndex += 1;
            }
            String result = "not_" + anno.toShortString();
            if (record.getIxTheoAnnoSet().contains(anno)) {
                result = anno.toShortString();
            }
            vals[data.numAttributes() - 1] = attVals.indexOf(result);
            data.add(new Instance(1.0, vals));
        }
        writer.write(data.toString());
        writer.flush();
        writer.close();
    }

}
