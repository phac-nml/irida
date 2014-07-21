package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Repository for managing {@link ProjectUserJoin}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * 
 */
public interface ProjectSampleJoinRepository extends PagingAndSortingRepository<ProjectSampleJoin, Long> {
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
	 * Remove a {@link Sample} from a {@link Project}
	 * 
	 * @param project
	 *            The {@link Project} to remove from
	 * @param sample
	 *            The {@link Sample} to remove
	 */
	@Modifying
	@Query("delete from ProjectSampleJoin j where j.project = ?1 and j.sample = ?2")
	public void removeSampleFromProject(Project project, Sample sample);

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

	/**
	 * Get a {@link Page} of the {@link Sample}s associated with a
	 * {@link Project}
	 * 
	 * @param project
	 *            The {@link Project} to get {@link Sample}s from
	 * @param page
	 *            The page request
	 * @return A page of {@link ProjectSampleJoin}s
	 */
	@Query("select j from ProjectSampleJoin j where j.project = ?1")
	public Page<Join<Project, Sample>> pageSamplesForProject(Project project, Pageable page);
}
