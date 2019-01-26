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
        tfidf.getMapWordToIdx();

        // tf idf values for most important terms
        // most important == all tokens with tf-idf > mean(tf-idf)
        HashMap<String, Double> calcTopWords = tfidf.importantTermsCorpus(tfidfMatrix);
        try (Writer writer = new FileWriter("data/out/topTokens.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(calcTopWords, writer);
        }

        //processed emails, use mean of sentence level tfidf values for all token in corpus
        Email[] processedEmails = tfidf.importantSentences(false);
        try (Writer writer = new FileWriter("data/out/preprocessedEmail_avgSentences.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(processedEmails, writer);
        }

        //processed emails, use mean of sentence level tfidf values for top tokens in corpus
        Email[] processedEmailsTopTokens = tfidf.importantSentences(true);
        try (Writer writer = new FileWriter("data/out/preprocessedEmail_topTokens.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(processedEmailsTopTokens, writer);
        }
    }
}