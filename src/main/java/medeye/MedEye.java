package medeye;

import medeye.imaging.DrugInfo;
import medeye.imaging.ImageUtil.DrugTriplet;

import java.io.IOException;
import java.util.List;

import static medeye.imaging.ImageUtil.processDrugs;
import static medeye.imaging.ImageUtil.runOCR;

public class MedEye {
    private static final String DRUG_NAME_DATABASE_URL = "https://data.medicaid.gov/resource/tau9-gfwr.json";

    // https://cloud.google.com/docs/authentication/production#auth-cloud-implicit-java
    public static void main(String[] args) throws IOException {
        DrugInfo[] drugs = Utility.gson.fromJson(Utility.getStringFromUrl(DRUG_NAME_DATABASE_URL), DrugInfo[].class);

        // The path to the image file to annotate
        String fileName = "MedEye_Images/lis.png";

        // run Optical Character Recognition (OCR) on the image file to determine the target drug name
        String targetDrugName = Utility.omitLastNewline(runOCR(fileName)).toUpperCase();

        System.out.println("TARGET: " + targetDrugName); // debug testing
        System.out.println(targetDrugName.endsWith("\n"));

        // process our drugs by applying filters, and sorting matching drugs by unit price
        List<DrugTriplet> processedDrugs = processDrugs(drugs, targetDrugName);

        processedDrugs.forEach(drug -> System.out.println(drug.getName() + "\n$" + drug.getUnitPrice() + " / " + drug.getUnit()));

    }
}
