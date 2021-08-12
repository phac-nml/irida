package ca.corefacility.bioinformatics.irida.model.enums;

public enum ProjectMetadataRole {
	LEVEL_1("LEVEL_1", 1),
	LEVEL_2("LEVEL_2", 2),
	LEVEL_3("LEVEL_3", 3),
	LEVEL_4("LEVEL_4", 4);

	private String code;
	private int level;

	private ProjectMetadataRole(String code, int level) {
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
	public static ProjectMetadataRole fromString(String code) {
		switch (code.toUpperCase()) {
		case "LEVEL_1":
			return LEVEL_1;
		case "LEVEL_2":
			return LEVEL_2;
		case "LEVEL_3":
			return LEVEL_3;
		case "LEVEL_4":
			return LEVEL_4;
		default:
			return LEVEL_1;
		}
	}

	public int getLevel() {
		return level;
	}
}
