//package medeye.imaging;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.services.vision.v1.Vision;
//import com.google.api.services.vision.v1.VisionScopes;
//import com.google.api.services.vision.v1.model.*;
//import com.google.common.base.MoreObjects;
//import com.google.common.collect.ImmutableList;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.security.GeneralSecurityException;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Class for doing OCR using Google Cloud
// *
// * https://github.com/GoogleCloudPlatform/java-docs-samples/blob/master/vision/text/src/main/java/com/google/cloud/vision/samples/text/TextApp.java
// */
//public class GoogleOCR {
//    private static final int MAX_RESULTS = 6;
//    private static final int BATCH_SIZE = 10;
//    private static Vision vision;
//
//    public GoogleOCR(Vision vision) {
//        GoogleOCR.vision = vision;
//    }
//
//
//    public static Vision getVisionService() throws IOException, GeneralSecurityException {
//        GoogleCredential credential =
//                GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
//        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
//                .setApplicationName("MedEye/1.0")
//                .build();
//    }
//
//    public static List<ImageText> runOCR (Path filePath) throws IOException {
//
//        List<Path> paths =
//                Files.walk(filePath)
//                        .filter(Files::isRegularFile)
//                        .collect(Collectors.toList());
//
//        List<ImageText> processed = detectText(paths).stream()
//                .filter(GoogleOCR::successfullyDetectedText).collect(Collectors.toList()); // filter out unprocessed images
//
//
//
//
//        return processed;
//    }
//
//    public static ImmutableList<ImageText> detectText(List<Path> paths) {
//        ImmutableList.Builder<AnnotateImageRequest> requests = ImmutableList.builder();
//        try {
//            for (Path path : paths) {
//                byte[] data;
//                data = Files.readAllBytes(path);
//                requests.add(
//                        new AnnotateImageRequest()
//                                .setImage(new Image().encodeContent(data))
//                                .setFeatures(ImmutableList.of(
//                                        new Feature()
//                                                .setType("TEXT_DETECTION")
//                                                .setMaxResults(MAX_RESULTS))));
//            }
//
//            Vision.Images.Annotate annotate =
//                    vision.images()
//                            .annotate(new BatchAnnotateImagesRequest().setRequests(requests.build()));
//            // Due to a bug: requests to Vision API containing large images fail when GZipped.
//            annotate.setDisableGZipContent(true);
//            BatchAnnotateImagesResponse batchResponse = annotate.execute();
//            assert batchResponse.getResponses().size() == paths.size();
//
//            ImmutableList.Builder<ImageText> output = ImmutableList.builder();
//            for (int i = 0; i < paths.size(); i++) {
//                Path path = paths.get(i);
//                AnnotateImageResponse response = batchResponse.getResponses().get(i);
//                output.add(
//                        ImageText.builder()
//                                .path(path)
//                                .textAnnotations(
//                                        MoreObjects.firstNonNull(
//                                                response.getTextAnnotations(),
//                                                ImmutableList.<EntityAnnotation>of()))
//                                .error(response.getError())
//                                .build());
//            }
//            return output.build();
//        } catch (IOException ex) {
//            // Got an exception, which means the whole batch had an error.
//            ImmutableList.Builder<ImageText> output = ImmutableList.builder();
//            for (Path path : paths) {
//                output.add(
//                        ImageText.builder()
//                                .path(path)
//                                .textAnnotations(ImmutableList.<EntityAnnotation>of())
//                                .error(new Status().setMessage(ex.getMessage()))
//                                .build());
//            }
//            return output.build();
//        }
//    }
//
//    private static boolean successfullyDetectedText(ImageText image) {
//        if (image.error() != null) {
//            System.out.printf("Error reading %s:\n%s\n", image.path(), image.error().getMessage());
//            return false;
//        }
//        return true;
//    }
//
//}
