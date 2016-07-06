package de.tuebingen.uni.ub.hc.Corpus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tuebingen.uni.ub.hc.enums.IxTheo_Annotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Corpus class holds all individual files (Titeldatensaetze) in a list
 * 
 * @author heike cardoso
 *
 */
public class IxTheoCorpus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5152829236449977331L;
	private Vector<IxTheoRecord> fileList;
	private HashMap<IxTheo_Annotation, Integer> ixTheoAnnoCount;
	private HashMap<String, Integer> wordCounts, lemmaCounts, neCounts;
	private Vector<String> lemmaStringVector, neStringVector;

	public IxTheoCorpus() {
		fileList = new Vector<IxTheoRecord>();
		setWordCounts(new HashMap<String, Integer>());
		setLemmaCounts(new HashMap<String, Integer>());
		setNeCounts(new HashMap<String, Integer>());
		setIxTheoAnnoCount(new HashMap<IxTheo_Annotation, Integer>());
		setLemmaStringVector(new Vector<String>());
		setNeStringVector(new Vector<>());
	}

	public Vector<IxTheoRecord> getFileList() {
		return fileList;
	}

	public void setFileList(Vector<IxTheoRecord> fileList) {
		this.fileList = fileList;
	}

	public HashMap<IxTheo_Annotation, Integer> getIxTheoAnnoCount() {
		return ixTheoAnnoCount;
	}

	public void setIxTheoAnnoCount(HashMap<IxTheo_Annotation, Integer> ixTheoAnnoCount) {
		this.ixTheoAnnoCount = ixTheoAnnoCount;
	}

	public HashMap<String, Integer> getWordCounts() {
		return wordCounts;
	}

	public void setWordCounts(HashMap<String, Integer> wordCounts) {
		this.wordCounts = wordCounts;
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

	public HashMap<String, Integer> getLemmaCounts() {
		return lemmaCounts;
	}

	public void setLemmaCounts(HashMap<String, Integer> lemmaCounts) {
		this.lemmaCounts = lemmaCounts;
	}

	public HashMap<String, Integer> getNeCounts() {
		return neCounts;
	}

	public void setNeCounts(HashMap<String, Integer> neCounts) {
		this.neCounts = neCounts;
	}

	public Vector<String> getLemmaStringVector() {
		return lemmaStringVector;
	}

	public void setLemmaStringVector(Vector<String> lemmaVector) {
		this.lemmaStringVector = lemmaVector;
	}

	public Vector<String> getNeStringVector() {
		return neStringVector;
	}

	public void setNeStringVector(Vector<String> neVector) {
		this.neStringVector = neVector;
	}

	/**
	 * method to be called after constructing and annotating corpus this methods
	 * builds a Hash Map of Words and their frequencies in the titles
	 */
	public void fillTokenMatrices() {
		// System.out.println("Filling Matrix");
		// setLemmaCounts(new HashMap<String, Integer>());
		// setNeCounts(new HashMap<String, Integer>());
		Pattern pattern = Pattern
				.compile("(CC)|(DT)|(EX)|(IN)|(LS)|(MD)|(P.*)|(RP)|(SYM)|(TO)|(UH)|(\\p{Punct})|(\\W)");
		for (IxTheoRecord rec : this.getFileList()) {
			// for (int i = 0; i < rec.getLemmas().size(); i++) {
			// String lemma = rec.getLemmas().get(i);
			// // check in posTagList is corresponding POS is significant
			// Matcher m = pattern.matcher(rec.getPos().get(i));
			//
			// if (!m.matches()) {
			//// System.out.println("IS SIG:"+rec.getPos().get(i));
			// if (this.getLemmaCounts().keySet().contains(lemma)) {
			// this.getLemmaCounts().put(lemma, this.getLemmaCounts().get(lemma)
			// + 1);
			// } else {
			// this.getLemmaCounts().put(lemma, 1);
			// }
			// }
			// }
			//
			// for (String ne : rec.getNe()) {
			// System.out.println(ne);
			// if (this.getNeCounts().keySet().contains(ne)) {
			// this.getNeCounts().put(ne, this.getNeCounts().get(ne) + 1);
			// } else {
			// this.getNeCounts().put(ne, 1);
			// }
			// }
			// }

			for (int i = 0; i < rec.getTokenList().size(); i++) {
				CoreLabel token = rec.getTokenList().get(i);
				// check in posTagList is corresponding POS is significant
				Matcher m = pattern.matcher(token.get(PartOfSpeechAnnotation.class));

				if (!m.matches()) {
					// System.out.println("IS SIG:"+rec.getPos().get(i));
					String lemma = token.lemma();
					if (this.getLemmaCounts().keySet().contains(lemma)) {
						this.getLemmaCounts().put(lemma, this.getLemmaCounts().get(lemma) + 1);
					} else {
						this.getLemmaCounts().put(lemma, 1);
					}
				}
				String ne = token.get(NamedEntityTagAnnotation.class);
				if (this.getNeCounts().keySet().contains(ne)) {
					this.getNeCounts().put(ne, this.getNeCounts().get(ne) + 1);
				} else {
					this.getNeCounts().put(ne, 1);
				}
			}
		}
		createLemmaVectorForCorpus();
		createVectorsInRecords();
	}

	private void createLemmaVectorForCorpus() {
		Vector<String> theList = new Vector<>(this.getLemmaCounts().keySet().size());
		for (String s : this.getLemmaCounts().keySet()) {
			theList.add(s);
			// System.out.println(s+": "+getLemmaCounts().get(s));
		}
		this.setLemmaStringVector(theList);
		Iterator<String> myIterator = theList.iterator();
		// while (myIterator.hasNext()) {
		// System.out.println("In der Liste: "+myIterator.next());
		// }
		printStringLemmaVector();
	}

	private void createVectorsInRecords() {
		// Iterate over String Vector including alphabet, if record has word
		// fill with 1, else 0
		for (IxTheoRecord rec : this.getFileList()) {
			// first determine the size of the vector
			rec.setLemmaVector(new Vector<Integer>(this.getLemmaCounts().keySet().size()));

			// then iterate over model vector and set data points;
			for (int i = 0; i < this.getLemmaStringVector().size(); i++) {
				String curLemma = this.getLemmaStringVector().get(i);
				// System.out.println(curLemma);
				for (int j = 0; j < rec.getLemmas().length; j++) {
					if (rec.getLemmas()[j].equals(curLemma)) {
						rec.getLemmaVector().add(1);
						// System.out.println("1");
					} else {
						rec.getLemmaVector().add(0);
						// System.out.println("0");
					}
				}

			}
			// Do the same for NamedEntities
			rec.setNeVector(new Vector<Integer>(getNeStringVector().size()));
			// then iterate over model vector and set data points;
			for (int i = 0; i < this.getNeStringVector().size(); i++) {
				String curNe = this.getNeStringVector().get(i);
				for (int j = 0; j < rec.getLemmas().length; j++) {
					if (rec.getNe()[j].equals(curNe)) {
						rec.getNeVector().add(1);
					} else {
						rec.getNeVector().add(0);
					}
				}
			}
		}
		printStringLemmaVector();
	}

	public String printStringLemmaVector() {
		// System.out.println("in printStringLemmaVector()"+
		// getLemmaStringVector().size());
		StringBuilder toWrite = new StringBuilder();
		Iterator<String> myIterator = this.getLemmaStringVector().iterator();
		while (myIterator.hasNext()) {
			// System.out.println("Im Vector: "+ myIterator.next());
			toWrite.append(myIterator.next());
			toWrite.append(", ");
		}
		// System.out.println(toWrite.toString());
		return toWrite.toString();
	}

}
