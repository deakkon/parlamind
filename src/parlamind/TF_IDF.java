package parlamind;

import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import com.ibm.icu.text.BreakIterator;
import one.util.streamex.*;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;

public class TF_IDF {

    // modified from https://github.com/MyEncyclopedia/AlgImpl/tree/master/01_Mining_of_Massive_Datasets/TF_IDF
    // additional info from https://www.oreilly.com/learning/transforming-text-data
    // prob wouldnt work well for laaarge corpora due to heap size and the data structures used in the implementation (i.e. there are more efficient data structures)

    int numOfWords;
    double[] idfVector;
    double[][] tfIdfMatrix;
    double[][] tfMatrix;
    String[] wordVector;
    int docLength[];
    HashMap<String, Integer> mapWordToIdx;
    Double avgSentenceImportance;

    public static Set<String> readStopWordsFile() throws FileNotFoundException, IOException {
        String fileStopWords = "stopWords.txt";

        Set<String> stopWords = new LinkedHashSet<String>();
        FileReader readFileStopWord = new FileReader(fileStopWords);
        BufferedReader stopWordsFile = new BufferedReader(readFileStopWord);

        String line;

        while ((line = stopWordsFile.readLine()) != null) {
            line = line.trim();
            stopWords.add(line);
        }
        stopWordsFile.close();
        return stopWords;
    }


    public HashMap<String, Integer> tokenize_corpus(ArrayList<String> docs) throws Exception {
        // public void tokenize_corpus(ArrayList<String> docs) throws Exception{

        // STEP 1, scan all words and count the number of different words
        // mapWordToIdx maps word to its vector index
        HashMap<String, Integer> mapWordToIdx = new HashMap<>();
        Set<String> stopWords = readStopWordsFile();
        int nextIdx = 0;

        for (String doc : docs) {
            //System.out.println(doc);

            for (String word : doc.split(" ")) {
                // System.out.println(word);
                String tmp_word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase();
                // System.out.println(tmp_word);
                if (stopWords.contains(tmp_word)) {
                    // System.out.println("Skipped:\t" + tmp_word);
                    continue; // skip over a stopword token
                } else if ((tmp_word != null) && (!mapWordToIdx.containsKey(tmp_word)) && (	tmp_word.length() > 1)) {
                    mapWordToIdx.put(tmp_word, nextIdx);
                    nextIdx++;
                    // System.out.println("Good:\t" + tmp_word);
                }
            }
            // System.out.println("----");
        }
        return mapWordToIdx;
    }

    public int[] getDocCountVector(ArrayList<String> docs) throws Exception {

        // HashMap<String, Integer> mapWordToIdx = getMapWordToIdx();
        numOfWords = mapWordToIdx.size();
        System.out.println(numOfWords);
        int[] docCountVector = new int[numOfWords];
        docLength = new int[docs.size()];
        // lastDocWordVector is auxilary vector keeping track of last doc index
        // containing the word
        int[] lastDocWordVector = new int[numOfWords];
        for (int wordIdx = 0; wordIdx < numOfWords; wordIdx++) {
            lastDocWordVector[wordIdx] = -1;
        }
        for (int docIdx = 0; docIdx < docs.size(); docIdx++) {
            //String doc = docs[docIdx];
            String doc = docs.get(docIdx);
            String[] words = doc.split(" ");
            for (String word : words) {
                docLength[docIdx] = words.length;
                String tmp_word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase();

                try {
                    int wordIdx = mapWordToIdx.get(tmp_word);
                    if (lastDocWordVector[wordIdx] < docIdx) {
                        lastDocWordVector[wordIdx] = docIdx;
                        docCountVector[wordIdx]++;
                    }
                }catch(NullPointerException e){
                            continue;
                        }
                    }
                }
                return docCountVector;
            }

    public TF_IDF(ArrayList<String> docs) throws Exception{

        // STEP 1, scan all words and count the number of different words
        // mapWordToIdx maps word to its vector index

        mapWordToIdx = tokenize_corpus(docs);
        numOfWords = mapWordToIdx.size();

        // STEP 2, create word vector where wordVector[i] is the actual word
        wordVector = new String[numOfWords];
        for (String word : mapWordToIdx.keySet()) {
            int wordIdx = mapWordToIdx.get(word);
            wordVector[wordIdx] = word;
        }

        // STEP 3, create doc word vector where docCountVector[i] is number of
        // docs containing word of index i
        // and doc length vector
        // int[] docCountVector = getDocCountVector(docs, mapWordToIdx);
        int[] docCountVector = getDocCountVector(docs);

        // STEP 4, compute IDF vector based on docCountVector
        idfVector = new double[numOfWords];
        for (int wordIdx = 0; wordIdx < numOfWords; wordIdx++) {
            idfVector[wordIdx] = Math.log10(1 + (double) docs.size() / (docCountVector[wordIdx]));
        }

        // STEP 5, compute term frequency matrix, tfMatrix[docIdx][wordIdx]
        tfMatrix = new double[docs.size()][];
        for (int docIdx = 0; docIdx < docs.size(); docIdx++) {
            tfMatrix[docIdx] = new double[numOfWords];
        }
        for (int docIdx = 0; docIdx < docs.size(); docIdx++) {
            String doc = docs.get(docIdx);
            for (String word : doc.split(" ")) {
                String tmp_word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase();

                try{
                    int wordIdx = mapWordToIdx.get(tmp_word);
                    tfMatrix[docIdx][wordIdx] = tfMatrix[docIdx][wordIdx] + 1;
                }catch (NullPointerException e) {
                    continue;
                }
            }
        }
        // normalize idfMatrix by deviding corresponding doc length
        for (int docIdx = 0; docIdx < docs.size(); docIdx++) {
            for (int wordIdx = 0; wordIdx < numOfWords; wordIdx++) {
                tfMatrix[docIdx][wordIdx] = tfMatrix[docIdx][wordIdx] / docLength[docIdx];
            }
        }

        // STEP 6, compute final TF-IDF matrix
        // tfIdfMatrix[docIdx][wordIdx] = tfMatrix[docIdx][wordIdx] * idfVector[wordIdx]
        tfIdfMatrix = new double[docs.size()][];
        for (int docIdx = 0; docIdx < docs.size(); docIdx++) {
            tfIdfMatrix[docIdx] = new double[numOfWords];
        }

        for (int docIdx = 0; docIdx < docs.size(); docIdx++) {
            for (int wordIdx = 0; wordIdx < numOfWords; wordIdx++) {
                tfIdfMatrix[docIdx][wordIdx] = tfMatrix[docIdx][wordIdx] * idfVector[wordIdx];
            }
        }
    }


    public List<Map.Entry<Integer,Double>> importantTermsCorpus(double[][] tfidfMatrix) throws Exception{

        //map token -> average tf-idf in corpus
        HashMap<Integer, Double> map = new HashMap<Integer, Double>();

        // coprus and feature space size
        int numRows = tfidfMatrix.length;
        int nr_columns = tfidfMatrix[0].length;

        //get average for individual features
        // dividing by nr docs in with feature
        for (int col = 0; col < nr_columns; col++) {
            List<Double> column_values = new ArrayList<Double>();
            for (int row = 0; row < numRows; row++) {

                Double tmp_val = tfidfMatrix[row][col];

                if (tmp_val > 0) {
                    //System.out.println(tfidfMatrix[row][col]);
                    column_values.add(tfidfMatrix[row][col]);
                }
            }

            // System.out.println(column_values.stream().mapToDouble(a -> a).average().getAsDouble());
            map.put(col, column_values.stream().mapToDouble(a -> a).average().getAsDouble());
        }

        // sort by value in hashmap by key and return all with value above mean of all values -> important tokens in corpora
        Map<Integer, Double> sortedMap =
                map.entrySet()
                        .stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(
                                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                        LinkedHashMap::new));
        List<Map.Entry<Integer,Double>> firstN =
                sortedMap.entrySet().stream().limit(10).collect(Collectors.toList());

        // System.out.println(firstN);
        // System.out.println(firstN.getClass());
        return firstN;
    }

    public Email[] importantSentences() throws Exception{
        //accepts single document
        // splits document in senteces
        // tokenizes senteces
        // gets wordIDs from mapper
        // looks up individual word tf-idf
        // calucalted mean of tf-idf values and addes sentence in array if tfidf_mean > threshold

        Email[] emails = ParseJSON.parseJSON();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        List<Double> allImportance = new ArrayList<>();
        int docIdx = 0;

        for (final Email email : emails) {

            HashMap<String, Double> importantSentences = new HashMap<String, Double>();

            String doc = new String(email.getExtractedBodyText() + email.getExtractedSubject());
            iterator.setText(doc);
            int start = iterator.first();


            for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
                Double sumTfIdf = new Double(0);
                List<Double> intList = new ArrayList<Double>();
                String sentence = doc.substring(start,end);
                // System.out.println(sentence);

                for (String word : sentence.split(" ")) {
                    String tmp_word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase();
                    if (mapWordToIdx.containsKey(tmp_word)) {
                        // System.out.println(tmp_word);
                        int wordIdx = mapWordToIdx.get(tmp_word);
                        // System.out.println(wordIdx);
                        // System.out.println(tfIdfMatrix[docIdx][wordIdx]);
                        // sumTfIdf += tfIdfMatrix[docIdx][wordIdx];
                        intList.add(tfIdfMatrix[docIdx][wordIdx]);
                        }
                    }

                try{
/*                    System.out.println(docIdx);
                    System.out.println(sentence);
                    System.out.println(intList.size());
                    System.out.println(intList.stream().mapToDouble(val -> val).average().getAsDouble());
                    System.out.println("------");*/
                    if (intList.size()>4) {
                        Double sentImportance = intList.stream().mapToDouble(val -> val).average().getAsDouble();
                        allImportance.add(sentImportance);
                        importantSentences.put(sentence, sentImportance);
                    }
                } catch(NoSuchElementException e) {
                    continue;
                    }
            }

            email.setImportantSentences(importantSentences);

            docIdx++;
        }

        // System.out.println(allImportance.stream().mapToDouble(val -> val).average().getAsDouble());
        avgSentenceImportance = allImportance.stream().mapToDouble(val -> val).average().getAsDouble();
        // System.out.println(emails);

        for (final Email email : emails) {
            // System.out.println(email.ImportantSentences);
            HashMap<String, Double> tmpSent = email.ImportantSentences;
            HashMap<String, Double> output = tmpSent.
                    entrySet()
                    .stream()
                    .collect(
                            HashMap::new,
                            (map,e)->{ Double i=e.getValue(); if(i>avgSentenceImportance) map.put(e.getKey(), i); },
                            Map::putAll);
            email.ImportantSentences = output;
        }

        return emails;

    }

    public double[][] getTF_IDFMatrix()  throws Exception{
        return tfIdfMatrix;
    }

    public String[] getWordVector()  throws Exception{
        return wordVector;
    }

    public HashMap<String, Integer> getMapWordToIdx() {
        return mapWordToIdx;
    }



}