package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;

/**
 * User interface model for DataTables for {@link ca.corefacility.bioinformatics.irida.model.user.group.UserGroup} member
 */
public class DTGroupMember implements DataTablesResponseModel {
	private Long id;
	private String label;
	private String username;
	private String role;
	private Date createdDate;

	public DTGroupMember(UserGroupJoin join) {
		User user = join.getSubject();
		this.id = user.getId();
		this.label = user.getLabel();
		this.username = user.getUsername();
		this.role = join.getRole().toString();
		this.createdDate = join.getCreatedDate();
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
