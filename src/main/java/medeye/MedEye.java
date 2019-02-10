package medeye;

import com.google.cloud.vision.v1.*;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import javafx.util.Pair;
import medeye.imaging.DrugInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        System.out.println("TARGET: (ASPIRIN) " + targetDrugName);

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //String targetDrugName = "ASPIRIN";



        Arrays.stream(drugs)
                .filter(d -> d.getNdc_description().contains(parsedTarget))
                .map(d -> new Pair<>(d.getNdc_description(), Double.valueOf(d.getNadac_per_unit())))
                .sorted((o1, o2) -> {
                    if (o1.getValue().equals(o2.getValue())) return 0;
                    return (o1.getValue() > o2.getValue() ? 1 : -1);
                }).forEach(p ->
                System.out.println(p.getKey() + " : " + p.getValue()));
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
