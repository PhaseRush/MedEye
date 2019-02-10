package medeye.medical;

import javafx.util.Pair;
import medeye.Utility;
import org.json.JSONObject;
import org.json.XML;

import java.util.*;
import java.util.stream.Collectors;

public class DrugSimilarity {

    /**
     * Given a commonName, return top x drugs which are similar enough (sorted, decreasing) in terms of similar ingredients, cures/preventions of diseases, etc
     *
     * @param commonName input commonName
     * @return List of Pairs<String, Integer>, such that the String is the name of the similar drug and the Integer is the similarity score
     */
    public static List<Pair<String, Double>> getSimilar(String commonName) {
        int rxcui = DrugUtil.getRxcuiFromCommon(commonName);

        Map<Integer, Integer> numHits = drugVals(getClassIds(rxcui));
        // System.out.println(numHits.get(rxcui));

        numHits = Utility.sortMap(numHits, false);

        int perfectScore = numHits.get(rxcui); // needed for lambda
        Set<Map.Entry<Integer, Integer>> entries =
                numHits.entrySet().stream()
                        .filter(e -> e.getValue() < perfectScore) // filter out perfect scores because it is probably the drug itself
                        .collect(Collectors.toSet());

        int categorySize = numHits.get(rxcui);
        return entries.stream()
                .filter(e -> e.getValue() > 7) // arbitrary thing, might change
                .limit(10) // dont flood with low score matches
                .map(e -> new Pair<>(
                        DrugUtil.getCommonFromRxcui(e.getKey()),
                        (double)Math.round((double)10000*e.getValue()/categorySize)/10000))
                .collect(Collectors.toList());
    }

    /**
     * Returns an arrayList of HashSets with class IDs that the drug passed by rxcui is in.
     *
     * The entry at 0 is all diseases treated by the specific drug.
     * The entry at 1 is all diseases prevented by the specific drug.
     * The entry at 2 is all active in ingredients in the specific drug.
     * The entry at 3 is the mechanism(s) of action of the specific drug.
     *
     * @param rxcui is the rxcui (drug identifier) of the drug.
     * @return
     */
    public static ArrayList<HashSet<String>> getClassIds(int rxcui) {
        ArrayList<HashSet<String>> retList = new ArrayList<>();

        String urlTreat = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui=" + rxcui + "&relaSource=MEDRT&relas=may_treat"; //making url to retrive may treat classes
        String urlPrevent = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui=" + rxcui + "&relaSource=MEDRT&relas=may_prevent"; //making url to retrive may prevent classes
        String urlIngredient = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui=" + rxcui + "&relaSource=MEDRT&relas=has_ingredient"; //making url to retrive ingredient classes
        String urlMoA = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json?rxcui=" + rxcui + "&relaSource=MEDRT&relas=has_moa"; //making url to retrive mechanism of action classes

        // getting data from url
        String strTreat = Utility.getStringFromUrl(urlTreat);
        String strPrevent = Utility.getStringFromUrl(urlPrevent);
        String strIngredient = Utility.getStringFromUrl(urlIngredient);
        String strMoA = Utility.getStringFromUrl(urlMoA);


        //parsing may treat data
        HashSet<String> tempList0 = new HashSet<>();
        Encapsulator contTreat = Utility.gson.fromJson(strTreat, Encapsulator.class);
        rxdruginfo[] treatments = contTreat.rxclassDrugInfoList.rxclassDrugInfo;
        for (int i = 0; i < treatments.length; i++) {
            tempList0.add(treatments[i].rxclassMinConceptItem.classId);
        }
        retList.add(tempList0); //adding the set of may treat classes to the return list

        //parsing may prevent classes
        HashSet<String> tempList1 = new HashSet<>();
        Encapsulator contPrevent = Utility.gson.fromJson(strPrevent, Encapsulator.class);
        rxdruginfo[] prevents = contPrevent.rxclassDrugInfoList.rxclassDrugInfo; // lyrica, cartia fails
        for (int i = 0; i < prevents.length; i++) {
            tempList1.add(prevents[i].rxclassMinConceptItem.classId);
        }
        retList.add(tempList1); //adding the set of may prevent classes to the return list

        //parsing the ingredient classes
        HashSet<String> tempList2 = new HashSet<>();
        Encapsulator contIngredient = Utility.gson.fromJson(strIngredient, Encapsulator.class);
        rxdruginfo[] ingredients = contIngredient.rxclassDrugInfoList.rxclassDrugInfo;
        for (int i = 0; i < ingredients.length; i++) {
            tempList2.add(ingredients[i].rxclassMinConceptItem.classId);
        }
        retList.add(tempList2); //adding the set of ingredient classes to the return list

        //parsing the mechanism of action classes
        HashSet<String> tempList3 = new HashSet<>();
        Encapsulator contMoA = Utility.gson.fromJson(strMoA, Encapsulator.class);
        rxdruginfo[] MoAs = contMoA.rxclassDrugInfoList.rxclassDrugInfo;
        for (int i = 0; i < MoAs.length; i++) {
            tempList3.add(MoAs[i].rxclassMinConceptItem.classId);
        }
        retList.add(tempList3); //adding the set of mechanism of action classes to the return list

        return retList;
    }

    /**
     * Takes an arrayList of HashSets of drug classes (described in detail below) and returns a Map in which the keys are rxcui
     * keys and the values they map to are the number of classes they appear in in the HashSets in the ArrayList
     *
     * @param classes
     * The entry at 0 in the list is all diseases treated by the specific drug.
     * The entry at 1 in the list is all diseases prevented by the specific drug.
     * The entry at 2 in the list is all active in ingredients in the specific drug.
     * The entry at 3 in the list is all mechanism(s) of action of the specific drug.
     *
     * @return Map < Drug id
     */
    public static HashMap<Integer, Integer> drugVals(ArrayList<HashSet<String>> classes) {
        HashMap<Integer, Integer> retmap = new HashMap<>();

        // taking every may treat class the original drug is in and summing up how many times each drug appears in those classes
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

        // taking every may prevent class the original drug is in and summing up how many times each drug appears in those classes and ones above
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

        // taking every ingredient class the original drug is in and summing up how many times each drug appears in those classes and ones above
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

        // taking every mechanism of action class the original drug is in and summing up how many times each drug appears in those classes and ones above
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


    /**
     * Takes a class identifier and relationship and returns a set of the RXCUIs from all of the drugs in the relationship class
     * @param inClass the class identifier
     * @param relationship the MEDRT relationship (https://rxnav.nlm.nih.gov/RxClassIntro.html)
     * @return
     */
    public static HashSet<Integer> getInClass(String inClass, String relationship) {
        // forming a URL to retrieve all drugs in the given relationship and class
        String url = "https://rxnav.nlm.nih.gov/REST/rxclass/classMembers?classId=" + inClass + "&relaSource=MEDRT&rela=" + relationship;
        String xml = Utility.getStringFromUrl(url);
        JSONObject fromXML = XML.toJSONObject(xml);

        //Taking data from URL
        DGEncapsulator container = Utility.gson.fromJson(fromXML.toString(), DGEncapsulator.class); // Lisinopril breaks
        drugMem[] outarr = container.rxclassdata.drugMemberGroup.drugMember;

        //Returning parsed data
        HashSet<Integer> retset = new HashSet<>();
        for (int i = 0; i < outarr.length; i++) {
            retset.add(outarr[i].minConcept.rxcui);
        }
        return retset;
    }


    /**
     * A set of classes for parsing JSON from getSimilar
     * getting rxcui
     */
    public class Encapsulate {
        Rxnormdata rxnormdata;
    }
    public class Rxnormdata {
        ApproximateGroup approximateGroup;
    }
    public class ApproximateGroup {
        CanidateObj[] candidate;
    }
    public class CanidateObj {
        int rxaui;
        int score;
        int rank;
        int rxcui;
    }

    /**
     * A set of classes for parsing JSON from getClassIDs
     * ingredients
     */
    public class Encapsulator {
        rxdruginfolist rxclassDrugInfoList;
    }
    public class rxdruginfolist {
        rxdruginfo[] rxclassDrugInfo;
    }
    public class rxdruginfo {
        rxmin rxclassMinConceptItem;
    }
    public class rxmin {
        String classId;
        String className;
        String classType;
    }


    /**
     * A set of classes for parsing JSON from getInClass
     */
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

