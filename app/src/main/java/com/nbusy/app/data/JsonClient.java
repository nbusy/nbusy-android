package com.nbusy.app.data;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * HTTP client with JSON content type by default.
 */
public class JsonClient {
    public <T> T getJSON(String uri, java.lang.Class<T> classOfT) {
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int status = connection.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                    reader.close();
                    Gson gson = new Gson();
                    return gson.fromJson(response.toString(), classOfT);
            }

        } catch (MalformedURLException ex) {
            //Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
