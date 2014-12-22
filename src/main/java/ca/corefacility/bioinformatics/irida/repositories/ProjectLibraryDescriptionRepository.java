package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.library.LibraryDescription;
import ca.corefacility.bioinformatics.irida.model.project.library.ProjectLibraryDescriptionJoin;

/**
 * Repository for managing {@link LibraryDescription}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface ProjectLibraryDescriptionRepository extends IridaJpaRepository<ProjectLibraryDescriptionJoin, Long> {

	/**
	 * Find all {@link LibraryDescription} for the specified {@link Project}.
	 * 
	 * @param project
	 *            the {@link Project} to load {@link LibraryDescription} for.
	 * @return the {@link LibraryDescription} for a {@link Project}.
	 */
	public Set<Join<Project, LibraryDescription>> findByProject(final Project project);

	/**
	 * Find the default {@link LibraryDescription} for the specified
	 * {@link Project}.
	 * 
	 * @param project
	 *            the {@link Project} to load the default
	 *            {@link LibraryDescription} for.
	 * @return the default {@link LibraryDescription} for the {@link Project}.
	 */
	@Query("from ProjectLibraryDescriptionJoin pldj where pldj.project = ?1 and pldj.defaultLibraryDescription = true")
	public Join<Project, LibraryDescription> findDefaultLibraryDescriptionForProject(final Project project);

	/**
	 * Remove the default status of any {@link LibraryDescription} assigned to
	 * the {@link Project}.
	 * 
	 * @param project
	 *            the {@link Project} to remove the default status from.
	 */
	@Modifying
	@Query("update ProjectLibraryDescriptionJoin pldj set pldj.defaultLibraryDescription = false where pldj.project = ?1")
	public void unsetDefaultLibraryDescriptionForProject(final Project project);
}
