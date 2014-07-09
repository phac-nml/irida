
package ca.corefacility.bioinformatics.irida.model.enums;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public enum ProjectRole {

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

    public static ProjectRole fromString(String code) {
        switch (code.toUpperCase()) {
            case "PROJECT_USER":
                return PROJECT_USER;
            case "PROJECT_OWNER":
                return PROJECT_OWNER;
            default:
                return PROJECT_USER;
        }
    }
}

