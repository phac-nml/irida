package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsLocale that = (UserDetailsLocale) o;
		return Objects.equals(language, that.language) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(language, name);
	}
}
