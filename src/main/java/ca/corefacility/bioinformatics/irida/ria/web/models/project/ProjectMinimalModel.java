package ca.corefacility.bioinformatics.irida.ria.web.models.project;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.models.MinimalModel;

public class ProjectMinimalModel extends MinimalModel {

	public ProjectMinimalModel(Project project) {
		super(project.getId(), project.getName());
	}
}
