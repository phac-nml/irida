package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * User interface model for DataTables for a {@link Project} Group.
 */
public class DTProjectGroup  implements DataTablesResponseModel {
	private Long id;
	private String name;
	private String role;
	private Date joined;

	public DTProjectGroup(UserGroupProjectJoin userGroupProjectJoin) {
		UserGroup group = userGroupProjectJoin.getObject();
		this.id = group.getId();
		this.name = group.getName();
		this.role = userGroupProjectJoin.getProjectRole().toString();
		this.joined = userGroupProjectJoin.getCreatedDate();
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Date getJoined() {
		return joined;
	}

	public void setJoined(Date joined) {
		this.joined = joined;
	}
}
