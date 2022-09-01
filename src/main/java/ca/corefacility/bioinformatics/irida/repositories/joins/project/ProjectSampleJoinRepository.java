package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Repository for managing {@link ProjectUserJoin}.
 */
public interface ProjectSampleJoinRepository
		extends PagingAndSortingRepository<ProjectSampleJoin, Long>, JpaSpecificationExecutor<ProjectSampleJoin> {

	/**
	 * {@inheritDoc}
	 */
	@EntityGraph(value = "projectSampleMinimal", type = EntityGraphType.FETCH)
	public Page<ProjectSampleJoin> findAll(@Nullable Specification<ProjectSampleJoin> spec, Pageable pageable);

	/**
	 * Get a collection of the {@link Project}s related to a {@link Sample}
	 *
	 * @param sample The {@link Sample} to get the projects for
	 * @return A collection of {@link ProjectSampleJoin}s describing the project/sample link
	 */
	@Query("select j from ProjectSampleJoin j where j.sample = ?1")
	public List<Join<Project, Sample>> getProjectForSample(Sample sample);

	/**
	 * Get a specific {@link ProjectSampleJoin} for a {@link Project} and {@link Sample}
	 *
	 * @param project the {@link Project} to read from
	 * @param sample  the {@link Sample} to read
	 * @return The {@link ProjectSampleJoin} for these
	 */
	@Query("from ProjectSampleJoin j where j.project = ?1 and j.sample = ?2")
	public ProjectSampleJoin readSampleForProject(Project project, Sample sample);

	/**
	 * Get the {@link Sample}s associated with a {@link Project}
	 *
	 * @param project The {@link Project} to get {@link Sample}s from
	 * @return A List of {@link ProjectSampleJoin}s describing the project/sample relationship
	 */
	@Query("select j from ProjectSampleJoin j where j.project = ?1")
	public List<Join<Project, Sample>> getSamplesForProject(Project project);

	/**
	 * Get {@link Sample} in a {@link Project} given a list of Sample ids.
	 *
	 * @param project   {@link Project} to get samples for.
	 * @param sampleIds {@link Sample} ids
	 * @return List of {@link Sample}
	 */
	@Query("select j.sample from ProjectSampleJoin j where j.project = ?1 and j.sample.id in ?2")
	List<Sample> getSamplesInProject(Project project, List<Long> sampleIds);

	/**
	 * Get a list of the organism fields stored for all {@link Sample}s in a {@link Project}
	 *
	 * @param project the {@link Project} to get sample organisms for
	 * @return a list of sample organisms
	 */
	@Query("select DISTINCT(j.sample.organism) FROM ProjectSampleJoin j where j.project=?1")
	public List<String> getSampleOrganismsForProject(Project project);

	/**
	 * Count the number of {@link Sample}s in a given {@link Project}.
	 *
	 * @param project {@link Project} to count {@link Sample}s for
	 * @return number of samples
	 */
	@Query("select count(j.id) from ProjectSampleJoin j where j.project = ?1")
	public Long countSamplesForProject(Project project);

	/**
	 * Get a list of the {@link Sample} ids that are not owned by the given project
	 *
	 * @param project the {@link Project} to check
	 * @return a List of the sample IDs that are locked
	 */
	@Query("SELECT j.sample.id FROM ProjectSampleJoin j where j.owner=false AND j.project=?1")
	public List<Long> getLockedSamplesForProject(Project project);

	/**
	 * Calculate the coverage for a list of Samples within a Project.
	 * 
	 * @param project   {@link Project} to get sample coverage for.
	 * @param sampleIds {@link Sample} ids
	 * @return A List of {@link Tuple}s with first entry as the sample id and second entry as the coverage
	 */
	@Query("SELECT ps.sample.id, ROUND(SUM(qc.totalBases)/ps.project.genomeSize) FROM ProjectSampleJoin ps join SampleSequencingObjectJoin sso on sso.sample = ps.sample join CoverageQCEntry qc on qc.sequencingObject = sso.sequencingObject where ps.project=?1 and ps.sample.id in ?2 group by ps.sample.id")
	public List<Tuple> calculateCoverageForSamplesInProject(Project project, List<Long> sampleIds);
}
