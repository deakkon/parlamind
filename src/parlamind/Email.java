package parlamind;

import java.util.Arrays;
import java.util.HashMap;

public class Email {
    String Id;
    String ExtractedSubject;
    String ExtractedBodyText;
    String RawText;
    HashMap<String,Double> ImportantSentences;


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getExtractedSubject() {
        return ExtractedSubject;
    }

    public void setExtractedSubject(String extractedSubject) {
        ExtractedSubject = extractedSubject;
    }

    public String getExtractedBodyText() {
        return ExtractedBodyText;
    }

    public void setExtractedBodyText(String extractedBodyText) {
        ExtractedBodyText = extractedBodyText;
    }

    public String getRawText() {
        return RawText;
    }

    public void setRawText(String rawText) {
        RawText = rawText;
    }

    public HashMap<String,Double> getImportantSentences() {
        return ImportantSentences;
    }

    public void setImportantSentences(HashMap<String,Double> importantSentences) {
        ImportantSentences = importantSentences;
    }
}
