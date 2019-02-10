package medeye.wrapper;

import javafx.util.Pair;
import medeye.imaging.ImageUtil;
import medeye.medical.DrugSimilarity;
import medeye.medical.DrugUtil;

import java.util.ArrayList;
import java.util.List;

import static medeye.MedEye.DRUG_DATABASE;

public class AlternativePrices {
    List<AlternativePriceObj> list;

    public AlternativePrices(String targetDrugName) {
        list = new ArrayList<>();
        List<Pair<String, Double>> temp = DrugSimilarity.getSimilar(targetDrugName);
        for (Pair<String, Double> pair : temp) {
            String recDrug = pair.getKey().toUpperCase();
            List<ImageUtil.DrugTriplet> parsedDrugs = DrugUtil.processDrugs(DRUG_DATABASE, recDrug);
            //double unitPrice = DrugUtil.processDrugs(DRUG_DATABASE, pair.getKey().toUpperCase()).get(0).getUnitPrice();
            if (parsedDrugs.isEmpty()) continue; // dont NPE next line
            double unitPrice = parsedDrugs.get(0).getUnitPrice();
            list.add(new AlternativePriceObj(pair.getKey(), "$" +unitPrice + " / " + "EA", pair.getValue()*100 + "%"));
        }
    }

    public class AlternativePriceObj {
        private String name;
        private String price;
        private String similarityPercent;

        public AlternativePriceObj(String name, String price, String similarityPercent) {
            this.name = name;
            this.price = price;
            this.similarityPercent = similarityPercent;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public String getSimilarityPercent() {
            return similarityPercent;
        }
    }
}
