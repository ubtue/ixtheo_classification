package de.tuebingen.uni.ub.hc.Pipeline;


import de.tuebingen.uni.ub.hc.Corpus.IxTheoCorpus;
import de.tuebingen.uni.ub.hc.Corpus.IxTheoRecord;
import de.tuebingen.uni.ub.hc.enums.IxTheoAnnotation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


/**
 * Give the corpus to this class to extract tables for the machine learner or
 * for statistics
 *
 * @author heike cardoso
 */
public class Writer {
    private static final int INSTANCE_BUFFER_SIZE = 500;

    public static void printARFFImpFeatures(IxTheoCorpus corpus, String pathname, IxTheoAnnotation IxTheo_Anno) {
        try (FileWriter writer = new FileWriter(new File(pathname))) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printIxTheoCategoryFrequenciesTable(IxTheoCorpus corpus, String pathname) throws IOException {
        FileWriter writer = new FileWriter(new File(pathname));
        for (IxTheoAnnotation ita : corpus.getIxTheoAnnoCount().keySet()) {
            writer.write(ita + ", " + corpus.getIxTheoAnnoCount().get(ita) + "\n");
        }
        writer.flush();
        writer.close();
    }

    public static void writeArfflemmaVector(IxTheoCorpus corpus, String pathname, IxTheoAnnotation IxTheo_Anno)
            throws IOException {
        FileWriter writer = new FileWriter(new File(pathname));
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
        FileWriter writer = new FileWriter(new File(pathname));
        for (String s : corpus.getNeStringVector()) {
            writer.write(s + "\n");
        }
        writer.flush();
        writer.close();
    }

    public static void writeNeArffWithWeka(IxTheoCorpus corpus, String pathname, IxTheoAnnotation anno) throws IOException {
        FastVector atts;
        FastVector attVals;
        MyInstances data;
        double[] vals;
        double[] valsRel;
        int i;
        FileWriter writer = new FileWriter(new File(pathname));
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
        data = new MyInstances("IxTheoRelation", atts, 0);

        // 3. Write file header using empty Instances-object.
        writer.write(data.toString());

        for (IxTheoRecord record : corpus) {
            writeRecordPartial(writer, record.getNeSet(), record.getIxTheoAnnoSet(), alphabetVector, anno);
        }
        writer.flush();
        writer.close();
    }

    private static Instance toInstance(HashSet<String> set, TreeSet<IxTheoAnnotation> annotations, ArrayList<String> alphabetVector, FastVector attVals, IxTheoAnnotation anno, int numAttributes) {
        double[] vals = new double[numAttributes]; // important: needs NEW
        // array!
        // - numeric
        int vectorIndex = 0;
        for (String ne : alphabetVector) {
            if (set == null) {
                vals[vectorIndex] = 0;
            } else if (set.contains(ne)) {
                vals[vectorIndex] = 1;
            } else {
                vals[vectorIndex] = 0;
            }
            vectorIndex += 1;
        }
        String result = "not_" + anno.toShortString();
        if (annotations.contains(anno)) {
            result = anno.toShortString();
        }
        vals[numAttributes - 1] = attVals.indexOf(result);
        return new Instance(1.0, vals);
    }

    public static void writeLemmaArffWithWeka(IxTheoCorpus corpus, String pathname, IxTheoAnnotation anno) throws IOException {
        FastVector atts;
        FastVector attVals;
        Instances data;
        double[] vals;
        double[] valsRel;
        int i;
        FileWriter writer = new FileWriter(new File(pathname));
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

        // 3. Write file header using empty Instances-object.
        writer.write(data.toString());

        for (IxTheoRecord record : corpus) {
            writeRecordPartial(writer, record.getLemmaSet(), record.getIxTheoAnnoSet(), alphabetVector, anno);
        }
        writer.flush();
        writer.close();
    }

    /**
     * See http://weka.wikispaces.com/ARFF+(stable+version)#Sparse%20ARFF%20files for more information.
     *
     * @param writer
     * @param recordPartialData
     * @param annotations
     * @param alphabetVector
     * @param anno
     * @throws IOException
     */
    private static void writeRecordPartial(FileWriter writer, Set<String> recordPartialData, TreeSet<IxTheoAnnotation> annotations, ArrayList<String> alphabetVector, IxTheoAnnotation anno) throws IOException {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{");
        if (recordPartialData != null) {
            int vectorIndex = 0;
            for (final String word : alphabetVector) {
                if (recordPartialData.contains(word)) {
                    buffer.append(vectorIndex).append(" 1,");
                }
                ++vectorIndex;
            }
        }
        if (annotations.contains(anno)) {
            buffer.append(anno.toShortString());
        } else {
            buffer.append("not_").append(anno.toShortString());
        }
        buffer.append("}\n");
        writer.write(buffer.toString());
    }

    static class MyInstances extends Instances {

        public MyInstances(final String name, final FastVector attInfo, final int capacity) {
            super(name, attInfo, capacity);
        }

        // Make this Method public.
        public String stringWithoutHeader() {
            return super.stringWithoutHeader();
        }
    }
}
