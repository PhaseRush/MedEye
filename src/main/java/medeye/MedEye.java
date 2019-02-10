package medeye;

import medeye.imaging.DrugInfo;
import medeye.imaging.ImageUtil;
import medeye.imaging.ImageUtil.DrugTriplet;
import medeye.medical.DrugSimilarity;

import java.io.IOException;
import java.util.List;

public class MedEye {
    private static final String DRUG_NAME_DATABASE_URL = "https://data.medicaid.gov/resource/tau9-gfwr.json";

    public static void main(String[] args) throws IOException {
        DrugInfo[] drugs = Utility.gson.fromJson(Utility.getStringFromUrl(DRUG_NAME_DATABASE_URL), DrugInfo[].class);

        // The path to the image file to annotate
        String fileName = "MedEye_Images/ibu2.png";

        // run Optical Character Recognition (OCR) on the image file to determine the target drug name
        String targetDrugName = Utility.omitLastNewline(ImageUtil.runOCR(fileName)).toUpperCase();

        System.out.println("TARGET: " + targetDrugName); // debug testing

        // process our drugs by applying filters, and sorting matching drugs by unit price
        List<DrugTriplet> processedDrugs = ImageUtil.processDrugs(drugs, targetDrugName);
        processedDrugs.forEach(drug -> System.out.println(drug.getName() + "\n$" + drug.getUnitPrice() + " / " + drug.getUnit()));

        // Playing around with Drug Similarity
        DrugSimilarity.getSimilar(targetDrugName);


    }
}
