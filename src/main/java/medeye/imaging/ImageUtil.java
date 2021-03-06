package medeye.imaging;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import medeye.Utility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for Image processing. Includes impl. for interacting with Google's Cloud platform.
 */
public class ImageUtil {

    /**
     * Runs Optical Character Recognition (OCR) on a local image through Google's Cloud vision api
     * First reads image from path and stores it as a ByteString. Then builds a AnnotateImageRequest which is used to
     * obtain the responses from the API.
     *
     * These responses are then sorted based on their bounding box, then filtered for text content.
     * The text in the largest box which MUST HAVE LESS THAN 10 (TEN) words is returned.
     *
     * Based off of https://cloud.google.com/docs/authentication/production#auth-cloud-implicit-java
     * @param fileName (relative) filepath to target image
     * @return String of the drug name in the image.
     * @throws IOException filepath error
     */
    public static String runOCR(String fileName) throws IOException {
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            // Reads the image file into memory
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString imgBytes = ByteString.copyFrom(data);

            // Builds the image annotation requests with image and the OCR feature
            List<AnnotateImageRequest> requests = new ArrayList<>(Collections.singletonList(
                    AnnotateImageRequest.newBuilder()
                            .addFeatures(Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build())
                            .setImage(Image.newBuilder().setContent(imgBytes).build())
                            .build()
            ));

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            // Interpret responses. Although for loop, should succeed on first AnnotateImageResponse obj.
            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) { // if error, just print and continue
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                } else {
                    return Utility.omitLastNewline(
                            res.getTextAnnotationsList().stream()
                                    .sorted((o1, o2) -> {
                                        int o1Size = calcArea(o1.getBoundingPoly().getVerticesList());
                                        int o2Size = calcArea(o2.getBoundingPoly().getVerticesList());
                                        if (o1Size == o2Size) return 0;
                                        return (o1Size > o2Size ? -1 : +1); // want DECREASING order
                                    })
                                    .filter(entity -> entity.getDescription().split("\\W+").length < 10) // eliminate extra large boxes by limiting word count
                                    .findFirst()
                                    .get()
                                    .getDescription()
                    );
                }
            }
        }
        return null; // should never reach
    }

    /**
     * Given a list of vertices, calculate the enclosed area.
     * Assumes no overlapping. Works with convex, but not guaranteed for concave (bounding box should never be concave)
     *
     * @param vertices Collection of vertices which enclose a shape
     * @return area of enclosed shape
     */
    private static int calcArea(List<Vertex> vertices) {
        int area = 0; // area accumulator
        int j = vertices.size()-1;  // init j

        for (int i = 0; i < vertices.size(); i++) {
            Vertex vi = vertices.get(i);
            Vertex vj = vertices.get(j);
            area += (vj.getX() + vi.getX()) * (vj.getY()- vi.getY());
            j = i;  //update j
        }
        //System.out.println("area: " + -1 *area/2);
        return -1*area/2;
    }


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

    // wrapper class for 3-field drug object
    public static class DrugTriplet {
        private String name;
        private double unitPrice;
        private String unit;

        public DrugTriplet(String name, double unitPrice, String unit){
            this.name = name;
            this.unitPrice = unitPrice;
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public String getUnit() {
            return unit;
        }
    }
}
