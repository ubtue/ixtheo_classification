package de.tuebingen.uni.ub.hc.Pipeline;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import de.tuebingen.uni.ub.hc.Corpus.IxTheoCorpus;
import de.tuebingen.uni.ub.hc.MARCProcessing.MarcXMLCorpusProcessor;
import de.tuebingen.uni.ub.hc.enums.IxTheoAnnotation;

public class ProcessingPipeline {

    private static MarcXMLCorpusProcessor reader;

    public static IxTheoCorpus createDesCorpus(String filename) throws IOException {
        try {
            IxTheoCorpus corpus = deserializeCorpus(filename);
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
        Writer.printIxTheoCategoryFrequenciesTable(corpus, "data/output/IxTheoCategoryFrequencies.csv");
        Writer.printARFFImpFeatures(corpus, "data/output/Imp.arff", IxTheoAnnotation.KDB);
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
            // MarcXMLCorpusProcessor.writeSubcorpusXML("data/gerCorpus.xml",
            // "data/output/TestCorpus.xml", 1000);
            // MarcXMLCorpusProcessor.writeSubcorpusXML("data/GesamtTiteldaten-post-pipeline-160612.xml",
            // "data/test2000CorpusGer.xml", 2000);

            // Block for new creation
//            MarcXMLCorpusProcessor.writeSubcorpusXML("data/gerCorpus.xml",
//                     "data/output/TestCorpus.xml", 10);
             IxTheoCorpus corpusGer = createNewCorpus("data/gerCorpus.xml");
             corpusGer.serialize("data/corpusGer.ser");
             LinguisticProcessing ling = new LinguisticProcessing(corpusGer);
             corpusGer.serialize("data/corpusGerLingAnno.ser");
             corpusGer.fillTokenMatrices();
             Writer.writeLemmaArffWithWeka(corpusGer,
             "data/output/lemmaWeka.arff", IxTheoAnnotation.KDB);
             Writer.writeNeArffWithWeka(corpusGer,
             "data/output/wekaNe.arff", IxTheoAnnotation.KDB);

            // corpusGer.serialize("data/corpusGer.ser");
            // corpusGer = null;

//            IxTheoCorpus corpusGer = createDesCorpus("data/corpusGerLingAnno.ser");
//            corpusGer.fillTokenMatrices();
//            System.out.println("filled Matrices");
//            Writer.writeLemmaArff(corpusGer, "data/output/test.txt");
//            Writer.printIxTheoCategoryFrequenciesTable(corpusGer, "data/output/IxTheoCategoryFrequencies.csv");
//            Writer.writeArfflemmaVector(corpusGer, "data/output/lemma.arff", IxTheoAnnotation.KDB);
//            Writer.writeNeArffWithWeka(corpusGer, "data/output/wekaNe.arff", IxTheoAnnotation.KDB);
//            System.out.println("wrote files");
             
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + (endTime - startTime) / 1000 / 60 + " min");
        } catch (IOException e) {
            System.err.println("IOException ");
            e.printStackTrace();
        }
    }
}
