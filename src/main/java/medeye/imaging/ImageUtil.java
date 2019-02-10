package medeye.imaging;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import medeye.Utility;

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
                    return Utility.omitLastNewline(
                            res.getTextAnnotationsList().stream()
                                    .sorted((o1, o2) -> {
                                        System.out.println("o1 desc: " + o1.getDescription());
                                        System.out.println("o2 desc: " + o2.getDescription());

                                        int o1Size = calcArea(o1.getBoundingPoly().getVerticesList());
                                        int o2Size = calcArea(o2.getBoundingPoly().getVerticesList());
                                        if (o1Size == o2Size) return 0;
                                        return (o1Size > o2Size ? -1 : +1); // want DECREASING order
                                    })
                                    .filter(entity -> entity.getDescription().length() < 100) // eliminate extra large boxes
                                    .findFirst()
                                    .get()
                                    .getDescription()
                    );
                }
            }
        }
        return null;
    }

    private static int calcArea(List<Vertex> verticies) {
        // print out verticies
        //verticies.forEach(v -> System.out.println(v.getX() + " : " + v.getY()));

        int area = 0;         // Accumulates area in the loop
        int j = verticies.size()-1;  // The last vertex is the 'previous' one to the first

        for (int i = 0; i < verticies.size(); i++) {
            Vertex vi = verticies.get(i);
            Vertex vj = verticies.get(j);
            area += (vj.getX() + vi.getX()) * (vj.getY()- vi.getY());
            j = i;  //j is previous vertex to i
        }
        System.out.println("area: " + -1 *area/2);
        return -1*area/2;
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
