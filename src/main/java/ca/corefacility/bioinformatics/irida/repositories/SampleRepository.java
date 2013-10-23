package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;

/**
 * A repository for storing Sample objects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface SampleRepository extends PagingAndSortingRepository<Sample, Long> {


	/**
	 * Get the {@link Sample} that owns the {@link SequenceFile}.
	 * 
	 * @param sequenceFile
	 *            the file to find the {@link Sample} for.
	 * @return the {@link Sample} that owns the file.
	 */
	public SampleSequenceFileJoin getSampleForSequenceFile(SequenceFile sequenceFile);

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
	public Sample getSampleByExternalSampleId(Project p, String externalSampleId) throws EntityNotFoundException;
}
