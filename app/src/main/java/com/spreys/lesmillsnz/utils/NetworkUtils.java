package com.spreys.lesmillsnz.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 30/09/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    public static final String REQUEST_POST = "POST";
    public static final String REQUEST_DELETE = "DELETE";

    public static JSONObject GetJsonFromUrl(String url, HashMap<String, String> properties) {
        try {
            String apiResponse = GetStringFromUrl(url, properties);
            if (apiResponse != null) {
                return new JSONObject(GetStringFromUrl(url, properties));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String GetStringFromUrl(String url, HashMap<String, String> properties) {
        URL myURL;
        try {
            myURL = new URL(url);
            URLConnection conn = myURL.openConnection();
            for(Map.Entry<String, String> parameter: properties.entrySet()){
                conn.setRequestProperty(parameter.getKey(), parameter.getValue());
            }
            conn.setDoOutput(false);
            conn.setDoInput(true);

            InputStream is = conn.getInputStream();
            return ConvertStreamToString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String ConvertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * Method can make DELETE and POST request
     * @param url address of the rest API
     * @param requestType at the moment only DELETE and POST supported
     * @param parameters list of parameters for the POST request
     * @throws Exception method throws multiple exceptions which needs to be handled
     */
    public static void MakeHttpRequest(String url, String requestType, HashMap<String, String> parameters)
            throws Exception {

        if(requestType.equals(REQUEST_DELETE)){
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod(REQUEST_DELETE);
            con.setDoOutput(true);

            int responseCode = con.getResponseCode();
            if(responseCode != 200){
                throw new Exception("API Returned 404");
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            System.out.println("Response code : " + responseCode);
            System.out.println(response.toString());
        } else if (requestType.equals(REQUEST_POST)) {
            Document doc = Jsoup.connect(url).data(parameters).ignoreContentType(true).post();
            System.out.println(doc.text());
        }
    }
}
