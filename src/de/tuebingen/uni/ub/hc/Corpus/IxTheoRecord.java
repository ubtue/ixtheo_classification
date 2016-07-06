package de.tuebingen.uni.ub.hc.Corpus;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import de.tuebingen.uni.ub.hc.enums.IxTheo_Annotation;
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
    private String publisher;
    private String language;
    private String title;
    private String subtitle;
    private Vector<CoreLabel> tokenList;
    private String[] words, lemmas, pos, ne;
    private Vector<Integer> lemmaVector, neVector;
    private TreeSet<IxTheo_Annotation> ixTheoAnnoSet;
    private TreeSet<IxTheo_Annotation> namedEntitySet;

    public IxTheoRecord(String ppn) {
        setPpnNumber(ppn);
        setLanguage("0");
        setAuthor("0");
        setAuthorGND("0");
        setSecAuthor("0");
        setSecAuthorGND("0");
        setPublisher("0");
        setTitle("0");
        setSubtitle("0");
        setTokenList(new Vector<>());
        setLemmaVector(new Vector<>());
        setNeVector(new Vector<>());
        setIxTheo_anno_set(new TreeSet<IxTheo_Annotation>());
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorGND() {
        return authorGND;
    }

    public TreeSet<IxTheo_Annotation> getIxTheoAnnoSet() {
        return ixTheoAnnoSet;
    }

    public String getLanguage() {
        return language;
    }

    public String[] getLemmas() {
        return lemmas;
    }

    public Vector<Integer> getLemmaVector() {
        return lemmaVector;
    }

    public TreeSet<IxTheo_Annotation> getNamedEntity_set() {
        return namedEntitySet;
    }

    public String[] getNe() {
        return ne;
    }

    public Vector<Integer> getNeVector() {
        return neVector;
    }

    public String[] getPos() {
        return pos;
    }

    public String getPpnNumber() {
        return ppnNumber;
    }

    public String getPublisher() {
        return publisher;
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
        // System.out.println("should now print lemma vector of File");
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

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAuthorGND(String authorGND) {
        this.authorGND = authorGND;
    }

    public void setIxTheo_anno_set(TreeSet<IxTheo_Annotation> ixTheoAnnoSet) {
        this.ixTheoAnnoSet = ixTheoAnnoSet;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setLemmas(String[] lemmas) {
        this.lemmas = lemmas;
    }

    public void setLemmaVector(Vector<Integer> lemmaVector) {
        this.lemmaVector = lemmaVector;
    }

    public void setNamedEntity_set(TreeSet<IxTheo_Annotation> namedEntity_set) {
        this.namedEntitySet = namedEntity_set;
    }

    public void setNe(String[] ne) {
        this.ne = ne;
    }

    public void setNeVector(Vector<Integer> neVector) {
        this.neVector = neVector;
    }

    public void setPos(String[] pos) {
        this.pos = pos;
    }

    public void setPpnNumber(String ppnNumber) {
        this.ppnNumber = ppnNumber;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setSecAuthor(String secAuthor) {
        secAuthor = secAuthor;
    }

    public void setSecAuthorGND(String secAuthorGND) {
        secAuthorGND = secAuthorGND;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setTitle(String title) {

        if (!title.equals("0")) {
            this.title = title.replaceAll("\\W^\\s", "");
            String[] words = title.split("\\s");
        }
    }

    public void setTokenList(Vector<CoreLabel> tokenList) {
        this.tokenList = tokenList;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

}
