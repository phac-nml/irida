package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Encapsulates information about the project as well as permissions.
 */

public class ProjectInfoResponse {
	private Long id;
	private String label;
	private Date createdDate;
	private Date modifiedDate;
	private String organism;
	private String description;
	private boolean canManage;
	private boolean canManageRemote;
	private String priority;

	public ProjectInfoResponse(Project project, boolean canManage, boolean canManageRemote) {
		this.id = project.getId();
		this.label = project.getName();
		this.createdDate = project.getCreatedDate();
		this.modifiedDate = project.getModifiedDate();
		this.organism = project.getOrganism();
		this.description = project.getProjectDescription();
		this.canManage = canManage;
		this.canManageRemote = canManageRemote;

		AnalysisSubmission.Priority analysisPriority = project.getAnalysisPriority();
		if (analysisPriority != null) {
			priority = analysisPriority.name();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public String getOrganism() {
		return organism;
	}

	public String getDescription() {
		return description;
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

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
}
