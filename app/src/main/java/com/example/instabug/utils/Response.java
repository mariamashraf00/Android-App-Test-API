package com.example.instabug.utils;

import java.util.ArrayList;

public class Response {
    protected int responseCode=0;
    protected String responseBody="";
    ArrayList<StringPair> responseHeaders = new ArrayList<StringPair>();
    protected String requestUrl="";

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Response() {
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setResponseHeaders(ArrayList<StringPair> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Response(int code, String responseBody, ArrayList<StringPair> responseHeaders, String requestUrl) {
        this.responseCode = code;
        this.responseBody = responseBody;
        this.responseHeaders = responseHeaders;
        this.requestUrl = requestUrl;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public ArrayList<StringPair> getResponseHeaders() {
        return responseHeaders;
    }

    public String getRequestUrl() {
        return requestUrl;
    }
}
