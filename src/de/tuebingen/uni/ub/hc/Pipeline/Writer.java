package de.tuebingen.uni.ub.hc.Pipeline;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

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
    

    public static void printARFFImpFeatures(IxTheoCorpus corpus, String pathname, IxTheoAnnotation IxTheo_Anno)
            throws IOException {
        StringBuilder toWrite = new StringBuilder();
        // toWrite.append("PPN, title, author, IXTheo_Annotation\n");
        toWrite.append(
                "@RELATION IX_Theo_Anno\n@ATTRIBUTE title STRING \n@ATTRIBUTE subtitle  STRING \n@ATTRIBUTE authorGND  STRING  \n@ATTRIBUTE secondAuthorGND  STRING\n@ATTRIBUTE class   {1,0}\n@DATA\n");
        for (IxTheoRecord f : corpus) {
            // System.out.println(f.getTitle());
            toWrite.append("'" + f.getTitle().replaceAll("\\p{Punct}", ""));
            toWrite.append("','" + f.getSubtitle().replaceAll("\\p{Punct}", ""));
            toWrite.append("'," + f.getAuthorGND().replaceAll("\\D", "") + ",");
            toWrite.append(f.getSecAuthorGND().replaceAll("\\D", "") + ",");
            if (f.getIxTheoAnnoSet().contains(IxTheo_Anno)) {
                toWrite.append("1");
            } else {
                toWrite.append("0");
            }
            toWrite.append("\n");
        }
        FileWriter writer = new FileWriter(new File(pathname));
        writer.write(toWrite.toString());
        writer.flush();
        writer.close();
    }

    public static void printIxTheoCategoryFrequenciesTable(IxTheoCorpus corpus, String pathname) throws IOException {
        StringBuilder toWrite = new StringBuilder();
        for (IxTheoAnnotation ita : corpus.getIxTheoAnnoCount().keySet()) {
            toWrite.append(ita + ", " + corpus.getIxTheoAnnoCount().get(ita) + "\n");
        }
        FileWriter writer  = new FileWriter(new File(pathname));
        writer.write(toWrite.toString());
        writer.flush();
        writer.close();
    }

    public static void writeArfflemmaVector(IxTheoCorpus corpus, String pathname, IxTheoAnnotation IxTheo_Anno)
            throws IOException {
        StringBuilder toWrite = new StringBuilder();
        toWrite.append("@RELATION ");
        toWrite.append(IxTheo_Anno.toShortString());
        toWrite.append("\n");
        // toWrite.append("PPN, ");

        // append all words as header
        // toWrite.append(corpus.printStringLemmaVector());
        Iterator<String> myIterator = corpus.getLemmaStringVector().iterator();
        while (myIterator.hasNext()) {
            toWrite.append("@ATTRIBUTE ");
            toWrite.append(myIterator.next().replaceAll("\\W", ""));
            toWrite.append(" NUMERIC");
            toWrite.append("\n");
        }
        // append class to classify
        toWrite.append("@ATTRIBUTE class {y,n}");
        toWrite.append("\n");
        toWrite.append("@DATA");
        toWrite.append("\n");
        for (IxTheoRecord f : corpus) {

            // System.out.println(f.getTitle());
            // toWrite.append(f.getPpnNumber());
            // toWrite.append(",");
            toWrite.append(f.lemmaVectortoString());
            if (f.getIxTheoAnnoSet().contains(IxTheo_Anno)) {
                toWrite.append("y");
            } else {
                toWrite.append("n");
            }
            toWrite.append("\n");
        }
        FileWriter writer  = new FileWriter(new File(pathname));
        writer.write(toWrite.toString());
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
        Vector<String> alphabetVector = corpus.getNeStringVector();

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
                System.out.println("NE in alphabet vector: "+ne);
                if(record.getNeSet() == null){
                    vals[vectorIndex] = 0;
                    System.out.println("EMPTYSET");
                }
                else if (record.getNeSet().contains(ne)) {
                    vals[vectorIndex] = 1;
                    System.err.println("SET CONTAINS NE");
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
        Vector<String> alphabetVector = corpus.getLemmaStringVector();

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
                System.out.println("NE in alphabet vector: "+ne);
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
//        System.out.println(data.toString());
        writer.flush();
        writer.close();
    }

}
