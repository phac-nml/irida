package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.Date;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;

public class UserGroupDetails {
	private final Long id;
	private final Long key;
	private final String name;
	private final String description;
	private final Date createdDate;
	private final Date modifiedDate;
	private final List<UserGroupMember> members;

	public UserGroupDetails(UserGroup group, List<UserGroupMember> members) {
		this.id = group.getId();
		this.key = group.getId();
		this.name = group.getLabel();
		this.description = group.getDescription();
		this.createdDate = group.getCreatedDate();
		this.modifiedDate = group.getModifiedDate();
		this.members = members;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public List<UserGroupMember> getMembers() {
		return members;
	}

	public Long getKey() {
		return key;
	}
}
