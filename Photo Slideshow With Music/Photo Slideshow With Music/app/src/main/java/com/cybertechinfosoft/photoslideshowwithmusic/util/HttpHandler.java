package com.cybertechinfosoft.photoslideshowwithmusic.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpHandler {
    private static final String TAG = "HttpHandler";

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
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

    public String makeServiceCall(String convertStreamToString) {
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(convertStreamToString).openConnection();
            httpURLConnection.setRequestMethod("GET");
            convertStreamToString = this.convertStreamToString(new BufferedInputStream(httpURLConnection.getInputStream()));
            return convertStreamToString;
        } catch (ProtocolException ex3) {
            final String tag3 = HttpHandler.TAG;
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("ProtocolException: ");
            sb3.append(ex3.getMessage());
        } catch (MalformedURLException ex4) {
            final String tag4 = HttpHandler.TAG;
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("MalformedURLException: ");
            sb4.append(ex4.getMessage());
        } catch (Exception ex) {
            final String tag = HttpHandler.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("Exception: ");
            sb.append(ex.getMessage());
        }
        return null;
    }
}
