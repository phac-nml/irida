package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;

import java.util.List;

/**
 * A repository for storing Sample objects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface SampleRepository extends CRUDRepository<Long, Sample> {

	/**
	 * Get the {@link Sample}s associated with a {@link Project}
	 * 
	 * @param project
	 *            The {@link Project} to get {@link Sample}s from
	 * @return A List of {@link ProjectSampleJoin}s describing the
	 *         project/sample relationship
	 */
	public List<ProjectSampleJoin> getSamplesForProject(Project project);

	/**
	 * Get the {@link Sample} that owns the {@link SequenceFile}.
	 * 
	 * @param sequenceFile
	 *            the file to find the {@link Sample} for.
	 * @return the {@link Sample} that owns the file.
	 */
	public SampleSequenceFileJoin getSampleForSequenceFile(SequenceFile sequenceFile);
        
        
    /**
     * Get a {@link Sample} with the given string sample identifier
     * @param sampleId The string sample identifier for a sample
     * @return The {@link Sample} for this identifier
     * @throws EntityNotFoundException if a sample with this identifier doesn't exist
     */
    public Sample getSampleBySampleId(String sampleId) throws EntityNotFoundException;
}
