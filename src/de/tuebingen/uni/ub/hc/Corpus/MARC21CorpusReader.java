// package de.tuebingen.uni.ub.hc.Corpus;
//
// import java.io.BufferedReader;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.FileReader;
// import java.io.IOException;
// import java.io.ObjectInputStream;
// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.TreeMap;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
//
// import de.tuebingen.uni.ub.hc.enums.IxTheo_Annotation;
//
// public class MARC21CorpusReader {
// private boolean isNewFile = true;
// private String ppn = "";
// private IxTheoCorpus corpus;
// private IxTheoRecord currentFile;
// private HashSet<String> possibleParams;
//
// public MARC21CorpusReader(String filename) throws IOException,
// FileNotFoundException {
//
// setCorpus(new IxTheoCorpus());
// possibleParams = new HashSet<String>();
// read(filename);
// System.out.println("num Files: " + getCorpus().getFileList().size());
// }
//
// // public MARC21CorpusReader(String filename, String language) throws
// // IOException, FileNotFoundException {
// //
// // }
//
// private void read(String filename) throws IOException {
//
// FileReader reader = new FileReader(new File(filename));
// BufferedReader br = new BufferedReader(reader);
// String line;
// while ((line = br.readLine()) != null) {
// lineProcessing(line.trim());
// }
// }
//
// /**
// * check if file is new and fill file object
// *
// * @param line
// */
// private void lineProcessing(String line) {
// // System.out.println(line);
// String[] lineSplit = line.split(":");
// // is this the first file?
// if (!lineSplit[0].equalsIgnoreCase(ppn)) {
// // file is new
// // if not first file
// if (!ppn.equals("")) {
// // only add to corpus if it is annotated and German
// if (currentFile.getLanguage().contains("ger") &&
// currentFile.isIxTheoAnnotated()) {
// getCorpus().getFileList().add(currentFile);
// }
// }
// // System.out.println("added file: "+ppn);
// ppn = lineSplit[0].trim();
// currentFile = new IxTheoRecord(ppn);
// // System.out.println("PPN: "+ ppn);
// }
// // case we are adding info to existing file
// else {
// // if (ppn.equalsIgnoreCase("410388289")) { // print out an
// // annotated
// // // file to check
// // // settings
// // System.out.println("ixTheo: " + line); // delete when working
// // } // delete when working
// // else
// fillFileObj(lineSplit);
// }
// }
//
// /**
// * Adds parameters to existing File Obj
// *
// * @param lineSplit
// */
// private void fillFileObj(String[] lineSplit) {
// // lineSplit e.g.: 000000353:041:0 ^_ager
// // define regex to filter out parameter indications
// // has subfield "\\u001F"
// // 359881793:652: ^_aAA
// // Make a list of all Parameters used in this corpus
//
// String currentTag = lineSplit[1].trim();
// if (getPossibleParams().isEmpty() ||
// !getPossibleParams().contains(currentTag)) {
// getPossibleParams().add(currentTag);
// }
// // if the field contains an IxTheo Annotation
// if (lineSplit[1].contains("652")) {
// // System.err.println("Found IXTheoAnno: ");
// // for(int i = 0; i<lineSplit.length;i++){
// // System.err.println(i+": "+lineSplit[i]);
// // }
// detectIxTheoAnno(lineSplit);
// }
// // detect language
// else if (lineSplit[1].trim().contains("041")) {
// currentFile.setLanguage(lineSplit[2].substring(1).trim());
// }
// //detect author
// else if(lineSplit[1].trim().contains("100")){
// currentFile.setAuthor(lineSplit[2].substring(1).trim());
// }
// // detect title
// else if (lineSplit[1].trim().contains("245")) {
//// System.out.println(lineSplit[2]);
// String[] titleSplit = lineSplit[2].split("\\u001F");
// // Now set subcategories of title
// for (int i = 0; i < titleSplit.length; i++) {
// if (titleSplit[i].trim().startsWith("a")) {
// currentFile.setTitle(titleSplit[i].trim().substring(1));
// } else if (titleSplit[i].trim().startsWith("c")&&
// currentFile.getAuthor().equals("")) {
// currentFile.setAuthor(titleSplit[i].substring(1).trim());
// }
// }
// }
// // i==0 is the ppn
//
// if (!currentTag.equals("LOK") && !currentTag.equals("SPR")) {
// if (Integer.parseInt(currentTag) > 9) {
// String currentSubtag = "";
// String currentSubcat = "";
// for (int i = 1; i < lineSplit.length; i++) {
// // at 1 tag and new TreeMap
// if (i == 1) {
// // tag might have been used in previous line
// if (!currentFile.getTagContentMap().containsKey(currentTag)) {
// currentFile.getTagContentMap().put(currentTag, new TreeMap<String,
// ArrayList<String>>());
// } else {
//
// }
// }
// // all others are subfield characterized by a single smaller
// // case letter and the content,
// // therefore evens are subfield tags, odds the values
// else if (i % 2 == 0) {
// currentSubtag = lineSplit[i];
// } else {
// currentSubtag = lineSplit[i];
// ArrayList<String> subcatList = new ArrayList<>();
// // does subtag exist
// if
// (currentFile.getTagContentMap().get(currentTag).containsKey(currentSubcat)) {
// subcatList =
// currentFile.getTagContentMap().get(currentTag).get(currentSubcat);
// }
// subcatList.add(currentSubcat);
// currentFile.getTagContentMap().get(currentTag).put(currentSubtag,
// subcatList);
//
// }
// }
// }
// }
// }
//
// // pattern to find IxTheo annotations
// private void detectIxTheoAnno(String[] lineSplit) {
// Pattern ITAnno_indicator = Pattern.compile("652");
// Matcher m = ITAnno_indicator.matcher(lineSplit[1]);
// boolean hasIxThAnno = m.find();
//
// // Fill current File with given values:
// String tag652 = lineSplit[2];
// ArrayList<String> values = new ArrayList<String>(5);
// if (hasIxThAnno) {
// // check for ixTheo Annotations
// String ixthanno = tag652.replaceAll("[a-z]", "").trim();
// // System.out.println(S);
// if (ixthanno.length() > 1) {
// // if there are several ixTheo tags split and add to file
// // and table
// if (lineSplit.length > 3) {
// // String[] ixTheoAnnoArray = ixthanno.split(" ");
// for (int i = 3; i < lineSplit.length; i++) {
// // System.out.println(i + " :" + lineSplit[i]);
// IxTheo_Annotation anno = IxTheo_Annotation
// .stringToIxTheo_Annotation(lineSplit[i].replaceAll("[^A-Z]", ""));
// currentFile.getIxTheo_anno_set().add(anno);
// currentFile.setIxTheoAnnotated(true);
// Integer j = 1;
// if (getCorpus().getIxTheoAnnoCount().containsKey(anno)) {
// j = getCorpus().getIxTheoAnnoCount().get(anno) + 1;
// }
// getCorpus().getIxTheoAnnoCount().put(anno, j);
// }
// } else {
// IxTheo_Annotation anno = IxTheo_Annotation
// .stringToIxTheo_Annotation(ixthanno.replaceAll("[^A-Z]", ""));
// currentFile.getIxTheo_anno_set().add(anno);
// currentFile.setIxTheoAnnotated(true);
// Integer j = 1;
// if (getCorpus().getIxTheoAnnoCount().containsKey(anno)) {
// j = getCorpus().getIxTheoAnnoCount().get(anno) + 1;
// }
// getCorpus().getIxTheoAnnoCount().put(anno, j);
// }
// }
// }
// // look for ixTheo
// if (lineSplit[1].equals("652"))
//
// {
// // if (ppn.equals("410388289")) {
// // System.out.println("ixTheo file TEST: " + lineSplit[1] + " " +
// // lineSplit[2]);
// // }
// }
// }
//
// /**
// * get the set of all Parameters used in this corpus
// *
// * @return
// */
// public HashSet<String> getPossibleParams() {
// return possibleParams;
// }
//
// public IxTheoCorpus getCorpus() {
// return corpus;
// }
//
// public void setCorpus(IxTheoCorpus corpus) {
// this.corpus = corpus;
// }
//
// public IxTheoCorpus deserializeCorpus(String filename) {
// IxTheoCorpus theCorpus = null;
// try {
// FileInputStream fileIn = new FileInputStream(filename);
// ObjectInputStream in = new ObjectInputStream(fileIn);
// theCorpus = (IxTheoCorpus) in.readObject();
// in.close();
// fileIn.close();
// } catch (IOException i) {
// i.printStackTrace();
// } catch (ClassNotFoundException e) {
// System.out.println("IxTheoCorpus class not found");
// e.printStackTrace();
// }
// return theCorpus;
// }
// }
