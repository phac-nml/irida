package ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.project.Project;

public class AssociatedProject {
	private String label;
	private Long id;
	private String organism;
	private Date createdDate;

	public AssociatedProject(Project project) {
		this.label = project.getLabel();
		this.id = project.getId();
		this.organism = project.getOrganism();
		this.createdDate = project.getCreatedDate();
	}

	public String getLabel() {
		return label;
	}

	public Long getId() {
		return id;
	}

	public String getOrganism() {
		return organism;
	}

	public Date getCreatedDate() {
		return createdDate;
	}
}
