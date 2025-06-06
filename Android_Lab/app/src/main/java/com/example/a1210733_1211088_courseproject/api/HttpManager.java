package com.example.a1210733_1211088_courseproject.api;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpManager {
    private static final String TAG = "HttpManager";

    public static String getData(String URL) {
        BufferedReader bufferedReader = null;
        try {
            Log.d(TAG, "Making HTTP request to: " + URL);
            URL url = new URL(URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line + '\n');
                line = bufferedReader.readLine();
            }
            String result = stringBuilder.toString();
            Log.d(TAG, "HTTP request successful, response length: " + result.length());
            return result;
        } catch (Exception ex) {
            Log.e(TAG, "Error in HTTP request: " + ex.toString(), ex);
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing reader: " + e.toString());
                }
            }
        }
    }
}
