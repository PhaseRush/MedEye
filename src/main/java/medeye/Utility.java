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


    public static BufferedImage loadImageFromPath(String path) {
        BufferedImage buffImg = null;

        try {
            buffImg = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading image from path: " + path);
            e.printStackTrace();
        }

        return buffImg;
    }

    public static String getStringFromUrl(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();//Response response = client.newCall(request).execute()
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            //kys
            return "error - url status request - getstringfromurl";
        }
    }

    public static String omitLastNewline(String str) {
        if (str.charAt(str.length()-1) != '\n') return str;
        return str.substring(0, str.length()-1);
    }
}
