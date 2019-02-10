package medeye.wrapper;

import medeye.Utility;
import medeye.medical.DrugUtil;
import medeye.medical.no.SideEffectWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WrapperUtil {
    public static ActiveSideEffectWrapper getActiveSideEffects(String targetDrugName) {
        String url = "https://api.fda.gov/drug/event.json?search=patient.drug.openfda.pharm_class_epc:\"";
        if (targetDrugName.contains("IBU") || targetDrugName.contains("NAP")) {
            url += "nonsteroidal+anti-inflammatory+drug";
        } else {
            // ignored
        }

        url += "\"&limit=1";

        String json = Utility.getStringFromUrl(url);
        SideEffectWrapper wrapper = Utility.gson.fromJson(json, SideEffectWrapper.class);
        SideEffectWrapper.Reaction[] reactions = wrapper.getResults()[0].getPatient().getReaction();

        List<String> sideEffects = Arrays.stream(wrapper.getResults()[0].getPatient().getReaction()).map(SideEffectWrapper.Reaction::getReactionmeddrapt).collect(Collectors.toList());

        Set<String> ingredients = DrugUtil.getIngredients(DrugUtil.getRxcuiFromCommon(targetDrugName));

        return new ActiveSideEffectWrapper(ingredients, sideEffects);
    }
}
