package medeye;

import medeye.imaging.DrugInfo;
import medeye.imaging.ImageUtil;
import medeye.medical.DrugSimilarity;
import medeye.medical.DrugUtil;

import java.io.IOException;

public class MedEye {
    private static final String DRUG_NAME_DATABASE_URL = "https://data.medicaid.gov/resource/tau9-gfwr.json";
    private static final String BASE_IMAGE_DIR = "MedEye_Images/";

    public static void main(String[] args) throws IOException {
        // Initializes the drug vocabulary database
        DrugInfo[] drugs = Utility.gson.fromJson(Utility.getStringFromUrl(DRUG_NAME_DATABASE_URL), DrugInfo[].class);

        // Sets path to the image file to annotate
        String fileName = BASE_IMAGE_DIR + "ibu4.png";

        // Runs Optical Character Recognition (OCR) on the image file to determine the target drug name
        String targetDrugName = Utility.omitLastNewline(ImageUtil.runOCR(fileName)).toUpperCase();

        System.out.println("TARGET: " + targetDrugName); // debug testing

        // Processes our drugs by applying filters, and sorting matching drugs by unit price
        DrugUtil.processDrugs(drugs, targetDrugName)
                .forEach(drug -> System.out.println(drug.getName() + "\n$" + drug.getUnitPrice() + " / " + drug.getUnit()));

        // Outputs top 10 similar drugs (that isn't itself)
        DrugSimilarity.getSimilar(targetDrugName)
                .forEach(p -> System.out.println("Name: " + Utility.properCapital(p.getKey()) + "\tScore: " + p.getValue()));;

        // Outputs ingredients
        DrugUtil.getIngredients(DrugUtil.getRxcuiFromCommon(targetDrugName))
                .forEach(System.out::println);
    }
}
