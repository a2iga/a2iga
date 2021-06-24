// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public static String get(Context context, String urlStr) {

        HttpURLConnection urlConnection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;

        try {

            URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            is = urlConnection.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            StringBuilder strBuilder = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                strBuilder.append(line);
            }

            result = strBuilder.toString();

        } catch (Exception e) {
            AppUtils.Log(context, "e", "42/HttpUtils.get: " + e);
        } finally {

            if (br != null) {

                try {
                    br.close();
                } catch (IOException ignored) {
					AppUtils.Log(context, "e", "50/HttpUtils.get: " + ignored);
				}

            }

            if (is != null) {

                try {
                    is.close();
                } catch (IOException ignored) {
					AppUtils.Log(context, "e", "60/HttpUtils.get: " + ignored);
				}

            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

        }

        return result;

    }

}
