package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.project.Project;

public interface ProjectRepositoryCustom {

	void updateProjectModifiedDate(Project project);
}
