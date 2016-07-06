package de.tuebingen.uni.ub.hc.MARCProcessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;

import org.marc4j.MarcReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import de.tuebingen.uni.ub.hc.Corpus.IxTheoCorpus;
import de.tuebingen.uni.ub.hc.Corpus.IxTheoRecord;
import de.tuebingen.uni.ub.hc.enums.IxTheo_Annotation;

public class MARC4JProcessor {
	private static InputStream in;
	private static IxTheoCorpus corpus;

	public MARC4JProcessor(String pathname) throws FileNotFoundException {
		in = new FileInputStream(pathname);
		corpus = new IxTheoCorpus();
		processMARCRecords();
	}

	public static void processMARCRecords() throws FileNotFoundException {
		MarcReader reader = new MarcXmlReader(in);
//		FileOutputStream fw = new FileOutputStream(new File("data/gerCorpus.xml"));
//		MarcWriter writer = new MarcXmlWriter(fw, "UTF8");
		int countRecForTest = 0;
		IxTheoRecord ixtRecord;
		// iterate through all records in MARC corpus at pathname
		while (reader.hasNext()) {
			Record record = reader.next();
			String data = "";
			Subfield subfield;
			// get control field with tag 652 IXTHEOAnno
			DataField currentField = (DataField) record.getVariableField("652");
			// if record has IXTheo Anno add to our corpus
			if (currentField != null) {
				String ppn = record.getControlNumber();
				ixtRecord = new IxTheoRecord(ppn);
				// get Annotation(s)
				data = currentField.getSubfieldsAsString("a");
				if (!data.contains(":")) {
					ixtRecord.getIxTheo_anno_set().add(IxTheo_Annotation.stringToIxTheo_Annotation(data));
				} else {
					String[] annotations = data.split(":");
					for (int i = 0; i < annotations.length; i++) {
						ixtRecord.getIxTheo_anno_set()
								.add(IxTheo_Annotation.stringToIxTheo_Annotation(annotations[i].trim()));
					}
				}
				// get Language
				// get control field with tag 008
				ControlField curControlField = (ControlField) record.getVariableField("008");
				data = curControlField.getData();

				// the three-character MARC language code takes character
				// positions 35-37
				String lang = data.substring(35, 38);
				ixtRecord.setLanguage(lang);
				//create small corpus for debugging purposes
//				if(lang.contains("ger")){
////					System.out.println(lang+" "+countRecForTest);
//					writer.write(record);
//					countRecForTest +=1;
//				}

				// get author 101
				currentField = (DataField) record.getVariableField("100");
				
//				System.out.println(record.toString());
				if (currentField != null) {
					subfield = currentField.getSubfield('a');
					if (subfield != null) {
						String author = subfield.getData();
						ixtRecord.setAuthor(author);
					}
					subfield = currentField.getSubfield('0');
					if (subfield != null) {
						String authorGND = subfield.getData();
						ixtRecord.setAuthorGND(authorGND);
					}
				}
				//get second author
				currentField = (DataField) record.getVariableField("700");
				
//				System.out.println(record.toString());
				if (currentField != null) {
					subfield = currentField.getSubfield('a');
					if (subfield != null) {
						String secAuthor = subfield.getData();
						ixtRecord.setAuthor(secAuthor);
					}
					subfield = currentField.getSubfield('0');
					if (subfield != null) {
						String secAuthorGND = subfield.getData();
						ixtRecord.setSecAuthorGND(secAuthorGND);
					}
				}

				// get data field 245 title
				currentField = (DataField) record.getVariableField("245");
//				System.out.println(currentField);
				// get the title proper
				subfield = currentField.getSubfield('a');
				String title = subfield.getData();
				ixtRecord.setTitle(title.replaceAll("\\u0098", ""));
				subfield = currentField.getSubfield('b');
				if (subfield != null) {
					String subtitle = subfield.getData();
					ixtRecord.setSubtitle(subtitle);
				}
				subfield = currentField.getSubfield('c');
				if (subfield != null && ixtRecord.getAuthor().equalsIgnoreCase("0")) {
					String author = subfield.getData();
					ixtRecord.setAuthor(author);
				}

				// System.out.println(ixtRecord.getAuthor());
				
				//when record is filled add to corpus
				corpus.getFileList().add(ixtRecord);
			}
		}
		
//		writer.close();
	}

	public static void main(String args[]) throws Exception {
		corpus = new IxTheoCorpus();
		in = new FileInputStream("data/GesamtTiteldaten-post-pipeline-160612.xml");
		// MarcReader reader = new MarcStreamReader(in);
		processMARCRecords();

	}

	public static IxTheoCorpus getCorpus() {
		return corpus;
	}

	public static void setCorpus(IxTheoCorpus corpus) {
		MARC4JProcessor.corpus = corpus;
	}
}
