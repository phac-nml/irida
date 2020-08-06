package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;

public class UIReferenceFile {
	private final Long id;
	private final String name;
	private final String projectName;
	private final Long projectId;

	public UIReferenceFile(Join<Project, ReferenceFile> join) {
		Project project = join.getSubject();
		ReferenceFile file = join.getObject();
		this.id = file.getId();
		this.name = file.getLabel();
		this.projectName = project.getName();
		this.projectId = project.getId();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getProjectName() {
		return projectName;
	}

	public Long getProjectId() {
		return projectId;
	}
}

