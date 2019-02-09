package medeye;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Utility {


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
}
