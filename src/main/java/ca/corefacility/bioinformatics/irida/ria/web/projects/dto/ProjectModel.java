package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableModel;

/**
 * Representation of a {@link Project} used in the UI Projects listing table.
 */
public class ProjectModel extends TableModel {
	private String organism;
	private Long samples;
	private boolean isRemote;

	public ProjectModel(Project project, Long samples) {
		super(project.getId(), project.getName(), project.getCreatedDate(), project.getModifiedDate());
		this.organism = project.getOrganism();
		this.samples = samples;
		this.isRemote = project.isRemote();
	}

	public String getOrganism() {
		return organism;
	}

	public long getSamples() {
		return samples;
	}

	public boolean isRemote() {
		return isRemote;
	}
}
