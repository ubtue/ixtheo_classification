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
        corpus = MarcXMLCorpusProcessor.processMARCRecords(filename);
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
            
//            MarcXMLCorpusProcessor.writeSubcorpusXML("data/GesamtTiteldaten-post-pipeline-160612.xml", "data/test2000CorpusGer.xml", 2000);

            // Block for new creation
            IxTheoCorpus corpusGer = createNewCorpus("data/test2000CorpusGer.xml");
            corpusGer.serialize("data/corpusGerTest.ser");
            LinguisticProcessing ling = new LinguisticProcessing(corpusGer);
            corpusGer.serialize("data/corpusGerLingAnnoTest.ser");
            corpusGer.fillTokenMatrices();
            Writer theWriter = new Writer();
            theWriter.writeArfflemmaVector(corpusGer, "data/output/lemmaTest.arff", IxTheoAnnotation.KDB);
//            theWriter.

//            corpusGer.serialize("data/corpusGer.ser");
//            corpusGer = null;

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
