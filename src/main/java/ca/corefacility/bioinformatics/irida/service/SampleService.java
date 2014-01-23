package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;

/**
 * A service class for working with samples.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SampleService extends CRUDService<Long, Sample> {

	/**
	 * Add a {@link SequenceFile} to a {@link Sample}.
	 * 
	 * @param sample
	 *            the {@link Sample} that the {@link SequenceFile} belongs to.
	 * @param sampleFile
	 *            the {@link SequenceFile} that we're adding.
	 * @return the {@link Relationship} created between the two entities.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public Join<Sample, SequenceFile> addSequenceFileToSample(Sample sample, SequenceFile sampleFile);

	/**
	 * Get a specific instance of a {@link Sample} that belongs to a
	 * {@link Project}. If the {@link Sample} is not associated to the
	 * {@link Project} (i.e., no {@link Relationship} is shared between the
	 * {@link Sample} and {@link Project}, then an
	 * {@link EntityNotFoundException} will be thrown.
	 * 
	 * @param project
	 *            the {@link Project} to get the {@link Sample} for.
	 * @param identifier
	 *            the {@link Identifier} of the {@link Sample}
	 * @return the {@link Sample} as requested
	 * @throws EntityNotFoundException
	 *             if no {@link Relationship} exists between {@link Sample} and
	 *             {@link Project}.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Sample getSampleForProject(Project project, Long identifier) throws EntityNotFoundException;

	/**
	 * Get the list of {@link Sample} that belongs to a specific project.
	 * 
	 * @param p
	 *            the {@link Project} to get samples for.
	 * @return the collection of samples for the {@link Project}.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public List<Join<Project, Sample>> getSamplesForProject(Project p);

	/**
	 * Get the {@link Sample} for the given ID
	 * 
	 * @param p
	 *            the {@link Project} that the {@link Sample} belongs to.
	 * @param sampleId
	 *            The id for the requested sample
	 * @return A {@link Sample} with the given ID
	 */
	public Sample getSampleByExternalSampleId(Project p, String sampleId);

	/**
	 * Move an instance of a {@link SequenceFile} associated with a
	 * {@link Sample} to its parent {@link Project}.
	 * 
	 * @param sample
	 *            the {@link Sample} from which we're moving the
	 *            {@link SequenceFile}.
	 * @param sequenceFile
	 *            the {@link SequenceFile} that we're moving.
	 * @return the new relationship between the {@link Project} and
	 *         {@link SequenceFile}.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public void removeSequenceFileFromSample(Sample sample, SequenceFile sequenceFile);

	/**
	 * Merge multiple samples into one. Merging samples copies the
	 * {@link SequenceFile} references from the set of samples into one sample.
	 * The collection of samples in <code>toMerge</code> are marked as deleted.
	 * All samples must be associated with the specified project. The
	 * relationship between each sample in <code>toMerge</code> and the project
	 * p will be deleted.
	 * 
	 * @param p
	 *            the {@link Project} that all samples must belong to.
	 * @param mergeInto
	 *            the {@link Sample} to merge other samples into.
	 * @param toMerge
	 *            the collection of {@link Sample} to merge.
	 * @return the completely merged {@link Sample} (the persisted version of
	 *         <code>mergeInto</code>).
	 */
	public Sample mergeSamples(Project p, Sample mergeInto, Sample... toMerge);
}
