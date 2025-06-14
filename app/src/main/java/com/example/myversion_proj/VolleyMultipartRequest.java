package com.example.myversion_proj;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mHeaders = new HashMap<>();

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    public void setHeaders(Map<String, String> headers) {
        mHeaders.putAll(headers);
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return buildBody();
    }

    private static final String twoHyphens = "--";
    private static final String lineEnd = "\r\n";
    private static final String boundary = "apiclient-" + System.currentTimeMillis();

    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        return null;
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    private byte[] buildBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            // Text Params
            Map<String, String> params = getParams();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    bos.write((twoHyphens + boundary + lineEnd).getBytes());
                    bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd).getBytes());
                    bos.write((lineEnd).getBytes());
                    bos.write((entry.getValue() + lineEnd).getBytes());
                }
            }

            // File Params
            Map<String, DataPart> data = getByteData();
            if (data != null && !data.isEmpty()) {
                for (Map.Entry<String, DataPart> entry : data.entrySet()) {
                    bos.write((twoHyphens + boundary + lineEnd).getBytes());
                    bos.write(("Content-Disposition: form-data; name=\"" +
                            entry.getKey() + "\"; filename=\"" +
                            entry.getValue().getFileName() + "\"" + lineEnd).getBytes());
                    bos.write(("Content-Type: " + entry.getValue().getType() + lineEnd).getBytes());
                    bos.write((lineEnd).getBytes());

                    bos.write(entry.getValue().getContent());
                    bos.write(lineEnd.getBytes());
                }
            }

            bos.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthFailureError e) {
            throw new RuntimeException(e);
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String name, byte[] data) {
            this(name, data, "application/octet-stream");
        }

        public DataPart(String name, byte[] data, String type) {
            fileName = name;
            content = data;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}

