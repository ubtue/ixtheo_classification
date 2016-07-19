package de.tuebingen.uni.ub.hc.Corpus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final long serialVersionUID = -5152829236449977331L;
    private Vector<IxTheoRecord> recordList;
    private HashMap<IxTheoAnnotation, Integer> ixTheoAnnoCount;
    private HashMap<String, Integer> wordCounts, lemmaCounts, neCounts;
    private Vector<String> lemmaStringVector, neStringVector;
    private HashMap<String, Integer> indexMap;

    public IxTheoCorpus() {
        recordList = new Vector<IxTheoRecord>();
        setWordCounts(new HashMap<String, Integer>());
        setLemmaCounts(new HashMap<String, Integer>());
        setNeCounts(new HashMap<String, Integer>());
        setIxTheoAnnoCount(new HashMap<IxTheoAnnotation, Integer>());
        setLemmaStringVector(new Vector<String>());
        setNeStringVector(new Vector<>());
        indexMap = new HashMap<>();
    }
    
    /**
     * This method creates a Vector of the Alphabet of Lemmas
     */
    private void createLemmaVectorForCorpus() {
        this.setLemmaStringVector(new Vector<>(this.getLemmaCounts().keySet().size()));
        for (String s : this.getLemmaCounts().keySet()) {
            this.getLemmaStringVector().add(s);
        }
    }
    
    /**
     * This method creates a Vector of the Alphabet of NamedEntities
     */
    private void creatNeVectorForCorpus() {
        this.setNeStringVector(new Vector<>(this.getNeCounts().keySet().size()));
        for (String s : this.getNeCounts().keySet()) {
            this.getNeStringVector().add(s);
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
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("IxTheoCorpus class not found");
            c.printStackTrace();
            return;
        }
    }

    /**
     * method to be called after constructing and annotating corpus this methods
     * builds a Hash Map of Words and their frequencies in the titles
     */
    public void fillTokenMatrices() {
        Pattern pattern = Pattern
                .compile("(CC)|(DT)|(EX)|(IN)|(LS)|(MD)|(P.*)|(RP)|(SYM)|(TO)|(UH)|(\\p{Punct})|(\\W)");
        for (IxTheoRecord rec : this.recordList) {
            for (int i = 0; i < rec.getTokenList().size(); i++) {
                CoreLabel token = rec.getTokenList().get(i);
                // check in posTagList is corresponding POS is significant
                Matcher m = pattern.matcher(token.get(PartOfSpeechAnnotation.class));

                if (!m.matches()) {
                    String lemma = token.lemma();
                    if (this.getLemmaCounts().keySet().contains(lemma)) {
                        this.getLemmaCounts().put(lemma, this.getLemmaCounts().get(lemma) + 1);
                    } else {
                        this.getLemmaCounts().put(lemma, 1);
                    }
                    rec.getLemmaSet().add(token.lemma());
                }
                String ne = token.get(NamedEntityTagAnnotation.class);
                rec.getNeSet().add(token.get(NamedEntityTagAnnotation.class));
                if (this.getNeCounts().keySet().contains(ne)) {
                    this.getNeCounts().put(ne, this.getNeCounts().get(ne) + 1);
                } else {
                    this.getNeCounts().put(ne, 1);
                }
            }
        }
        createLemmaVectorForCorpus();
        creatNeVectorForCorpus();
//        createVectorsInRecords();
    }
    
    public void addRecord(IxTheoRecord record){
        this.recordList.add(record);
    }

    public HashMap<IxTheoAnnotation, Integer> getIxTheoAnnoCount() {
        return ixTheoAnnoCount;
     }

    public HashMap<String, Integer> getLemmaCounts() {
        return lemmaCounts;
    }

    public Vector<String> getLemmaStringVector() {
        return lemmaStringVector;
    }

    public HashMap<String, Integer> getNeCounts() {
        return neCounts;
    }

    public Vector<String> getNeStringVector() {
        return neStringVector;
    }


    public HashMap<String, Integer> getWordCounts() {
        return wordCounts;
    }

    public String printStringLemmaVector() {
        StringBuilder toWrite = new StringBuilder();
        Iterator<String> myIterator = this.getLemmaStringVector().iterator();
        while (myIterator.hasNext()) {
            toWrite.append(myIterator.next());
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

    public void setIxTheoAnnoCount(HashMap<IxTheoAnnotation, Integer> ixTheoAnnoCount) {
        this.ixTheoAnnoCount = ixTheoAnnoCount;
    }

    public void setLemmaCounts(HashMap<String, Integer> lemmaCounts) {
        this.lemmaCounts = lemmaCounts;
    }

    public void setLemmaStringVector(Vector<String> lemmaVector) {
        this.lemmaStringVector = lemmaVector;
    }

    public void setNeCounts(HashMap<String, Integer> neCounts) {
        this.neCounts = neCounts;
    }

    public void setNeStringVector(Vector<String> neVector) {
        this.neStringVector = neVector;
    }

    public void setWordCounts(HashMap<String, Integer> wordCounts) {
        this.wordCounts = wordCounts;
    }
    @Override
    public Iterator<IxTheoRecord> iterator() {
        return this.recordList.iterator();
    }
    
    public int getNumRecordsInCorpus(){
        return this.recordList.size();
    }
   

}
