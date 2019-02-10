package medeye.medical;

import medeye.Utility;

public class DrugUtil {


    public static StringBuilder fallback(String targetDrugName) {
        String url = "https://clinicaltables.nlm.nih.gov/api/rxterms/v3/search?terms=" + targetDrugName + "&ef=STRENGTHS_AND_FORMS";
        String json = Utility.getStringFromUrl(url);

        Fallback obj = Utility.gson.fromJson(json, Fallback.class);

        System.out.println(obj.STRENGTHS_AND_FORMS.length);

        return null;
    }


    public class Fallback
    {
        private String[][] STRENGTHS_AND_FORMS;

        public String[][] getSTRENGTHS_AND_FORMS ()
        {
            return STRENGTHS_AND_FORMS;
        }

        public void setSTRENGTHS_AND_FORMS (String[][] STRENGTHS_AND_FORMS)
        {
            this.STRENGTHS_AND_FORMS = STRENGTHS_AND_FORMS;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [STRENGTHS_AND_FORMS = "+STRENGTHS_AND_FORMS+"]";
        }
    }
}
