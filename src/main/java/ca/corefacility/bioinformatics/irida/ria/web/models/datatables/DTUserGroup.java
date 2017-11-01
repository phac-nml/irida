package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * User Interface model for DataTables for {@link UserGroup}
 */
public class DTUserGroup implements DataTablesResponseModel {
	private Long id;
	private String name;
	private String description;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isOwner;
	private boolean isAdmin;

	public DTUserGroup(UserGroup group, boolean isOwner, boolean isAdmin) {
		this.id = group.getId();
		this.name = group.getName();
		this.description = group.getDescription();
		this.createdDate = group.getCreatedDate();
		this.modifiedDate = group.getModifiedDate();
		this.isOwner = isOwner;
		this.isAdmin = isAdmin;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean owner) {
		isOwner = owner;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}
}
