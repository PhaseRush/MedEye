package medeye;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

public class Utility {

    private static OkHttpClient client = new OkHttpClient();
    public static Gson gson = new Gson();

    /**
     * Sort a map by value. Can specify increasing or decreasing
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMap(Map<K, V> map, boolean smallestToLargest) {
        List<Map.Entry<K, V>> entryList = new ArrayList<>(map.entrySet());
        entryList.sort(Map.Entry.comparingByValue());
        if (!smallestToLargest) Collections.reverse(entryList);

        Map<K, V> result = new LinkedHashMap<>();

        entryList.forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

    /**
     * Given String url, fetch the String on page
     * @param url target URL
     * @return String contained on that page
     */
    public static String getStringFromUrl(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error in Utility#getStringFromUrl";
        }
    }

    /**
     * Deletes newline character at end of String, else ignore
     * @param str input
     * @return input without newLine at end
     */
    public static String omitLastNewline(String str) {
        if (str.charAt(str.length()-1) != '\n') return str; // if not newline just return original
        return str.substring(0, str.length()-1); //return without last char
    }

    /**
     * turns a string to proper capitalization
     * assume at least length 1
     * @param str
     * @return
     */
    public static String properCapital(String str) {
        str = str.toLowerCase();
        char[] charray = str.toCharArray();

        for (int i = 1; i < charray.length; i++) {
            if (charray[i-1] == ' ') {
                charray[i] -= 32;
            }
        }
        return new String(charray);
    }

    public static void padding(int lines, String message) {
        for (int i = 1; i < lines; i++) System.out.println();
        System.out.println("--------: " + message + " :--------");
    }
}
