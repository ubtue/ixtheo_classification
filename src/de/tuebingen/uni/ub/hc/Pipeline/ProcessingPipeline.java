package de.tuebingen.uni.ub.hc.Pipeline;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import de.tuebingen.uni.ub.hc.Corpus.IxTheoCorpus;
import de.tuebingen.uni.ub.hc.Corpus.IxTheoRecord;
import de.tuebingen.uni.ub.hc.MARCProcessing.MARC4JProcessor;
import de.tuebingen.uni.ub.hc.enums.IxTheo_Annotation;

public class ProcessingPipeline {

    private static MARC4JProcessor reader;

    public static IxTheoCorpus createDesCorpus() throws IOException {
        try {
            IxTheoCorpus corpus = deserializeCorpus("data/corpusGerLingAnno.ser");

            return corpus;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static IxTheoCorpus createEnglishCorpus(IxTheoCorpus c) {
        IxTheoCorpus cGer = new IxTheoCorpus();
        for (IxTheoRecord rec : c.getRecordList()) {
            if (rec.getLanguage().contains("eng")) {
                cGer.getRecordList().add(rec);
            }
        }
        return cGer;
    }

    private static IxTheoCorpus createGermanCorpus(IxTheoCorpus c) {
        IxTheoCorpus cGer = new IxTheoCorpus();
        for (IxTheoRecord rec : c.getRecordList()) {
            if (rec.getLanguage().contains("ger")) {
                cGer.getRecordList().add(rec);
            }
        }
        return cGer;
    }

    public static IxTheoCorpus createNewCorpus() throws IOException {
        IxTheoCorpus corpus;

        // reader = new
        // MARC4JProcessor("data/GesamtTiteldaten-post-pipeline-160612.xml");
        // reader = new MARC4JProcessor("data/testCorpus.xml");
        reader = new MARC4JProcessor("data/gerCorpus.xml");

        corpus = MARC4JProcessor.getCorpus();
        // create corpus only consisting of annotated IxTheo files for
        // training and testing:
        // IxTheoCorpus taggedOnlyCorpus = new IxTheoCorpus();
        System.out.println("num files in entire corpus: " + corpus.getRecordList().size());
        // for(IxTheoFile f : corpusAllFiles.getFileList()){
        // if(f.isIxTheoAnnotated() &&
        // f.getLanguage().equalsIgnoreCase("ger")){
        // counter += 1;
        // taggedOnlyCorpus.getFileList().add(f);
        // }
        // }
        // System.out.println("Total number of annotated files:
        // "+counter);
        Writer theWriter = new Writer();
        theWriter.printIxTheoCategoryFrequenciesTable(corpus, "data/output/IxTheoCategoryFrequencies.csv");
        theWriter.printARFFImpFeatures(corpus, "data/output/Imp.arff", IxTheo_Annotation.KDB);
        return corpus;
    }

    public static IxTheoCorpus deserializeCorpus(String filename) throws IOException, ClassNotFoundException {
        IxTheoCorpus e = null;

        FileInputStream fileIn = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        e = (IxTheoCorpus) in.readObject();
        in.close();
        fileIn.close();

        return e;

    }

    public static void main(String[] args) {

        try {
            System.out.println("Reading corpus");
            final long startTime = System.currentTimeMillis();

            // Block for new creation
            IxTheoCorpus corpusGer = createNewCorpus();
            // IxTheoCorpus corpusGer = createGermanCorpus(corpus);
            // corpus = null;
            corpusGer.serialize("data/corpusGer.ser");
            LinguisticProcessing ling = new LinguisticProcessing(corpusGer);
            corpusGer.serialize("data/corpusGerLingAnno.ser");
            corpusGer.fillTokenMatrices();
            System.out.println("IN PPipeline" + corpusGer.printStringLemmaVector());
            Writer theWriter = new Writer();
            theWriter.printArfflemmaVector(corpusGer, "data/output/lemma.arff", IxTheo_Annotation.KDB);

            corpusGer.serialize("data/corpusGerLingAnno.ser");
            corpusGer = null;

            // IxTheoCorpus corpus = createDesCorpus();
            // Writer theWriter = new Writer();
            // theWriter.printArfflemmaVector(corpus, "data/output/lemma.arff",
            // IxTheo_Annotation.KDB);

            final long endTime = System.currentTimeMillis();

            System.out.println("Total execution time: " + (endTime - startTime) / 1000);
            // ArrayList<String> paramList = new ArrayList<String>();
            // for(String s : reader.getPossibleParams()){
            // paramList.add(s);
            // }
            // Collections.sort(paramList);
            // System.out.println("Printing all used parameters:");
            // for(String s : paramList){
            // System.out.println(s);
            // }

        } catch (IOException e) {
            System.err.println("IOException ");
            e.printStackTrace();
        }
    }
}
