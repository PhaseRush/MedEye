package medeye.medical;

import medeye.Utility;
import org.json.JSONObject;
import org.json.XML;

public class DrugSimilarity {

    public static Object getSimilar(String commonName) {
        String url = "https://rxnav.nlm.nih.gov/REST/approximateTerm?term=" + commonName;
        String xml = Utility.getStringFromUrl(url);
        JSONObject fromXML = XML.toJSONObject(xml);

        Encapsulate container = Utility.gson.fromJson(fromXML.toString(), Encapsulate.class);
        int rxcui = container.rxnormdata.approximateGroup.candidate[0].rxcui;



        return null;
    }


    private class Encapsulate {
        Rxnormdata rxnormdata;
    }
    private class Rxnormdata{
        ApproximateGroup approximateGroup;
    }
    private class ApproximateGroup {
        CanidateObj[] candidate;
    }
    private class CanidateObj {
        int rxaui;
        int score;
        int rank;
        int rxcui;
    }
}
