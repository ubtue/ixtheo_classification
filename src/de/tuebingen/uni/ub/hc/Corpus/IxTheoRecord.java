package de.tuebingen.uni.ub.hc.Corpus;

import java.io.Serializable;
import java.util.*;

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
    private List<CoreLabel> tokenList;
    private String[] words;
    private HashSet<String> neSet, lemmaSet;
    private List<Integer> lemmaVector, neVector;
    private TreeSet<IxTheoAnnotation> ixTheoAnnoSet;

    public IxTheoRecord(String ppn, String lang, String author, String authorGND, String secAuthor, String secAuthorGND,
            String title, String subtitle, TreeSet<IxTheoAnnotation> ixTheoAnnoSet) {

        this.ppnNumber = ppn;
        this.language = lang;
        this.author = author;
        this.authorGND = authorGND;
        this.secAuthor = secAuthor;
        this.secAuthorGND = secAuthorGND;
        this.title = title;
        this.subtitle = subtitle;
        this.ixTheoAnnoSet = ixTheoAnnoSet;
        this.neSet = new HashSet<>();
        this.lemmaSet = new HashSet<>();
        setTokenList(new ArrayList<>());
        setLemmaVector(new ArrayList<>());
        setNeVector(new ArrayList<>());
    }

    private String getStringOrEmpty(String string) {
        return string != null ? string : "";
    }

    public String getAuthor() {
        return getStringOrEmpty(author);
    }

    public String getAuthorGND() {
        return getStringOrEmpty(authorGND);
    }

    public TreeSet<IxTheoAnnotation> getIxTheoAnnoSet() {
        return ixTheoAnnoSet;
    }

    public String getLanguage() {
        return getStringOrEmpty(language);
    }

    public List<Integer> getLemmaVector() {
        return lemmaVector;
    }

    public List<Integer> getNeVector() {
        return neVector;
    }

    public String getPpnNumber() {
        return getStringOrEmpty(ppnNumber);
    }

    public String getSecAuthor() {
        return getStringOrEmpty(secAuthor);
    }

    public String getSecAuthorGND() {
        return getStringOrEmpty(secAuthorGND);
    }

    public String getSubtitle() {
        return getStringOrEmpty(subtitle);
    }

    public String getTitle() {
        return getStringOrEmpty(title);
    }

    public List<CoreLabel> getTokenList() {
        return tokenList;
    }

    public String[] getWords() {
        return words;
    }

    public String lemmaVectortoString() {
        StringBuilder toWrite = new StringBuilder();
        for (final Integer integer : getLemmaVector()) {
            toWrite.append(integer);
            toWrite.append(", ");
        }
        return toWrite.toString();
    }

    public String neVectortoString() {
        StringBuilder toWrite = new StringBuilder();
        for (final Integer integer : getNeVector()) {
            toWrite.append(integer);
            toWrite.append(", ");
        }
        return toWrite.toString();
    }

    public void setLemmaVector(List<Integer> lemmaVector) {
        this.lemmaVector = lemmaVector;
    }

    public void setNeVector(List<Integer> neVector) {
        this.neVector = neVector;
    }


    public void setTokenList(List<CoreLabel> tokenList) {
        this.tokenList = tokenList;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

    public HashSet<String> getNeSet() {
        return neSet;
    }
    public void addToNeSet(String ne){
        this.getNeSet().add(ne);
    }

    public HashSet<String> getLemmaSet() {
        return lemmaSet;
    }

   

}
