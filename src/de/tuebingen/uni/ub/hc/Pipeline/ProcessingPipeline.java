package de.tuebingen.uni.ub.hc.Pipeline;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import de.tuebingen.uni.ub.hc.Corpus.IxTheoCorpus;
import de.tuebingen.uni.ub.hc.Corpus.IxTheoRecord;
import de.tuebingen.uni.ub.hc.MARCProcessing.MarcXMLCorpusProcessor;
import de.tuebingen.uni.ub.hc.enums.IxTheoAnnotation;

public class ProcessingPipeline {

    private static MarcXMLCorpusProcessor reader;

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

    public static IxTheoCorpus createNewCorpus(String filename) throws IOException {
        IxTheoCorpus corpus;

        // reader = new
        // MARC4JProcessor("data/GesamtTiteldaten-post-pipeline-160612.xml");
        // reader = new MARC4JProcessor("data/testCorpus.xml");
        corpus = MarcXMLCorpusProcessor.processMARCRecords(filename);
        // create corpus only consisting of annotated IxTheo files for
        // training and testing:
        // IxTheoCorpus taggedOnlyCorpus = new IxTheoCorpus();
        System.out.println("num files in entire corpus: " + corpus.getNumRecordsInCorpus());
        Writer theWriter = new Writer();
        theWriter.printIxTheoCategoryFrequenciesTable(corpus, "data/output/IxTheoCategoryFrequencies.csv");
        theWriter.printARFFImpFeatures(corpus, "data/output/Imp.arff", IxTheoAnnotation.KDB);
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
            IxTheoCorpus corpusGer = createNewCorpus("data/gerCorpus.xml");
            // IxTheoCorpus corpusGer = createGermanCorpus(corpus);
            // corpus = null;
            corpusGer.serialize("data/corpusGer.ser");
            LinguisticProcessing ling = new LinguisticProcessing(corpusGer);
            corpusGer.serialize("data/corpusGerLingAnno.ser");
            corpusGer.fillTokenMatrices();
            System.out.println("IN PPipeline" + corpusGer.printStringLemmaVector());
            Writer theWriter = new Writer();
            theWriter.printArfflemmaVector(corpusGer, "data/output/lemma.arff", IxTheoAnnotation.KDB);

            corpusGer.serialize("data/corpusGerLingAnno.ser");
            corpusGer = null;

            // IxTheoCorpus corpus = createDesCorpus();
            // Writer theWriter = new Writer();
            // theWriter.printArfflemmaVector(corpus, "data/output/lemma.arff",
            // IxTheoAnnotation.KDB);

            final long endTime = System.currentTimeMillis();

            System.out.println("Total execution time: " + (endTime - startTime) / 1000);
        } catch (IOException e) {
            System.err.println("IOException ");
            e.printStackTrace();
        }
    }
}
