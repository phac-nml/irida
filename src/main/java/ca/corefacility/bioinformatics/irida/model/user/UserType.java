package ca.corefacility.bioinformatics.irida.model.user;

/**
 * Types of users in the application.
 * Refers to where the source of authentication is located
 */
public enum UserType {

	/**
	 * Constant reference for local user type.
	 */
	TYPE_LOCAL("TYPE_LOCAL"),

	/**
	 * Constant reference for remote domain user type.
	 */
	TYPE_DOMAIN("TYPE_DOMAIN");

	private String name;

	private UserType() {
	}

	private UserType(String name) {
		this();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
