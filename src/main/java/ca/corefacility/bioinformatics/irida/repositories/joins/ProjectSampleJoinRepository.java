package ca.corefacility.bioinformatics.irida.repositories.joins;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;

/**
 * Repository for managing {@link ProjectUserJoin}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public interface ProjectSampleJoinRepository extends CrudRepository<ProjectSampleJoin, Long> {
	/**
	 * Get a collection of the {@link Project}s related to a {@link Sample}
	 * 
	 * @param sample
	 *            The {@link Sample} to get the projects for
	 * @return A collection of {@link ProjectSampleJoin}s describing the
	 *         project/sample link
	 */
	@Query("select j from ProjectSampleJoin where j.sample = ?1")
	public List<Join<Project, Sample>> getProjectForSample(Sample sample);

	/**
	 * Remove a {@link Sample} from a {@link Project}
	 * 
	 * @param project
	 *            The {@link Project} to remove from
	 * @param sample
	 *            The {@link Sample} to remove
	 */
	@Query("delete from ProjectSampleJoin where j.project = ?1 and j.sample = ?2")
	public void removeSampleFromProject(Project project, Sample sample);
}
