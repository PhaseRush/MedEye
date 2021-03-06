package medeye.medical;

import medeye.Utility;
import medeye.imaging.ImageUtil;
import org.json.JSONObject;
import org.json.XML;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DrugUtil {

    /**
     * Get the Rxcui from a drug's common name
     * @param commonName
     * @return rxcui of drug
     */
    public static int getRxcuiFromCommon(String commonName) {
        String url = "https://rxnav.nlm.nih.gov/REST/approximateTerm?term=" + commonName;
        String xml = Utility.getStringFromUrl(url);
        JSONObject fromXML = XML.toJSONObject(xml);

        DrugSimilarity.Encapsulate container = Utility.gson.fromJson(fromXML.toString(), DrugSimilarity.Encapsulate.class);
        return container.rxnormdata.approximateGroup.candidate[0].rxcui;
    }
    // opposite as above
    public static String getCommonFromRxcui(int rxcui) {
        String json = Utility.getStringFromUrl("https://rxnav.nlm.nih.gov/REST/rxcui/"+ rxcui +"/allProperties.json?prop=all");
        CommonWrapper wrapper = Utility.gson.fromJson(json, CommonWrapper.class);

        return Arrays.stream(wrapper.getPropConceptGroup().getPropConcept())
                .filter(concept -> concept.getPropName().equals("RxNorm Name"))
                .findAny()
                .get()
                .getPropValue();
    }

    /**
     * Processes drugs based on target drug name.
     * Filters out drugs that do not contain keyword name, then sorts remaining based on price.
     *
     * @param drugs Array of all drugs in database
     * @param target name of drug, treated as keyword for all drug names
     * @return List of DrugTriplet (container w/ 3 fields) of sorted (increasing) drugs which match target name
     */
    public static List<ImageUtil.DrugTriplet> processDrugs(DrugInfo[] drugs, String target) {
        return Arrays.stream(drugs)
                .filter(d -> d.getNdc_description().contains(target))
                .map(d -> new ImageUtil.DrugTriplet(d.getNdc_description(), Double.valueOf(d.getNadac_per_unit()), d.getPricing_unit()))
                .sorted((o1, o2) -> {
                    if (o1.getUnitPrice() == o2.getUnitPrice()) return 0;
                    return (o1.getUnitPrice() > o2.getUnitPrice() ? 1 : -1);
                }).limit(8) // change this later
                .collect(Collectors.toList());
    }

    /**
     * Given a rxcui, return all UNIQUE active ingredients
     * @param rxcui input drug rxcui
     * @return Set of ingredients
     */
    public static Set<String> getIngredients(int rxcui) {
        String urlIngredient = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui=" + rxcui + "&relaSource=MEDRT&relas=has_ingredient"; //making url to retrive ingredient classes
        String json = Utility.getStringFromUrl(urlIngredient);
        DrugSimilarity.Encapsulator ingredientContainer = Utility.gson.fromJson(json, DrugSimilarity.Encapsulator.class);

        return Arrays.stream(ingredientContainer.rxclassDrugInfoList.rxclassDrugInfo)
                .filter(container -> container.rxclassMinConceptItem.classType.equals("CHEM"))
                .map(container -> container.rxclassMinConceptItem.className)
                .collect(Collectors.toSet());
    }
    // same as above but can accept common name
    public static Set<String> getIngredients(String commonName) {
        return getIngredients(getRxcuiFromCommon(commonName));
    }

    // class wrapper
    private class CommonWrapper {
        private PropConceptGroup propConceptGroup;

        public PropConceptGroup getPropConceptGroup() {
            return propConceptGroup;
        }
    }
    private class PropConceptGroup {
        private PropConcept[] propConcept;

        public PropConcept[] getPropConcept() {
            return propConcept;
        }
    }
    private class PropConcept {
        private String propName;
        private String propValue;

        public String getPropName() {
            return propName;
        }

        public String getPropValue() {
            return propValue;
        }
    }

    /*
    encapsulation object
     */
    public class DrugInfo {
        private String as_of_date;
        private String classification_for_rate_setting;
        private String effective_date;
        private String ndc_description;

        private String nadac_per_unit;
        private String pricing_unit;
        ///...


        public String getAs_of_date() {
            return as_of_date;
        }

        public String getClassification_for_rate_setting() {
            return classification_for_rate_setting;
        }

        public String getEffective_date() {
            return effective_date;
        }

        public String getNdc_description() {
            return ndc_description;
        }

        public String getNadac_per_unit() {
            return nadac_per_unit;
        }

        public String getPricing_unit() {
            return pricing_unit;
        }
    }
}
