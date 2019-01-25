package parlamind;

import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.*;
import java.util.stream.Collectors;
import java.util.Map;
import java.io.*;

public class Main {

    String[] documents = null;

    public static void main(String[] args)  throws Exception{


        ArrayList<String> raw_text = ParseJSON.getDocuments();

        TF_IDF tfidf = new TF_IDF(raw_text);
        double[][] tfidfMatrix = tfidf.getTF_IDFMatrix();

        // DICTIONARIES; WOR TO IDX AND IDX TO WORD
        // USED FOR LOOKUP OF MOST IMPORTANT TOKENS BASED ON TF IDF
        HashMap<String, Integer> mapWordToIdx = tfidf.getMapWordToIdx();
        // Map<Integer, String> mapIdxToWord = mapWordToIdx.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        // tf idf values for most important terms
        // most important == all tokens with tf-idf > mean(tf-idf)
        tfidf.importantTermsCorpus(tfidfMatrix);

        //get important senteces in loop, add properties to Email objects
        tfidf.importantSentences();

        //processed emails
        Email[] processedEmails = tfidf.importantSentences();

        for (final Email email : processedEmails) {
            System.out.println(email.Id);
            System.out.println(email.ExtractedSubject);
            System.out.println(email.ExtractedBodyText);
            System.out.println(email.ImportantSentences);
            System.out.println("---------------------");
        }

        try (Writer writer = new FileWriter("preprocessedEmail.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(processedEmails, writer);
        }
    }
}