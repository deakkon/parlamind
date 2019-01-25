package parlamind;

import java.io.FileReader;
import com.google.gson.Gson;
import java.util.ArrayList;

//import parlamind.Email;

public class ParseJSON
{



    public static Email[] parseJSON() throws Exception
    {
        Gson gson = new Gson();
        Email[] emails = gson.fromJson(new FileReader("hillary_emails_subset.json"), Email[].class);

        return emails;
    }

    public static ArrayList<String> getDocuments()  throws Exception{

        Email[] emails = ParseJSON.parseJSON();
        ArrayList<String> documents =  new ArrayList<String>();

        for (final Email email : emails) {

        /*    documents.add(email.getRawText());
            documents.add(email.getExtractedBodyText());
            documents.add(email.getExtractedSubject());
        */

            documents.add(
                    email.getExtractedBodyText() + email.getExtractedSubject()
            );

        }

        return documents;
    }


}