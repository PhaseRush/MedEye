package medeye.imaging;

public class DrugInfo {
    private String as_of_date;
    private String classification_for_rate_setting;
    private String effective_date;
    private String ndc_description;

    private String nadac_per_unit;
    private String pricing_unit;
    ///...


    public String getAs_of_date() {
        return as_of_date;
    }

    public String getClassification_for_rate_setting() {
        return classification_for_rate_setting;
    }

    public String getEffective_date() {
        return effective_date;
    }

    public String getNdc_description() {
        return ndc_description;
    }

    public String getNadac_per_unit() {
        return nadac_per_unit;
    }

    public String getPricing_unit() {
        return pricing_unit;
    }
}
