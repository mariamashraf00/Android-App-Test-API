package com.example.instabug;

import android.content.Context;
import android.os.AsyncTask;

import com.example.instabug.utils.Response;
import com.example.instabug.utils.StringPair;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class APIClient extends AsyncTask<String,Void, Response> {
    URL requestUrl;
    String url;
    Context context;
    HttpURLConnection urlConnection;
    List<StringPair> postData, headerData;
    String method;
    Response response;
    int responseCode = HttpURLConnection.HTTP_OK;

    interface OnCompleteListener{
        void onComplete(Response result);
    }

    public OnCompleteListener delegate = null;

    APIClient(Context context, String requestUrl, String method, Response response, OnCompleteListener delegate){
        this.context = context;
        this.delegate = delegate;
        this.method = method;
        this.response=response;
        try {
            this.url=requestUrl;
            this.requestUrl = new URL(requestUrl);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    APIClient(Context context, String requestUrl, String method, List<StringPair> postData, Response response, OnCompleteListener delegate){
        this(context, requestUrl, method, response,delegate);
        this.postData = postData;
    }

    APIClient(Context context, String requestUrl, String method, List<StringPair> postData,
              List<StringPair> headerData, Response response, OnCompleteListener delegate ){
        this(context, requestUrl,method,postData,response,delegate);
        this.headerData = headerData;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Response doInBackground(String... params) {
        try {
            response = new Response();
            response.setRequestUrl(url);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            if(headerData != null) {
                for (StringPair pair : headerData) {
                    urlConnection.setRequestProperty(pair.getFirst(),pair.getSecond());
                }
            }
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod(method);
            urlConnection.connect();
            StringBuilder sb = new StringBuilder();

            if(!(method.equals("GET"))) {
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(getPostDataString(postData));
                writer.flush();
                writer.close();
                out.close();
            }

            urlConnection.connect();
            ArrayList<StringPair> responseHeaders= new ArrayList<StringPair>();
            for (int i = 0;; i++) {
                String headerName = urlConnection.getHeaderFieldKey(i);
                String headerValue = urlConnection.getHeaderField(i);
                if (headerName == null && headerValue == null)
                    break;
                responseHeaders.add(new StringPair(headerName,headerValue));
            }
            response.setResponseHeaders(responseHeaders);
            responseCode = urlConnection.getResponseCode();
            response.setResponseCode(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            response.setResponseBody(sb.toString());
            return response;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response result) {
        delegate.onComplete(result);
        super.onPostExecute(result);
    }

    private String getPostDataString(List<StringPair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(StringPair pair : params){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getFirst(),"UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getSecond(), "UTF-8"));
        }
        return result.toString();
    }
}