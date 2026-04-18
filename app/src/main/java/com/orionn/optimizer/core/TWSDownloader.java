package com.orionn.optimizer.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class TWSDownloader {

    public static String download(String url) {

        StringBuilder sb = new StringBuilder();

        try {

            URL u = new URL(url);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(u.openStream())
            );

            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            br.close();

        } catch (Exception e) {
        }

        return sb.toString();
    }
}
