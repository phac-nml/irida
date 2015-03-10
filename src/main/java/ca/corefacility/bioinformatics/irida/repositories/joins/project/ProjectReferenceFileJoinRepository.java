package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectReferenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for interacting with {@link ProjectReferenceFileJoin}.
 * 
 *
 */
public interface ProjectReferenceFileJoinRepository extends IridaJpaRepository<ProjectReferenceFileJoin, Long> {

	/**
	 * Get the collection of {@link ReferenceFile} for the specified
	 * {@link Project}.
	 * 
	 * @param project
	 *            the {@link Project} to get {@link ReferenceFile} for.
	 * @return the collection of {@link ReferenceFile} for the specified
	 *         {@link Project}.
	 */
	@Query("select j from ProjectReferenceFileJoin j where j.project = ?1")
	public List<Join<Project, ReferenceFile>> findReferenceFilesForProject(Project project);

	/**
	 * Get the collection of {@link ReferenceFile} for the specified
	 * {@link Project}.
	 * 
	 * @param project
	 *            the {@link Project} to get {@link ReferenceFile} for.
	 * @return the collection of {@link ReferenceFile} for the specified
	 *         {@link Project}.
	 */
	@Query("select j from ProjectReferenceFileJoin j where j.referenceFile = ?1")
	public List<Join<Project, ReferenceFile>> findProjectsForReferenceFile(ReferenceFile referenceFile);
}
