package ca.corefacility.bioinformatics.irida.ria.web.admin.dto;

/**
 * Used by the UI to to get updated user statistics.
 */

public class UserStatistics {
	private Long numUsers;

	public UserStatistics(Long numUsers) {
		this.numUsers = numUsers;
	}

	public Long getNumUsers() {
		return numUsers;
	}

	public void setNumUsers(Long numUsers) {
		this.numUsers = numUsers;
	}
}
