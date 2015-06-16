package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Repository for managing {@link ProjectUserJoin}.
 * 
 * 
 */
public interface ProjectSampleJoinRepository extends PagingAndSortingRepository<ProjectSampleJoin, Long>,
		JpaSpecificationExecutor<ProjectSampleJoin> {
	/**
	 * Get a collection of the {@link Project}s related to a {@link Sample}
	 * 
	 * @param sample
	 *            The {@link Sample} to get the projects for
	 * @return A collection of {@link ProjectSampleJoin}s describing the
	 *         project/sample link
	 */
	@Query("select j from ProjectSampleJoin j where j.sample = ?1")
	public List<Join<Project, Sample>> getProjectForSample(Sample sample);

	/**
	 * Get a specific {@link ProjectSampleJoin} for a {@link Project} and
	 * {@link Sample}
	 * 
	 * @param project
	 *            the {@link Project} to read from
	 * @param sample
	 *            the {@link Sample} to read
	 * @return The {@link ProjectSampleJoin} for these
	 */
	@Query("from ProjectSampleJoin j where j.project = ?1 and j.sample = ?2")
	public ProjectSampleJoin readSampleForProject(Project project, Sample sample);

	/**
	 * Get the {@link Sample}s associated with a {@link Project}
	 * 
	 * @param project
	 *            The {@link Project} to get {@link Sample}s from
	 * @return A List of {@link ProjectSampleJoin}s describing the
	 *         project/sample relationship
	 */
	@Query("select j from ProjectSampleJoin j where j.project = ?1")
	public List<Join<Project, Sample>> getSamplesForProject(Project project);
	
	@Query("select count(j.id) from ProjectSampleJoin j where j.project = ?1")
	public Long countSamplesForProject(Project project);

}
