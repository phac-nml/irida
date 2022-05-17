package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.user.User;

public class UserPasswordResetDetails {
	private String identifier;
	private User user;

	public UserPasswordResetDetails(String identifier, User user) {
		this.identifier = identifier;
		this.user = user;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
