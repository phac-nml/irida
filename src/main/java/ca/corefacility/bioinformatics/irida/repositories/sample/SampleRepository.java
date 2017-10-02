package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for storing Sample objects
 * 
 */
public interface SampleRepository extends IridaJpaRepository<Sample, Long> {
	/**
	 * Get a {@link Sample} with the given string sample identifier from a
	 * specific project.
	 * 
	 * @param p
	 *            The {@link Project} that the {@link Sample} belongs to.
	 * @param sampleName
	 *            The string sample name for a sample
	 * @return The {@link Sample} for this identifier
	 * @throws EntityNotFoundException
	 *             if a sample with this identifier doesn't exist
	 */
	@Query("select j.sample from ProjectSampleJoin j where j.project = ?1 and j.sample.sampleName = ?2")
	public Sample getSampleBySampleName(Project p, String sampleName) throws EntityNotFoundException;

	/**
	 * Get a {@link Page} of {@link Sample}s based on a list of {@link Sample}
	 * names
	 *
	 * @param project
	 *            The {@link Project} that the {@link Sample} belongs to.
	 * @param sampleNames
	 *            A {@link List} of {@link String} sample names
	 * @param pageable
	 *            {@link Pageable} information about which {@link Sample}s to
	 *            retrun
	 *
	 * @return The {@link Page} of {@link ProjectSampleJoin}
	 */
	@Query("select j from ProjectSampleJoin j where j.project = ?1 and j.sample.sampleName in ?2")
	public Page<ProjectSampleJoin> findSampleByNameInProject(Project project, List<String> sampleNames,
			Pageable pageable);

	/**
	 * Get the {@link Sample}s associated with a given
	 * {@link AnalysisSubmission}
	 * 
	 * @param analysisSubmissionId
	 *            the {@link AnalysisSubmission} id.
	 * @return the set of associated {@link Sample}s
	 */
	@Query(value = "SELECT s.* from sample s INNER JOIN sample_sequencingobject sso ON s.id = sso.sample_id INNER JOIN analysis_submission_sequencing_object asso ON sso.sequencingobject_id = asso.sequencing_object_id WHERE asso.analysis_submission_id = ?1", nativeQuery = true)
	public Set<Sample> findSamplesForAnalysisSubmission(Long analysisSubmissionId);
}
