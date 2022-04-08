
package ca.corefacility.bioinformatics.irida.model.enums;

/**
 *
 */
public enum ProjectRole {

    PROJECT_VIEWER("PROJECT_VIEWER"),
    PROJECT_USER("PROJECT_USER"),
    PROJECT_OWNER("PROJECT_OWNER");
	
    private String code;

    private ProjectRole(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    /**
     * Get a role from the given string code
     * @param code the string to get a role for
     * @return The requested ProjectRole
     */
    public static ProjectRole fromString(String code) {
        switch (code.toUpperCase()) {
            case "PROJECT_VIEWER":
                return PROJECT_VIEWER;
            case "PROJECT_USER":
                return PROJECT_USER;
            case "PROJECT_OWNER":
                return PROJECT_OWNER;
            default:
                return PROJECT_USER;
        }
    }
}
