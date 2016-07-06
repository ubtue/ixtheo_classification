package de.tuebingen.uni.ub.hc.Pipeline;

import java.util.List;
import java.util.Properties;

import de.tuebingen.uni.ub.hc.Corpus.IxTheoCorpus;
import de.tuebingen.uni.ub.hc.Corpus.IxTheoRecord;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
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
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        Properties germanProperties = StringUtils.argsToProperties(
                new String[] { "tokenize, ssplit, pos, lemma, ner", "StanfordCoreNLP-german.properties" });
        StanfordCoreNLP pipeline = new StanfordCoreNLP(germanProperties);
        for (IxTheoRecord record : corpus.getRecordList()) {

            runStanfordCoreNLPPipeline(record, pipeline);
        }
    }

    private void runStanfordCoreNLPPipeline(IxTheoRecord record, StanfordCoreNLP pipeline) {
        String title = record.getTitle();

        // StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // read some text in the text variable
        String text = title;

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // System.out.println(document.toString());

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
                // record.getWords().add(word);
                String lemma = token.lemma();
                lemmas.append(lemma);
                lemmas.append("#");
                // this is the POS tag of the token
                // String pos = token.get(PartOfSpeechAnnotation.class);
                // record.getPos().add(pos);
                // this is the NER label of the token
                String ne = token.get(NamedEntityTagAnnotation.class);
                nes.append(ne);
                nes.append("#");
                // System.out.println("word: "+ word+" pos: "+pos+" ne: "+ne);
                record.getTokenList().add(token);

            }
            record.setLemmas(lemmas.toString().split("#"));
            record.setNe(nes.toString().split("#"));
            // this is the parse tree of the current sentence
            // Tree tree = sentence.get(TreeAnnotation.class);
            // System.out.println(tree.toString());

            // this is the Stanford dependency graph of the current sentence
            // SemanticGraph dependencies =
            // sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
            // System.out.println(dependencies.toCompactString());
        }

        // This is the coreference link graph
        // Each chain stores a set of mentions that link to each other,
        // along with a method for getting the most representative mention
        // Both sentence and token offsets start at 1!
        // Map<Integer, CorefChain> graph =
        // document.get(CorefChainAnnotation.class);

    }

}
