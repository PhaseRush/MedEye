package medeye.medical;

import medeye.Utility;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class DrugSimilarity {

    public static Object getSimilar(String commonName) {
        String url = "https://rxnav.nlm.nih.gov/REST/approximateTerm?term=" + commonName;
        String xml = Utility.getStringFromUrl(url);
        JSONObject fromXML = XML.toJSONObject(xml);

        Encapsulate container = Utility.gson.fromJson(fromXML.toString(), Encapsulate.class);
        int rxcui = container.rxnormdata.approximateGroup.candidate[0].rxcui;

        HashMap<Integer, Integer> numHits = drugVals(getClassIds(rxcui));


        return null;
    }

    public static ArrayList<HashSet<String>> getClassIds(int rxcui) {
        ArrayList<HashSet<String>> retList = new ArrayList<>();

        String urlTreat = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui=" + rxcui + "&relaSource=MEDRT&relas=may_treat";
        String urlPrevent = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui=" + rxcui + "&relaSource=MEDRT&relas=may_prevent";
        String urlIngredient = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui=" + rxcui + "&relaSource=MEDRT&relas=has_ingredient";
        String urlMoA = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui=" + rxcui + "&relaSource=MEDRT&relas=has_moa";

        String strTreat = Utility.getStringFromUrl(urlTreat);
        String strPrevent = Utility.getStringFromUrl(urlPrevent);
        String strIngredient = Utility.getStringFromUrl(urlIngredient);
        String strMoA = Utility.getStringFromUrl(urlMoA);


        HashSet<String> tempList0 = new HashSet<>();
        Encapsulator contTreat = Utility.gson.fromJson(strTreat, Encapsulator.class);
        rxdruginfo[] treatments = contTreat.rxclassDrugInfoList.rxclassDrugInfo;
        for (int i = 0; i < treatments.length; i++) {
            tempList0.add(treatments[i].rxclassMinConceptItem.classId);
        }
        retList.add(tempList0);

        HashSet<String> tempList1 = new HashSet<>();
        Encapsulator contPrevent = Utility.gson.fromJson(strPrevent, Encapsulator.class);
        rxdruginfo[] prevents = contPrevent.rxclassDrugInfoList.rxclassDrugInfo;
        for (int i = 0; i < prevents.length; i++) {
            tempList1.add(prevents[i].rxclassMinConceptItem.classId);
        }
        retList.add(tempList1);

        HashSet<String> tempList2 = new HashSet<>();
        Encapsulator contIngredient = Utility.gson.fromJson(strIngredient, Encapsulator.class);
        rxdruginfo[] ingredients = contIngredient.rxclassDrugInfoList.rxclassDrugInfo;
        for (int i = 0; i < ingredients.length; i++) {
            tempList2.add(ingredients[i].rxclassMinConceptItem.classId);
        }
        retList.add(tempList2);

        HashSet<String> tempList3 = new HashSet<>();
        Encapsulator contMoA = Utility.gson.fromJson(strMoA, Encapsulator.class);
        rxdruginfo[] MoAs = contMoA.rxclassDrugInfoList.rxclassDrugInfo;
        for (int i = 0; i < MoAs.length; i++) {
            tempList3.add(MoAs[i].rxclassMinConceptItem.classId);
        }
        retList.add(tempList3);

        return retList;
    }

    public static HashMap<Integer, Integer> drugVals(ArrayList<HashSet<String>> classes) {
        HashMap<Integer, Integer> retmap = new HashMap<>();
        for (String classCode: classes.get(0)){
            HashSet<Integer> inclass = getInClass(classCode, "may_treat");
            for(int key: inclass){
                try {
                    int currval = retmap.putIfAbsent(key, 1);
                    if (currval != 0){
                        retmap.replace(key, currval, currval+1);
                    }

                }
                catch(NullPointerException ignored){}
            }
        }

        for (String classCode: classes.get(1)){
            HashSet<Integer> inclass = getInClass(classCode, "may_prevent");
            for(int key: inclass){
                try {
                    int currval = retmap.putIfAbsent(key, 1);
                    if (currval != 0){
                        retmap.replace(key, currval, currval+1);
                    }

                }
                catch(NullPointerException ignored){}
            }
        }

        for (String classCode: classes.get(2)){
            HashSet<Integer> inclass = getInClass(classCode, "has_ingredient");
            for(int key: inclass){
                try {
                    int currval = retmap.putIfAbsent(key, 1);
                    if (currval != 0){
                        retmap.replace(key, currval, currval+1);
                    }

                }
                catch(NullPointerException ignored){}
            }
        }

        for (String classCode: classes.get(3)){
            HashSet<Integer> inclass = getInClass(classCode, "has_moa");
            for(int key: inclass){
                try {
                    int currval = retmap.putIfAbsent(key, 1);
                    if (currval != 0){
                        retmap.replace(key, currval, currval+1);
                    }

                }
                catch(NullPointerException ignored){}
            }
        }
        return retmap;
    }

    public static HashSet<Integer> getInClass(String inClass, String relationship) {
        String url = "https://rxnav.nlm.nih.gov/REST/rxclass/classMembers?classId=" + inClass + "&relaSource=MEDRT&rela=" + relationship;
        String xml = Utility.getStringFromUrl(url);
        JSONObject fromXML = XML.toJSONObject(xml);

        DGEncapsulator container = Utility.gson.fromJson(fromXML.toString(), DGEncapsulator.class);
        drugMem[] outarr = container.rxclassdata.drugMemberGroup.drugMember;

        HashSet<Integer> retset = new HashSet<>();
        for (int i = 0; i < outarr.length; i++) {
            retset.add(outarr[i].minConcept.rxcui);
        }
        return retset;
    }


    private class Encapsulate {
        Rxnormdata rxnormdata;
    }

    private class Rxnormdata {
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


    private class Encapsulator {
        rxdruginfolist rxclassDrugInfoList;
    }

    private class rxdruginfolist {
        rxdruginfo[] rxclassDrugInfo;
    }

    private class rxdruginfo {
        rxmin rxclassMinConceptItem;
    }

    private class rxmin {
        String classId;
        String className;
        String classType;
    }


    private class DGEncapsulator {
        rxData rxclassdata;
    }

    private class rxData {
        drugMemGroup drugMemberGroup;
    }

    private class drugMemGroup {
        drugMem[] drugMember;
    }

    private class drugMem {
        minConc minConcept;
    }

    private class minConc {
        int rxcui;
    }

}

