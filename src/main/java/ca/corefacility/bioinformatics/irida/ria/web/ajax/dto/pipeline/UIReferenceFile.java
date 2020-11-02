package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

public class UIReferenceFile extends AjaxResponse {
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

	public UIReferenceFile(ReferenceFile file) {
		this.id = file.getId();
		this.name = file.getLabel();
		this.projectId = null;
		this.projectName = null;
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