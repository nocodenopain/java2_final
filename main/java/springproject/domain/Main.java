package springproject.domain;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        readByURL("https://api.github.com/repos/21stCenturyGreensleeves/JAVA2");
    }
    public static void readByURL(String url) throws IOException, ParseException {
        URL u=new URL(url);
        URLConnection connection=u.openConnection();
        HttpURLConnection httpURLConnection=(HttpURLConnection) connection;


        int code=httpURLConnection.getResponseCode();
//        System.out.println(code+" "+httpURLConnection.getResponseMessage());
        if(code!=HttpURLConnection.HTTP_OK)
            return;



        InputStream inputStream=httpURLConnection.getInputStream();
        Scanner in = new Scanner(inputStream);
//        while (in.hasNext())
//            System.out.println(in.nextLine());

        String line = in.nextLine();
        System.out.println(line);
        JSONParser parser = new JSONParser();
        JSONObject o = (JSONObject) parser.parse(line);
        JSONArray o1 = (JSONArray) o.get("owner");
        for (Object o2 : o1) {
            JSONObject o3 = (JSONObject) o2;
            System.out.println(((JSONObject)o3.get("repo_url")).get("name"));
        }

    }
}
