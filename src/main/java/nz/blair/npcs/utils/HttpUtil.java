package nz.blair.npcs.utils;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    /**
     * Sends a GET request to the specified URL and returns the response.
     *
     * @param url The URL to send the GET request to.
     * @return The response from the GET request.
     */
    @Nullable
    public static String get(String url) {
        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            // Close the connection
            in.close();
            connection.disconnect();

            return stringBuilder.toString();
        } catch (IOException e) {
            LoggerUtil.warning("Failed to get from " + url, e);
        }

        return null;
    }
}
