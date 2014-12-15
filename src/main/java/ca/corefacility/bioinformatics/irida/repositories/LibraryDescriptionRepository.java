package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.library.LibraryDescription;

/**
 * Repository for managing {@link LibraryDescription}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface LibraryDescriptionRepository extends IridaJpaRepository<LibraryDescription, Long> {

	/**
	 * Find all {@link LibraryDescription} for the specified {@link Project}.
	 * 
	 * @param project
	 *            the {@link Project} to load {@link LibraryDescription} for.
	 * @return the {@link LibraryDescription} for a {@link Project}.
	 */
	public Set<LibraryDescription> findByProject(final Project project);
}
