package medeye.medical;

import medeye.Utility;

import java.util.Arrays;

public class DrugUtil {

    public static String getCommonFromRxcui(int rxcui) {
        String json = Utility.getStringFromUrl("https://rxnav.nlm.nih.gov/REST/rxcui/"+ rxcui +"/allProperties.json?prop=all");
        CommonWrapper wrapper = Utility.gson.fromJson(json, CommonWrapper.class);

        return Arrays.stream(wrapper.getPropConceptGroup().getPropConcept())
                .filter(concept -> concept.getPropName().equals("RxNorm Name"))
                .findAny()
                .get()
                .getPropValue();
    }


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
}
