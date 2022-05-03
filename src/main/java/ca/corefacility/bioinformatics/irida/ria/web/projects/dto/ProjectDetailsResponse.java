package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.Coverage;

/**
 * Encapsulates information about the project as well as permissions.
 */

public class ProjectDetailsResponse extends AjaxResponse {
	private Long id;
	private String label;
	private Date createdDate;
	private Date modifiedDate;
	private String organism;
	private String description;
	private boolean canManage;
	private boolean canManageRemote;
	private String priority;
	private Long defaultMetadataTemplateId = 0L;
	private Coverage coverage;
	private boolean isRemote;

	public ProjectDetailsResponse(Project project, boolean canManage, boolean canManageRemote, MetadataTemplate defaultMetadataTemplate) {
		this.id = project.getId();
		this.label = project.getName();
		this.createdDate = project.getCreatedDate();
		this.modifiedDate = project.getModifiedDate();
		this.organism = project.getOrganism();
		this.description = project.getProjectDescription();
		this.canManage = canManage;
		this.canManageRemote = canManageRemote;
		this.isRemote = project.isRemote();

		MetadataTemplate defaultTemplate = defaultMetadataTemplate;
		if (defaultTemplate != null) {
			defaultMetadataTemplateId = defaultTemplate.getId();
		}

		AnalysisSubmission.Priority analysisPriority = project.getAnalysisPriority();
		if (analysisPriority != null) {
			priority = analysisPriority.name();
		}

		var minimum = project.getMinimumCoverage() == null ? -1 : project.getMinimumCoverage();
		var maximum = project.getMaximumCoverage() == null ? -1 : project.getMaximumCoverage();
		var genomeSize = project.getGenomeSize() == null ? -1 : project.getGenomeSize();
		this.coverage = new Coverage(minimum, maximum, genomeSize);
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

	public boolean isRemote() {
		return isRemote;
	}

	public Long getDefaultMetadataTemplateId() {
		return defaultMetadataTemplateId;
	}

	public void setDefaultMetadataTemplateId(Long defaultMetadataTemplateId) {
		this.defaultMetadataTemplateId = defaultMetadataTemplateId;
	}

	public Coverage getCoverage() {
		return coverage;
	}

	public void setCoverage(Coverage coverage) {
		this.coverage = coverage;
	}
}
