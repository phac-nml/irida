package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Encapsulates information about the project as well as permissions.
 */

public class ProjectInfoResponse {
	private Long id;
	private String name;
	private boolean canManage;
	private boolean canManageRemote;

	public ProjectInfoResponse(Project project, boolean canManage, boolean canManageRemote) {
		this.id = project.getId();
		this.name = project.getName();
		this.canManage = canManage;
		this.canManageRemote = canManageRemote;
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

	public boolean isCanManage() {
		return canManage;
	}

	public void setCanManage(boolean canManage) {
		this.canManage = canManage;
	}

	public boolean isCanManageRemote() {
		return canManageRemote;
	}

	public void setCanManageRemote(boolean canManageRemote) {
		this.canManageRemote = canManageRemote;
	}
}
