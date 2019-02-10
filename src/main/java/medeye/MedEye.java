package medeye;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import medeye.imaging.DrugInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MedEye {


    public static void main(String[] args) throws IOException {
        // https://cloud.google.com/docs/authentication/production#auth-cloud-implicit-java
        // Instantiates a client


        String json = Utility.getStringFromUrl("https://data.medicaid.gov/resource/tau9-gfwr.json");
        DrugInfo[] drugs = Utility.gson.fromJson(json, DrugInfo[].class);

        // The path to the image file to annotate
        String fileName = "MedEye_Images/aspirin.png";
        String targetDrugName = runOCR(fileName);
        String parsedTarget = targetDrugName.substring(0, targetDrugName.length()-1);
        System.out.println("TARGET: " + targetDrugName);

        List<DrugTriplet> processedDrugs = processDrugs(drugs, parsedTarget);

        processedDrugs.forEach(pd -> System.out.println(pd.name + "\n$" + pd.unitPrice + " / " + pd.unit));

    }

    private static List<DrugTriplet> processDrugs(DrugInfo[] drugs, String target) {
        return Arrays.stream(drugs)
                .filter(d -> d.getNdc_description().contains(target))
                .map(d -> new DrugTriplet(d.getNdc_description(), Double.valueOf(d.getNadac_per_unit()), d.getPricing_unit()))
                .sorted((o1, o2) -> {
                    if (o1.unitPrice == o2.unitPrice) return 0;
                    return (o1.unitPrice > o2.unitPrice ? 1 : -1);
                }).collect(Collectors.toList());
    }

    private static class DrugTriplet {
        String name;
        double unitPrice;
        String unit;

        public DrugTriplet(String name, double unitPrice, String unit){
            this.name = name;
            this.unitPrice = unitPrice;
            this.unit = unit;
        }
    }

    private static String runOCR(String fileName) throws IOException {
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
                    //System.out.println(res.getFullTextAnnotation().getText());
                    return res.getFullTextAnnotation().getText();
                }
            }
        }
        return null;
    }
}
