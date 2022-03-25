package ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples;

import ca.corefacility.bioinformatics.irida.model.project.Project;

public class ProjectObject {
	private Long id;
	private String name;

	public ProjectObject(Project project) {
		this.id = project.getId();
		this.name = project.getName();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
