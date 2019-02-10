package medeye.medical;

import medeye.Utility;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;

public class DrugSimilarity {

    public static Object getSimilar(String commonName) {
        String url = "https://rxnav.nlm.nih.gov/REST/approximateTerm?term=" + commonName;
        String xml = Utility.getStringFromUrl(url);
        JSONObject fromXML = XML.toJSONObject(xml);

        Encapsulate container = Utility.gson.fromJson(fromXML.toString(), Encapsulate.class);
        int rxcui = container.rxnormdata.approximateGroup.candidate[0].rxcui;




        return null;
    }

    public static ArrayList<Integer> getClassIds(int rxcui){
        String urlTreat = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui="+ rxcui + "&relaSource=MEDRT&relas=may_treat";
        String urlIngredient = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui="+ rxcui + "&relaSource=MEDRT&relas=has_ingredient";
        String urlMoA = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui="+ rxcui + "&relaSource=MEDRT&relas=has_moa";
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
