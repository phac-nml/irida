package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Custom repository methods for {@link Project}s
 */
public interface ProjectRepositoryCustom {

	/**
	 * Update the modifiedDate in a {@link Project} to the specified date.
	 *
	 * @param project      The {@link Project} to update
	 * @param modifiedDate The new {@link Date}
	 */
	void updateProjectModifiedDate(Project project, Date modifiedDate);
}
