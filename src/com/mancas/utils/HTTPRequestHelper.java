package com.mancas.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.util.Log;

/**
 * Helper class to perform HTTP request operations
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class HTTPRequestHelper
{
    /**
     * Tag for indicating an POST operation
     */
    private static final int POST_TYPE = 1;
    /**
     * Tag for indicating an GET operation
     */
    private static final int GET_TYPE = 2;
    /**
     * Tag for indicating the content type of the operation
     */
    private static final String CONTENT_TYPE = "Content-Type";
    /**
     * Tag for indicating the content type is a JSON application
     */
    private static final String JSON_APPLICATION = "application/json";

    private static final String TAG = "HTTPRequestHelper";
    /**
     * Parameters to send with the HTTP request
     */
    private JSONObject mParams;
    /**
     * Handler class that will manage the response from server
     */
    private HTTPResponseCallback mCallbacks;

    /**
     * Constructor. Initialize a new object
     * @param params parameters to send with the HTTP request
     */
    public HTTPRequestHelper(JSONObject params, HTTPResponseCallback callbacks)
    {
        Log.d(TAG, "New object http");
        mParams = params;
        mCallbacks = callbacks;
    }

    /**
     * Performs a GET request
     * @param url
     */
    public void performGet(String url)
    {
        performRequest(url, HTTPRequestHelper.GET_TYPE);
    }

    /**
     * Performs a POST request
     * @param url
     */
    public void performPost(String url)
    {
        performRequest(url, HTTPRequestHelper.POST_TYPE);
    }

    /**
     * Private method that perform the request depending on the requestType argument
     * @param url
     * @param requestType type of the request (POST/GET/PUT/DELETE)
     */
    private void performRequest(String url, int requestType)
    {
        DefaultHttpClient client = new DefaultHttpClient();

        switch (requestType) {
        case HTTPRequestHelper.GET_TYPE:
            HttpGet get = new HttpGet(url);
            execute(client, get);
            break;
        case HTTPRequestHelper.POST_TYPE:
            HttpPost post = new HttpPost(url);
            try {
                post.setEntity(new StringEntity(mParams.toString()));
                post.setHeader(new BasicHeader(CONTENT_TYPE, JSON_APPLICATION));
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            execute(client, post);
            break;
        }
    }

    private void execute(HttpClient client, HttpRequestBase method)
    {
        try {
            HttpResponse response = client.execute(method);
            String parseData = parseResponse(response);
            if (mCallbacks != null) {
                mCallbacks.onResponseReady(parseData);
            }
        } catch (ClientProtocolException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Method that parse the JSON received from the REST server
     * @param response the HTTP response from the server
     * @return an string with the response, or an empty string if something went wrong
     */
    private String parseResponse(HttpResponse response) {
        //The status line returned from the HTTP server
        StatusLine status = response.getStatusLine();
        //The response returned from the HTTP server
        HttpEntity entity = response.getEntity();
        String responseResult = "";
        if (entity != null) {
            try {
                responseResult = inputStreamToString(entity.getContent());
            } catch (ClientProtocolException e) {
                Log.e(TAG, e.getMessage());
                return responseResult;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return responseResult;
            }
        }

        return responseResult;
    }

    /**
     * Converts an InputStream object into a String
     * @param stream
     * @return a string with all the content present in the stream argument
     * @throws IOException
     */
    public static String inputStreamToString(final InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        return sb.toString();
    }

    public static interface HTTPResponseCallback {
        public void onResponseReady(String response);
    }
}
