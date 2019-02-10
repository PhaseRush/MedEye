package medeye.imaging;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImageUtil {

    public static String runOCR(String fileName) throws IOException {
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            // Reads the image file into memory
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString imgBytes = ByteString.copyFrom(data);

            // Builds the image annotation request
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            requests.add(request);

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return null;
                } else {
                    String nameWithNewline = res.getFullTextAnnotation().getText();
                    return nameWithNewline.substring(1, nameWithNewline.length()-1);
                }
            }
        }
        return null;
    }

    public static List<DrugTriplet> processDrugs(DrugInfo[] drugs, String target) {
        return Arrays.stream(drugs)
                .filter(d -> d.getNdc_description().contains(target))
                .map(d -> new DrugTriplet(d.getNdc_description(), Double.valueOf(d.getNadac_per_unit()), d.getPricing_unit()))
                .sorted((o1, o2) -> {
                    if (o1.unitPrice == o2.unitPrice) return 0;
                    return (o1.unitPrice > o2.unitPrice ? 1 : -1);
                }).collect(Collectors.toList());
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
