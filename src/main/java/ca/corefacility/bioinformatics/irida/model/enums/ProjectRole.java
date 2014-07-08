
package ca.corefacility.bioinformatics.irida.model.enums;

import org.springframework.util.StringUtils;

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
    
	/**
	 * Get the display name for this role. It will generally return a
	 * capatalized version of "name" without the "PROJECT_" prefix.
	 * 
	 * @return Display name for this Role
	 */
	public String getDisplayName() {
		String ROLE_PREFIX = "PROJECT_";
		int lastIndexOf = code.lastIndexOf(ROLE_PREFIX);
		return StringUtils.capitalize(code.substring(lastIndexOf + ROLE_PREFIX.length()).toLowerCase());
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

