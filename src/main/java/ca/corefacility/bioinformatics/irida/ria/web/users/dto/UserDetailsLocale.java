package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

/**
 * Stores locale info the User Details Page.
 */
public class UserDetailsLocale {
	private String language;
	private String name;

	public UserDetailsLocale(String language, String name) {
		this.language = language;
		this.name = name;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
