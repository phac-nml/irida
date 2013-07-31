package ca.corefacility.bioinformatics.irida.model.enums;

/**
 * When sorting a collection of generic objects you should be able to specify
 * the order of the sort.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public enum Order {

    ASCENDING("ASCENDING"),
    DESCENDING("DESCENDING"),
    NONE("NONE");
    private String code;

    private Order(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static Order fromString(String code) {
        switch (code.toUpperCase()) {
            case "ASCENDING":
                return ASCENDING;
            case "DESCENDING":
                return DESCENDING;
            default:
                return NONE;
        }
    }
}
