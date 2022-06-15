package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Service for managing {@link SequencingObject}s and relationships with related objects
 */
public interface SequencingObjectService extends CRUDService<Long, SequencingObject> {

	/**
	 * Create a new {@link SequencingObject} associated with a {@link Sample}
	 *
	 * @param seqObject The {@link SequencingObject} to create
	 * @param sample    the {@link Sample} to associate it with
	 * @return a new {@link SampleSequencingObjectJoin} describing the relationship
	 */
	public SampleSequencingObjectJoin createSequencingObjectInSample(SequencingObject seqObject, Sample sample);

	/**
	 * Get all the {@link SequencingObject}s associate with a given {@link Sample}
	 *
	 * @param sample The {@link Sample} to get sequences for
	 * @return A collection of {@link SampleSequencingObjectJoin}
	 */
	public Collection<SampleSequencingObjectJoin> getSequencingObjectsForSample(Sample sample);

	/**
	 * Get a collection of {@link SampleSequencingObjectJoin}s assocaited with a given {@link Sample} and of the given
	 * class type
	 *
	 * @param sample the {@link Sample} used in the join
	 * @param type   The type of {@link SequencingObject} that must be in the join
	 * @return a Collection of {@link SampleSequencingObjectJoin}
	 */
	public Collection<SampleSequencingObjectJoin> getSequencesForSampleOfType(Sample sample,
			Class<? extends SequencingObject> type);

	/**
	 * Read a {@link SequencingObject} and verify that it belongs to a given {@link Sample}.
	 *
	 * @param sample   the {@link Sample} to get the {@link SequencingObject} for
	 * @param objectId The {@link SequencingObject} ID
	 * @return A {@link SequencingObject} object
	 */
	public SequencingObject readSequencingObjectForSample(Sample sample, Long objectId);

	/**
	 * Gets a map of {@link SequencingObject}s and corresponding {@link Sample} s.
	 *
	 * @param sequenceFiles A {@link Set} of {@link SequencingObject}s.
	 * @param <T>           The type of sequencing object which should be returned
	 * @return A {@link Map} of between {@link Sample} and {@link SequencingObject}.
	 * @throws DuplicateSampleException If there is a duplicate sample.
	 */
	public <T extends SequencingObject> Map<Sample, T> getUniqueSamplesForSequencingObjects(Set<T> sequenceFiles)
			throws DuplicateSampleException;

	/**
	 * Get all the {@link SequencingObject}s associated with a given {@link SequencingRun}
	 *
	 * @param sequencingRun the run to get objects for
	 * @return a set of {@link SequencingObject}
	 */
	public Set<SequencingObject> getSequencingObjectsForSequencingRun(SequencingRun sequencingRun);

	/**
	 * Update the {@link RemoteStatus} for a {@link SequencingObject}
	 *
	 * @param id           ID of the objet to update
	 * @param remoteStatus a RemoteStatus to set
	 * @return the updated {@link SequencingObject}
	 */
	public SequencingObject updateRemoteStatus(Long id, RemoteStatus remoteStatus);

	/**
	 * Get the set of {@link SequencingObject}s associated with a given {@link AnalysisSubmission}
	 *
	 * @param submission the {@link AnalysisSubmission}
	 * @return the associated {@link SequencingObject}s
	 */
	public Set<SequencingObject> getSequencingObjectsForAnalysisSubmission(AnalysisSubmission submission);

	/**
	 * Get all {@link SequencingObject}s of a given type associated with an {@link AnalysisSubmission}
	 *
	 * @param submission the {@link AnalysisSubmission}
	 * @param type       the class type of {@link SequencingObject} to return
	 * @param <Type>     a class type extending {@link SequencingObject}
	 * @return a set of the requested type
	 */
	public <Type extends SequencingObject> Set<Type> getSequencingObjectsOfTypeForAnalysisSubmission(
			AnalysisSubmission submission, Class<Type> type);

	/**
	 * Concatenate a collection of {@link SequencingObject}s and save back to a {@link Sample}
	 *
	 * @param toJoin          the {@link SequencingObject}s to concatenate
	 * @param filename        The name of the file to create on concatenation
	 * @param targetSample    the {@link Sample} to save to
	 * @param removeOriginals Whether to remove the original {@link SequencingObject}s from the sample
	 * @return the new {@link SampleSequencingObjectJoin}
	 * @throws ConcatenateException if there was an error concatenating the sequences
	 */
	public SampleSequencingObjectJoin concatenateSequences(List<SequencingObject> toJoin, String filename,
			Sample targetSample, boolean removeOriginals) throws ConcatenateException;
}
