package com.spreys.lesmillsnz.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

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

    public static JSONObject GetJsonFromUrl(String url){
        DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-type", "application/json");

        InputStream inputStream = null;
        String result = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "Unable to get page from URL");
            return null;
        }
        finally {
            try{
                if(inputStream != null){
                    inputStream.close();
                }
            } catch(Exception squish) {
                Log.e(TAG, "Unable to parse webservice response");
            }
        }

        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse webservice response to JSON");
            return null;
        }
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
