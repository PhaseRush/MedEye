package medeye.wrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActiveSideEffectWrapper {
    private Map<Set<String>, List<String>> map;

    public ActiveSideEffectWrapper(Set<String> ingredients, List<String> sideEffects) {
        map = new HashMap<>();
        map.put(ingredients, sideEffects);
    }

    public Map<Set<String>, List<String>> getMap() {
        return map;
    }
}
