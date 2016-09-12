package de.tuebingen.uni.ub.hc.Corpus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tuebingen.uni.ub.hc.Counter;
import de.tuebingen.uni.ub.hc.enums.IxTheoAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Corpus class holds all individual files (Titeldatensaetze) in a list
 *
 * @author heike cardoso
 *
 */
public class IxTheoCorpus implements Serializable, Iterable<IxTheoRecord>
 {
    private static final long serialVersionUID = -5152829236449977330L;
    private static final Pattern TOKEN_PATTERN = Pattern
             .compile("(CC)|(DT)|(EX)|(IN)|(LS)|(MD)|(P.*)|(RP)|(SYM)|(TO)|(UH)|(\\p{Punct})|(\\W)");
    private ArrayList<IxTheoRecord> recordList;
    private HashMap<IxTheoAnnotation, Counter> ixTheoAnnoCount;
    private HashMap<String, Counter> wordCounts, lemmaCounts, neCounts;
    private ArrayList<String> lemmaStringVector, neStringVector;
    private HashMap<String, Counter> indexMap;

    public IxTheoCorpus() {
        recordList = new ArrayList<>();
        setWordCounts(new HashMap<>());
        setLemmaCounts(new HashMap<>());
        setNeCounts(new HashMap<>());
        setIxTheoAnnoCount(new HashMap<>());
        setLemmaStringVector(new ArrayList<>());
        setNeStringVector(new ArrayList<>());
        indexMap = new HashMap<>();
    }

    /**
     * This method creates a Vector of the Alphabet of Lemmas
     */
    private void createLemmaVectorForCorpus() {
        this.setLemmaStringVector(new ArrayList<>(this.getLemmaCounts().keySet().size()));
        for (String s : this.getLemmaCounts().keySet()) {
            this.getLemmaStringVector().add(s);
//            System.out.println("adding lemma: "+s);
        }
    }

    /**
     * This method creates a Vector of the Alphabet of NamedEntities
     */
    private void creatNeVectorForCorpus() {
        this.setNeStringVector(new ArrayList<>(this.getNeCounts().keySet().size()));
        for (String s : this.getNeCounts().keySet()) {
            this.getNeStringVector().add(s);
//            System.out.println("adding s: "+s);
        }
    }

    /**
     * construct corpus from serialized file
     */
    public void deserialize(String filename) {
        IxTheoCorpus e = null;
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            e = (IxTheoCorpus) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("IxTheoCorpus class not found");
            c.printStackTrace();
        }
    }

    /**
     * method to be called after constructing and annotating corpus this methods
     * builds a Hash Map of Words and their frequencies in the titles
     */
    public void fillTokenMatrices() {
        for (IxTheoRecord rec : this.recordList) {
            for (int i = 0; i < rec.getTokenList().size(); i++) {
                CoreLabel token = rec.getTokenList().get(i);
                // check in posTagList is corresponding POS is significant
                Matcher m = TOKEN_PATTERN.matcher(token.get(PartOfSpeechAnnotation.class));
                if (!m.matches()) {
                    String lemma = token.lemma();
                    this.getLemmaCounter(lemma).increase();
                }

                String ne = "";
                if(!token.get(NamedEntityTagAnnotation.class).equals("O")){
                    ne = token.lemma();
                    rec.addToNeSet(ne);
                    this.getNeCounter(ne).increase();
                }
            }
        }
        createLemmaVectorForCorpus();
        creatNeVectorForCorpus();
//        for(String s : this.lemmaStringVector){
//            System.out.println("lemma: " + s);
//        }
//        for(String s : this.neStringVector){
//            System.out.println("NAMED ENTITY: " + s);
//        }
//        createVectorsInRecords();
    }

    public void addRecord(IxTheoRecord record){
        this.recordList.add(record);
    }

    public HashMap<IxTheoAnnotation, Counter> getIxTheoAnnoCount() {
        return ixTheoAnnoCount;
     }

     public Counter getIxTheoAnnoCounter(IxTheoAnnotation ixTheoAnno) {
         Counter counter = getIxTheoAnnoCount().get(ixTheoAnno);
         if (counter == null) {
             counter = new Counter();
             getIxTheoAnnoCount().put(ixTheoAnno, counter);
         }
         return counter;
     }

    public HashMap<String, Counter> getLemmaCounts() {
        return lemmaCounts;
    }

     public Counter getLemmaCounter(String lemma) {
         Counter counter = getLemmaCounts().get(lemma);
         if (counter == null) {
             counter = new Counter();
             getLemmaCounts().put(lemma, counter);
         }
         return counter;
     }

    public ArrayList<String> getLemmaStringVector() {
        return lemmaStringVector;
    }

    public HashMap<String, Counter> getNeCounts() {
        return neCounts;
    }


     public Counter getNeCounter(String ne) {
         Counter counter = getNeCounts().get(ne);
         if (counter == null) {
             counter = new Counter();
             getNeCounts().put(ne, counter);
         }
         return counter;
     }

    public ArrayList<String> getNeStringVector() {
        return neStringVector;
    }


    public HashMap<String, Counter> getWordCounts() {
        return wordCounts;
    }


     public Counter getWordCounter(String word) {
         Counter counter = getWordCounts().get(word);
         if (counter == null) {
             counter = new Counter();
             getWordCounts().put(word, counter);
         }
         return counter;
     }

    public String printStringLemmaVector() {
        StringBuilder toWrite = new StringBuilder();
        for (final String s : this.getLemmaStringVector()) {
            toWrite.append(s);
            toWrite.append(", ");
        }
        return toWrite.toString();
    }

    public void serialize(String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in " + filename);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void setIxTheoAnnoCount(HashMap<IxTheoAnnotation, Counter> ixTheoAnnoCount) {
        this.ixTheoAnnoCount = ixTheoAnnoCount;
    }

     public void setLemmaCounts(HashMap<String, Counter> lemmaCounts) {
        this.lemmaCounts = lemmaCounts;
    }

    public void setLemmaStringVector(ArrayList<String> lemmaVector) {
        this.lemmaStringVector = lemmaVector;
    }

    public void setNeCounts(HashMap<String, Counter> neCounts) {
        this.neCounts = neCounts;
    }

    public void setNeStringVector(ArrayList<String> neVector) {
        this.neStringVector = neVector;
    }

    public void setWordCounts(HashMap<String, Counter> wordCounts) {
        this.wordCounts = wordCounts;
    }
    @Override
    public Iterator<IxTheoRecord> iterator() {
        return this.recordList.iterator();
    }

    public int getNumRecordsInCorpus(){
        return this.recordList.size();
    }

    public Queue<IxTheoRecord> getConcurrentRecords() {
        return new ConcurrentLinkedQueue<>(recordList);
    }
}
