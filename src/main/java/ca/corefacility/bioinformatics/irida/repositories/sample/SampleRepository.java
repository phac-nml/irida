package ca.corefacility.bioinformatics.irida.repositories.sample;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * A repository for storing Sample objects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SampleRepository extends PagingAndSortingRepository<Sample, Long>,
		RevisionRepository<Sample, Long, Integer> {
	/**
	 * Get a {@link Sample} with the given string sample identifier from a
	 * specific project.
	 * 
	 * @param project
	 *            The {@link Project} that the {@link Sample} belongs to.
	 * @param externalSampleId
	 *            The string sample identifier for a sample
	 * @return The {@link Sample} for this identifier
	 * @throws EntityNotFoundException
	 *             if a sample with this identifier doesn't exist
	 */
	@Query("select j.sample from ProjectSampleJoin j where j.project = ?1 and j.sample.externalSampleId = ?2")
	public Sample getSampleByExternalSampleId(Project p, String externalSampleId) throws EntityNotFoundException;
}
