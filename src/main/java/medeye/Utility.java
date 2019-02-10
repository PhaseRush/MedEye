package medeye;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Utility {

    private static OkHttpClient client = new OkHttpClient();
    public static Gson gson = new Gson();


    // probs dont need anymore because abstracted in google code
    public static BufferedImage loadImageFromPath(String path) {
        BufferedImage bufferedImage = null;

        try {
            bufferedImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading image from path: " + path);
            e.printStackTrace();
        }

        return bufferedImage;
    }

    public static String getStringFromUrl(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "error - url status request - getstringfromurl";
        }
    }

    public static String omitLastNewline(String str) {
        if (str.charAt(str.length()-1) != '\n') return str; // if not newline just return original
        return str.substring(0, str.length()-1); //return without last char
    }
}
