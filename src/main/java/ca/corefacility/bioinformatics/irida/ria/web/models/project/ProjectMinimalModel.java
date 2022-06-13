package ca.corefacility.bioinformatics.irida.ria.web.models.project;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.models.MinimalModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.ModelKeys;

/**
 * Bare minimal representation for a project in the UI
 */
public class ProjectMinimalModel extends MinimalModel {

	public ProjectMinimalModel(Project project) {
		super(project.getId(), project.getName(), ModelKeys.Project.label);
	}
}
