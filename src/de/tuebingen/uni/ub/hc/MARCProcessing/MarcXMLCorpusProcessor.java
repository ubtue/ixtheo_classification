package de.tuebingen.uni.ub.hc.MARCProcessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.TreeSet;

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
import de.tuebingen.uni.ub.hc.enums.IxTheoAnnotation;

/*
 * reads XML gives back Corpus or subcorpus
 */
public class MarcXMLCorpusProcessor {
    private static InputStream in;
    private static IxTheoCorpus corpus;

    public static void writeSubcorpusXML(String inputFilename, String outputFilename, int sizeOfTestCorpus)
            throws FileNotFoundException {
        in = new FileInputStream(inputFilename);
        FileOutputStream fw = new FileOutputStream(new File(outputFilename));
        MarcWriter writer = new MarcXmlWriter(fw, "UTF8");
        MarcReader reader = new MarcXmlReader(in);
        int count = 0;
        int countKDB = 0;
        while (reader.hasNext()) {
            Record record = reader.next();
            DataField currentField = (DataField) record.getVariableField("652");

            ControlField curControlField = (ControlField) record.getVariableField("008");

            // the three-character MARC language code takes character
            // positions 35-37
            String lang = curControlField.getData().substring(35, 38);

            // if record has IXTheo Anno add to our corpus
            if (currentField != null && lang.contains("ger") && countKDB < sizeOfTestCorpus / 2) {
                if (!currentField.find("KDB")) {
                    if (count < sizeOfTestCorpus / 2) {
                        writer.write(record);
                        count += 1;
                    }
                } else {
                    if (countKDB < sizeOfTestCorpus / 2) {
                        writer.write(record);
                        countKDB += 1;
                    }
                }
            }
        }
        System.out.println(count);
        writer.close();
    }

    public static IxTheoCorpus processMARCRecords(String pathname) throws FileNotFoundException {
        in = new FileInputStream(pathname);
        corpus = new IxTheoCorpus();
        MarcReader reader = new MarcXmlReader(in);
        int countRecForTest = 0;
        // iterate through all records in MARC corpus at pathname
        while (reader.hasNext()) {
            Record record = reader.next();
            String data = "";
            Subfield subfield;
            String ppn = "0";
            String author = "0";
            String secAuthor = "0";
            String lang = "0";
            String title = "0";
            String subtitle = "0";
            String authorGND = "0";
            String secAuthorGND = "0";
            // get control field with tag 652 IXTHEOAnno
            DataField currentField = (DataField) record.getVariableField("652");
            // if record has IXTheo Anno add to our corpus
            if (currentField != null) {
                ppn = record.getControlNumber();
                TreeSet<IxTheoAnnotation> ixTheoAnnoSet = new TreeSet<>();
                // get Annotation(s)
                data = currentField.getSubfieldsAsString("a");
                if (!data.contains(":")) {
                    ixTheoAnnoSet.add(IxTheoAnnotation.stringToIxTheoAnnotation(data));
                } else {
                    String[] annotations = data.split(":");
                    for (int i = 0; i < annotations.length; i++) {
                        ixTheoAnnoSet.add(IxTheoAnnotation.stringToIxTheoAnnotation(annotations[i].trim()));
                    }
                }
                // get Language
                // get control field with tag 008
                ControlField curControlField = (ControlField) record.getVariableField("008");
                data = curControlField.getData();

                // the three-character MARC language code takes character
                // positions 35-37
                lang = data.substring(35, 38);

                // get author 101
                currentField = (DataField) record.getVariableField("100");

                // System.out.println(record.toString());
                if (currentField != null) {
                    subfield = currentField.getSubfield('a');
                    if (subfield != null) {
                        author = subfield.getData();
                    }
                    subfield = currentField.getSubfield('0');
                    if (subfield != null) {
                        authorGND = subfield.getData();
                    }
                }
                // get second author
                currentField = (DataField) record.getVariableField("700");

                // System.out.println(record.toString());
                if (currentField != null) {
                    subfield = currentField.getSubfield('a');
                    if (subfield != null) {
                        secAuthor = subfield.getData();
                    }
                    subfield = currentField.getSubfield('0');
                    if (subfield != null) {
                        secAuthorGND = subfield.getData();
                    }
                }

                // get data field 245 title
                currentField = (DataField) record.getVariableField("245");
                // get the title proper
                subfield = currentField.getSubfield('a');
                title = subfield.getData();

                // take out seperator, because it's not part of the title itself
                title = title.replaceAll("\\u0098", "");

                subfield = currentField.getSubfield('b');
                if (subfield != null) {
                    subtitle = subfield.getData();
                }
                // // This field contains a publisher if no Author is named
                // subfield = currentField.getSubfield('c');
                // if (subfield != null && author.equalsIgnoreCase("0")) {
                // author = subfield.getData();
                // }

                // when record is filled add to corpus
                corpus.addRecord(new IxTheoRecord(ppn, lang, author, authorGND, secAuthor, secAuthorGND, title,
                        subtitle, ixTheoAnnoSet));
                for(IxTheoAnnotation anno : ixTheoAnnoSet){
                    if(corpus.getIxTheoAnnoCount().containsKey(anno)){
                        corpus.getIxTheoAnnoCount().put(anno, corpus.getIxTheoAnnoCount().get(anno)+1);
                    }
                    else{
                        corpus.getIxTheoAnnoCount().put(anno, 1);
                    }
                    
                }
            }
        }
        return corpus;
    }

    private static IxTheoCorpus createEnglishCorpus(IxTheoCorpus corpus) {
        IxTheoCorpus englishCorpus = new IxTheoCorpus();
        for (IxTheoRecord rec : corpus) {
            if (rec.getLanguage().contains("eng")) {
                englishCorpus.addRecord(rec);
            }
        }
        return englishCorpus;
    }

    private static IxTheoCorpus createGermanCorpus(IxTheoCorpus corpus) {
        IxTheoCorpus germanCorpus = new IxTheoCorpus();
        for (IxTheoRecord rec : corpus) {
            if (rec.getLanguage().contains("ger")) {
                germanCorpus.addRecord(rec);
            }
        }
        return germanCorpus;
    }

}
