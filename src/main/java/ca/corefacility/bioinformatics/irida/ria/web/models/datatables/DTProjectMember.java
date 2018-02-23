package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * User interface model for DataTables for a {@link Project} Member.
 */
public class DTProjectMember implements DataTablesResponseModel {
	private Long id;
	private String name;
	private String role;
	private Date joined;

	public DTProjectMember(ProjectUserJoin projectUserJoin) {
		User user = projectUserJoin.getObject();
		this.id = user.getId();
		this.name = user.getLabel();
		this.role = projectUserJoin.getProjectRole().toString();
		this.joined = projectUserJoin.getCreatedDate();
	}

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
