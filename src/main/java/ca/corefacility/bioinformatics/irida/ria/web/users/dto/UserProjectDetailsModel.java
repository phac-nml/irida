package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;

public class UserProjectDetailsModel {
	private Long projectId;
	private String projectName;
	private String roleName;
	private String groupName;
	private Date createdDate;
	private boolean isManager;
	private boolean isEmailSubscribed;

	public UserProjectDetailsModel(ProjectUserJoin join) {
		Project project = join.getSubject();
		this.projectId = project.getId();
		this.projectName = project.getName();

		if (join != null) {
			this.isManager = join.getProjectRole()
					.equals(ProjectRole.PROJECT_OWNER);
			this.isEmailSubscribed = join.isEmailSubscription();
			this.roleName = join.getProjectRole()
					.toString();
			this.groupName = null;
			this.createdDate = join.getCreatedDate();
		}
	}

	public UserProjectDetailsModel(UserGroupProjectJoin join) {
		Project project = join.getSubject();
		this.projectId = project.getId();
		this.projectName = project.getName();

		if (join != null) {
			this.isManager = join.getProjectRole()
					.equals(ProjectRole.PROJECT_OWNER);
			this.isEmailSubscribed = false;
			this.roleName = join.getProjectRole()
					.toString();
			this.groupName = join.getObject()
					.getName();
			this.createdDate = join.getCreatedDate();
		}
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isManager() {
		return isManager;
	}

	public void setManager(boolean manager) {
		isManager = manager;
	}

	public boolean isEmailSubscribed() {
		return isEmailSubscribed;
	}

	public void setEmailSubscribed(boolean emailSubscribed) {
		isEmailSubscribed = emailSubscribed;
	}
}
