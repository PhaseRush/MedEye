package medeye.wrapper;

import medeye.Utility;
import medeye.medical.DrugUtil;
import medeye.medical.no.SideEffectWrapper;

import java.util.*;
import java.util.stream.Collectors;

public class WrapperUtil {
    public static ActiveSideEffectWrapper getActiveSideEffects(String targetDrugName) {
        String url = "https://api.fda.gov/drug/event.json?search=patient.drug.openfda.pharm_class_epc:\"";
        if (targetDrugName.contains("IBU") || targetDrugName.contains("NAP")) {
            url += "nonsteroidal+anti-inflammatory+drug";
        } else {
            return extraCase(targetDrugName);
        }

        url += "\"&limit=1";

        String json = Utility.getStringFromUrl(url);
        SideEffectWrapper wrapper = Utility.gson.fromJson(json, SideEffectWrapper.class);

        ArrayList<String> sideEffects = Arrays.stream(wrapper.getResults()[0].getPatient().getReaction()).map(SideEffectWrapper.Reaction::getReactionmeddrapt).collect(Collectors.toCollection(ArrayList::new));

        Set<String> ingredients = DrugUtil.getIngredients(DrugUtil.getRxcuiFromCommon(targetDrugName));

        return new ActiveSideEffectWrapper(ingredients, sideEffects);
    }

    private static ActiveSideEffectWrapper extraCase(String targetDrugName) {
        ArrayList<String> sideEffects = new ArrayList<>();
        Set<String> ingredients = DrugUtil.getIngredients(DrugUtil.getRxcuiFromCommon(targetDrugName));
        if (targetDrugName.contains("LYR")) {
            sideEffects.addAll(Arrays.asList("Blurred Vision", "Drowsiness", "Loss of Balance", "Constipation", "Tremors"));
        } else if (targetDrugName.contains("CAR")) {
            sideEffects.addAll(Arrays.asList("Dizziness", "Flushing", "Nausea", "Headache"));
        }

        return new ActiveSideEffectWrapper(ingredients, sideEffects);
    }
}
