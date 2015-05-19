package ca.corefacility.bioinformatics.irida.service.upload;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;

/**
 * Used to convert IRIDA Samples to those that can be uploaded into a remote
 * site.
 * 
 *
 */
public interface UploadSampleConversionService {

	/**
	 * Converts the passed {@link Sample} object to an {@link UploadSample}.
	 *
	 * @param sample
	 *            The sample to convert
	 * @return The corresponding UploadSample.
	 */
	public UploadSample convertToUploadSample(Sample sample);

	/**
	 * Converts a set of samples to UploadSamples to be uploaded to Galaxy.
	 *
	 * @param samples
	 *            The samples to upload.
	 * @return A set of UploadSamples.
	 */
	public Set<UploadSample> convertToUploadSamples(Set<Sample> samples);

	/**
	 * Converts a list of samples to UploadSamples to be uploaded to Galaxy.
	 *
	 * @param samples
	 *            The samples to upload.
	 * @return A set of UploadSamples.
	 */
	public Set<UploadSample> convertToUploadSamples(Sample... samples);

	/**
	 * Converts a list of {@link SequenceFile}s to {@link UploadSample}s to be
	 * uploaded to Galaxy.
	 *
	 * @param sequenceFiles
	 *            The sequence files to upload.
	 * @return A set of {@link UploadSample}s.
	 * @throws EntityNotFoundException
	 *             If information could not be found for the passed sequence
	 *             files.
	 */
	public Set<UploadSample> convertSequenceFilesToUploadSamples(
			Set<SequenceFile> sequenceFiles) throws EntityNotFoundException;

	/**
	 * Converts a list of {@link SequenceFile} by ids to {@link UploadSample}s
	 * to be uploaded to Galaxy.
	 *
	 * @param sequenceFileIds
	 *            The ids of the {@link SequenceFile}s to upload.
	 * @return A set of {@link UploadSample}s.
	 * @throws EntityNotFoundException
	 *             If information could not be found for the passed sequence
	 *             files.
	 */
	public Set<UploadSample> convertSequenceFilesByIdToUploadSamples(
			Set<Long> sequenceFileIds) throws EntityNotFoundException;

	/**
	 * Get the samples for a specific project, identified by its project id.
	 *
	 * @param projectId
	 *            The project ID
	 * @return A set of UploadSamples.
	 */
	public Set<UploadSample> getUploadSamplesForProject(Long projectId);
}
