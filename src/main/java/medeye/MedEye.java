package medeye;

import java.awt.image.BufferedImage;

public class MedEye {

    public static void main(String[] args) {
        // load file

        String path = "MedEye_Images/test1.png";
        BufferedImage helloImg = Utility.loadImageFromPath(path);

        System.out.println("Height x Width: " + helloImg.getHeight() + " x " + helloImg.getWidth());
    }
}
