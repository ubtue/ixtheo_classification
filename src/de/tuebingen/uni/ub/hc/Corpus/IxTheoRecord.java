package de.tuebingen.uni.ub.hc.Corpus;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import de.tuebingen.uni.ub.hc.enums.IxTheoAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * contains one data set "Titeldatensatz" with all annotations that are
 * available
 * 
 * @author heike cardoso
 *
 */
public class IxTheoRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private String ppnNumber;
    private String author;
    private String authorGND;
    private String secAuthor;
    private String secAuthorGND;
    private String language;
    private String title;
    private String subtitle;
    private Vector<CoreLabel> tokenList;
    private String[] words;
    private Vector<Integer> lemmaVector, neVector;
    private TreeSet<IxTheoAnnotation> ixTheoAnnoSet;
    private TreeSet<IxTheoAnnotation> namedEntitySet;

    public IxTheoRecord(String ppn, String lang, String author, String authorGND, String secAuthor, String secAuthorGND,
            String title, String subtitle, TreeSet<IxTheoAnnotation> ixTheoAnnoSet) {
        this.ppnNumber = ppn;
        setTokenList(new Vector<>());
        setLemmaVector(new Vector<>());
        setNeVector(new Vector<>());
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorGND() {
        return authorGND;
    }

    public TreeSet<IxTheoAnnotation> getIxTheoAnnoSet() {
        return ixTheoAnnoSet;
    }

    public String getLanguage() {
        return language;
    }

    public Vector<Integer> getLemmaVector() {
        return lemmaVector;
    }

    public TreeSet<IxTheoAnnotation> getNamedEntitySet() {
        return namedEntitySet;
    }

    public Vector<Integer> getNeVector() {
        return neVector;
    }

    public String getPpnNumber() {
        return ppnNumber;
    }

    public String getSecAuthor() {
        return secAuthor;
    }

    public String getSecAuthorGND() {
        return secAuthorGND;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }

    public Vector<CoreLabel> getTokenList() {
        return tokenList;
    }

    public String[] getWords() {
        return words;
    }

    public String printLemmaVector() {
        StringBuilder toWrite = new StringBuilder();
        Iterator<Integer> myIterator = getLemmaVector().iterator();
        while (myIterator.hasNext()) {
            toWrite.append(myIterator.next());
            toWrite.append(", ");
        }
        return toWrite.toString();
    }

    public String printNeVector() {
        StringBuilder toWrite = new StringBuilder();
        Iterator<Integer> myIterator = getNeVector().iterator();
        while (myIterator.hasNext()) {
            toWrite.append(myIterator.next());
            toWrite.append(", ");
        }
        return toWrite.toString();
    }

    public void setLemmaVector(Vector<Integer> lemmaVector) {
        this.lemmaVector = lemmaVector;
    }

    public void setNamedEntitySet(TreeSet<IxTheoAnnotation> namedEntitySet) {
        this.namedEntitySet = namedEntitySet;
    }

    public void setNeVector(Vector<Integer> neVector) {
        this.neVector = neVector;
    }


    public void setTokenList(Vector<CoreLabel> tokenList) {
        this.tokenList = tokenList;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

}
