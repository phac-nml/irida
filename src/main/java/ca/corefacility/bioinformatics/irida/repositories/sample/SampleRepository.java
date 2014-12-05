package ca.corefacility.bioinformatics.irida.repositories.sample;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for storing Sample objects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SampleRepository extends IridaJpaRepository<Sample, Long> {
	/**
	 * Get a {@link Sample} with the given string sample identifier from a
	 * specific project.
	 * 
	 * @param project
	 *            The {@link Project} that the {@link Sample} belongs to.
	 * @param sequencerSampleId
	 *            The string sample identifier for a sample
	 * @return The {@link Sample} for this identifier
	 * @throws EntityNotFoundException
	 *             if a sample with this identifier doesn't exist
	 */
	@Query("select j.sample from ProjectSampleJoin j where j.project = ?1 and j.sample.sequencerSampleId = ?2")
	public Sample getSampleBySequencerSampleId(Project p, String sequencerSampleId) throws EntityNotFoundException;
}
