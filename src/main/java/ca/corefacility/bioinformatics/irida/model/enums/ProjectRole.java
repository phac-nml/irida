package ca.corefacility.bioinformatics.irida.model.enums;

/**
 *
 */
public enum ProjectRole {

	PROJECT_USER("PROJECT_USER", 1),
	PROJECT_OWNER("PROJECT_OWNER", 2);

	private String code;
	private int level;

	private ProjectRole(String code, int level) {
		this.code = code;
		this.level = level;
	}

	@Override
	public String toString() {
		return code;
	}

	/**
	 * Get a role from the given string code
	 *
	 * @param code the string to get a role for
	 * @return The requested ProjectRole
	 */
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

	public int getLevel() {
		return level;
	}
}

