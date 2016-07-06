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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ppnNumber;
	private String author;
	private String authorGND;
	private String SecAuthor;
	private String SecAuthorGND;
	private String publisher;
	private String language;
	private String title;
	private String subtitle;
	private Vector<CoreLabel> tokenList;
	private String[] words,lemmas, pos, ne;
	private Vector<Integer> lemmaVector, neVector;
	private TreeSet<IxTheo_Annotation> ixTheo_anno_set;
	private TreeSet<IxTheo_Annotation> namedEntity_set;

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

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPpnNumber() {
		return ppnNumber;
	}

	public void setPpnNumber(String ppnNumber) {
		this.ppnNumber = ppnNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		
		if (!title.equals("0")) {
			this.title = title.replaceAll("\\W^\\s","");
			String[] words = title.split("\\s");
		}
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public TreeSet<IxTheo_Annotation> getIxTheo_anno_set() {
		return ixTheo_anno_set;
	}

	public void setIxTheo_anno_set(TreeSet<IxTheo_Annotation> ixTheo_anno_set) {
		this.ixTheo_anno_set = ixTheo_anno_set;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAuthorGND() {
		return authorGND;
	}

	public void setAuthorGND(String authorGND) {
		this.authorGND = authorGND;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getSecAuthor() {
		return SecAuthor;
	}

	public void setSecAuthor(String secAuthor) {
		SecAuthor = secAuthor;
	}

	public String getSecAuthorGND() {
		return SecAuthorGND;
	}

	public void setSecAuthorGND(String secAuthorGND) {
		SecAuthorGND = secAuthorGND;
	}

	public TreeSet<IxTheo_Annotation> getNamedEntity_set() {
		return namedEntity_set;
	}

	public void setNamedEntity_set(TreeSet<IxTheo_Annotation> namedEntity_set) {
		this.namedEntity_set = namedEntity_set;
	}

	public String[] getLemmas() {
		return lemmas;
	}

	public void setLemmas(String[] lemmas) {
		this.lemmas = lemmas;
	}

	public String[] getPos() {
		return pos;
	}

	public void setPos(String[] pos) {
		this.pos = pos;
	}

	public String[] getNe() {
		return ne;
	}

	public void setNe(String[] ne) {
		this.ne = ne;
	}

	public String[] getWords() {
		return words;
	}

	public void setWords(String[] words) {
		this.words = words;
	}

	public Vector<Integer> getLemmaVector() {
		return lemmaVector;
	}

	public void setLemmaVector(Vector<Integer> lemmaVector) {
		this.lemmaVector = lemmaVector;
	}

	public Vector<Integer> getNeVector() {
		return neVector;
	}

	public void setNeVector(Vector<Integer> neVector) {
		this.neVector = neVector;
	}
	
	public String printLemmaVector(){
//		System.out.println("should now print lemma vector of File");
		StringBuilder toWrite = new StringBuilder();
		Iterator<Integer> myIterator = getLemmaVector().iterator();
		while (myIterator.hasNext()) {
			toWrite.append(myIterator.next());
			toWrite.append(", ");
		}
		return toWrite.toString();
	}
	
	public String printNeVector(){
		StringBuilder toWrite = new StringBuilder();
		Iterator<Integer> myIterator = getNeVector().iterator();
		while (myIterator.hasNext()) {
			toWrite.append(myIterator.next());
			toWrite.append(", ");
		}
		return toWrite.toString();
	}

	public Vector<CoreLabel> getTokenList() {
		return tokenList;
	}

	public void setTokenList(Vector<CoreLabel> tokenList) {
		this.tokenList = tokenList;
	}

}
