package de.tuebingen.uni.ub.hc.MARCProcessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.StringTokenizer;
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

    public static TreeSet<IxTheoAnnotation> getAnnotations(Record record) {
        DataField field652 = (DataField) record.getVariableField("652");
        if (field652 == null) {
            return null;
        }
        TreeSet<IxTheoAnnotation> ixTheoAnnoSet = new TreeSet<>();
        String data = field652.getSubfieldsAsString("a");
        StringTokenizer annotationTokenizer = new StringTokenizer(data, ":");
        while (annotationTokenizer.hasMoreTokens()) {
            ixTheoAnnoSet.add(IxTheoAnnotation.stringToIxTheoAnnotation(annotationTokenizer.nextToken().trim()));
        }
        return ixTheoAnnoSet;
    }

    public static String getLanguage(Record record) {
        ControlField field008 = (ControlField) record.getVariableField("008");
        String data = field008.getData();
        // the three-character MARC language code takes character
        // positions 35-37
        return data.substring(35, 38);
    }

    public static String getAuthorName(DataField dataField) {
        if (dataField == null) {
            return null;
        }
        Subfield subfield = dataField.getSubfield('a');
        if (subfield != null) {
            return subfield.getData();
        }
        return null;
    }

    public static String getAuthorGND(DataField dataField) {
        if (dataField == null) {
            return null;
        }
        Subfield subfield = dataField.getSubfield('0');
        if (subfield != null) {
            return subfield.getData();
        }
        return null;
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
            DataField currentField;

            TreeSet<IxTheoAnnotation> ixTheoAnnoSet = getAnnotations(record);
            if (ixTheoAnnoSet != null) {
                ppn = record.getControlNumber();
                lang = getLanguage(record);

                // get author 101
                currentField = (DataField) record.getVariableField("100");
                author = getAuthorName(currentField);
                authorGND = getAuthorGND(currentField);

                // get second author
                currentField = (DataField) record.getVariableField("700");
                secAuthor = getAuthorName(currentField);
                secAuthorGND = getAuthorGND(currentField);

                // get data field 245 title
                currentField = (DataField) record.getVariableField("245");
                // get the title proper
                subfield = currentField.getSubfield('a');
                // take out seperator, because it's not part of the title itself
                title = subfield.getData().replaceAll("\\u0098", "");

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
                    corpus.getIxTheoAnnoCounter(anno).increase();
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
