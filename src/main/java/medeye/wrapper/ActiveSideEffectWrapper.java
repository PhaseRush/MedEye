package medeye.wrapper;

import java.util.*;

public class ActiveSideEffectWrapper {
    private Map<Set<String>, ArrayList<String>> map;
    private Set<String> ingredients;
    private ArrayList<String> sideEffects;

    public ActiveSideEffectWrapper(Set<String> ingredients, ArrayList<String> sideEffects) {
        map = new HashMap<>();
        map.put(ingredients, sideEffects);
        this.ingredients = ingredients;
        this.sideEffects = sideEffects;
    }


    public Set<String> getIngredients() {
        return ingredients;
    }

    public ArrayList<String> getSideEffects() {
        return sideEffects;
    }

    public Map<Set<String>, ArrayList<String>> getMap() {
        return map;
    }
}
