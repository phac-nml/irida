package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;

import java.util.Date;

/**
 * Used to send information about a user group member to the UI.
 */
public class UserGroupMember {
	private final Long id;
	private final Long key;
	private final String name;
	private final Date createdDate;
	private final String role;

	public UserGroupMember(UserGroupJoin join) {
		User user = join.getSubject();
		this.id = user.getId();
		this.key = user.getId();
		this.name = user.getLabel();
		this.createdDate = join.getCreatedDate();
		this.role = join.getRole().toString();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getRole() {
		return role;
	}

	public Long getKey() {
		return key;
	}
}
