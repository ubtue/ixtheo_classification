package de.tuebingen.uni.ub.hc.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.*;

import de.tuebingen.uni.ub.hc.Corpus.IxTheoCorpus;
import de.tuebingen.uni.ub.hc.Corpus.IxTheoRecord;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

public class LinguisticProcessing {
    IxTheoCorpus corpus;

    public LinguisticProcessing(IxTheoCorpus corpus) {
        this.corpus = corpus;
        try {
            annotate();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs annotates concurrently in 8 threads so it uses the 8 cores of sobek's cpu.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void annotate() throws ExecutionException, InterruptedException {
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        final List<Future<?>> futures = new ArrayList<>();
        final ConcurrentLinkedQueue<IxTheoRecord> records = corpus.getConcurrentRecords();
        ExecutorService executor = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 8; i++) {
             futures.add(executor.submit(() -> {
                Properties germanProperties = StringUtils.argsToProperties(
                        "tokenize, ssplit, pos, lemma, ner", "StanfordCoreNLP-german.properties");
                StanfordCoreNLP pipeline = new StanfordCoreNLP(germanProperties);

                IxTheoRecord record;
                while ((record = records.poll()) != null) {
                    runStanfordCoreNLPPipeline(record, pipeline);
                }
            }));
        }
        for (final Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();
    }

    private void runStanfordCoreNLPPipeline(IxTheoRecord record, StanfordCoreNLP pipeline) {
        String title = record.getTitle();

        // create an empty Annotation just with the given title
        Annotation document = new Annotation(title);

        // run all Annotators on this text
        pipeline.annotate(document);
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and
        // has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods

            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // this is the text of the token
                // String word = token.get(TextAnnotation.class);
                record.getTokenList().add(token);
                if(!token.get(NamedEntityTagAnnotation.class).equals("O")){
                    record.addToNeSet(token.lemma());
                }
                record.getLemmaSet().add(token.lemma());
            }
        }
    }

}
