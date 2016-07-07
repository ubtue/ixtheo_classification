package de.tuebingen.uni.ub.hc.Pipeline;

import java.util.List;
import java.util.Properties;

import de.tuebingen.uni.ub.hc.Corpus.IxTheoCorpus;
import de.tuebingen.uni.ub.hc.Corpus.IxTheoRecord;
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
        annotate();
    }

    public void annotate() {
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        Properties germanProperties = StringUtils.argsToProperties(
                new String[] { "tokenize, ssplit, pos, lemma, ner", "StanfordCoreNLP-german.properties" });
        StanfordCoreNLP pipeline = new StanfordCoreNLP(germanProperties);
        for (IxTheoRecord record : corpus) {
            runStanfordCoreNLPPipeline(record, pipeline);
        }
    }

    private void runStanfordCoreNLPPipeline(IxTheoRecord record, StanfordCoreNLP pipeline) {
        String title = record.getTitle();
        // read some text in the text variable
        String text = title;

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and
        // has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            StringBuilder lemmas = new StringBuilder();
            StringBuilder nes = new StringBuilder();

            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(TextAnnotation.class);
                record.getTokenList().add(token);
            }
        }
    }

}
