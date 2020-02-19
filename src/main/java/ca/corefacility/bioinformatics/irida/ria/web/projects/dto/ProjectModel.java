package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Representation of a {@link Project} used in the UI Projects listing table.
 */
public class ProjectModel {
	private Long id;
	private String name;
	private String organism;
	private Long samples;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isRemote;

	public ProjectModel(Project project, Long samples) {
		this.id = project.getId();
		this.name = project.getName();
		this.organism = project.getOrganism();
		this.samples = samples;
		this.createdDate = project.getCreatedDate();
		this.modifiedDate = project.getModifiedDate();
		this.isRemote = project.isRemote();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOrganism() {
		return organism;
	}

	public long getSamples() {
		return samples;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public boolean isRemote() {
		return isRemote;
	}
}
