package ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Consumed by the UI on the project details page.
 * Returns default information about a project.
 */
public class ProjectDetailsResponse {
	private Long id;
	private String label;
	private Date createdDate;
	private Date modifiedDate;
	private String organism;
	private String description;

	public ProjectDetailsResponse(Project project) {
		this.id = project.getId();
		this.label = project.getLabel();
		this.createdDate = project.getCreatedDate();
		this.modifiedDate = project.getModifiedDate();
		this.organism = project.getOrganism();
		this.description = project.getProjectDescription();
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
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
}
