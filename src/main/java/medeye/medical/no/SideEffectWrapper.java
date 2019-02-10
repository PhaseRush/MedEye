package medeye.medical.no;

public class SideEffectWrapper {
    private ResultObj[] results;

    public ResultObj[] getResults() {
        return results;
    }

    public class ResultObj {
        private Patient patient;

        public Patient getPatient() {
            return patient;
        }
    }

    public class Patient {
        private Reaction[] reaction;

        public Reaction[] getReaction() {
            return reaction;
        }
    }
    public class Reaction {
        private String reactionmeddrapt;

        public String getReactionmeddrapt() {
            return reactionmeddrapt;
        }
    }
}
