package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;

/**
 * Service for managing {@link SequencingObject}s and relationships with related
 * objects
 */
public interface SequencingObjectService extends CRUDService<Long, SequencingObject> {

	/**
	 * Create a new {@link SequencingObject} associated with a {@link Sample}
	 * 
	 * @param seqObject
	 *            The {@link SequencingObject} to create
	 * @param sample
	 *            the {@link Sample} to associate it with
	 * @return a new {@link SampleSequencingObjectJoin} describing the
	 *         relationship
	 */
	public SampleSequencingObjectJoin createSequencingObjectInSample(SequencingObject seqObject, Sample sample);

	/**
	 * Get all the {@link SequencingObject}s associate with a given
	 * {@link Sample}
	 * 
	 * @param sample
	 *            The {@link Sample} to get sequences for
	 * @return A collection of {@link SampleSequencingObjectJoin}
	 */
	public Collection<SampleSequencingObjectJoin> getSequencingObjectsForSample(Sample sample);

	/**
	 * Get a collection of {@link SampleSequencingObjectJoin}s assocaited with a
	 * given {@link Sample} and of the given class type
	 * 
	 * @param sample
	 *            the {@link Sample} used in the join
	 * @param type
	 *            The type of {@link SequencingObject} that must be in the join
	 * @return a Collection of {@link SampleSequencingObjectJoin}
	 */
	public Collection<SampleSequencingObjectJoin> getSequencesForSampleOfType(Sample sample,
			Class<? extends SequencingObject> type);

	/**
	 * Read a {@link SequencingObject} and verify that it belongs to a given
	 * {@link Sample}.
	 * 
	 * @param sample
	 *            the {@link Sample} to get the {@link SequencingObject} for
	 * @param objectId
	 *            The {@link SequencingObject} ID
	 * @return A {@link SequencingObject} object
	 */
	public SequencingObject readSequencingObjectForSample(Sample sample, Long objectId);

	/**
	 * Gets a map of {@link SequencingObject}s and corresponding {@link Sample}
	 * s.
	 *
	 * @param sequenceFiles
	 *            A {@link Set} of {@link SequencingObject}s.
	 * @return A {@link Map} of between {@link Sample} and
	 *         {@link SequencingObject}.
	 * @throws DuplicateSampleException
	 *             If there is a duplicate sample.
	 */
	public <T extends SequencingObject> Map<Sample, T> getUniqueSamplesForSequenceFiles(Set<T> sequenceFiles)
			throws DuplicateSampleException;

	/**
	 * Get all the {@link SequencingObject}s associated with a given
	 * {@link SequencingRun}
	 * 
	 * @param sequencingRun
	 *            the run to get objects for
	 * @return a set of {@link SequencingObject}
	 */
	public Set<SequencingObject> getSequencingObjectsForSequencingRun(SequencingRun sequencingRun);

	/**
	 * Create a {@link SequencingObject} without running a
	 * {@link FileProcessingChain}. This method should be used when creating
	 * objects with existing {@link SequenceFile}s
	 * 
	 * @param object
	 *            the {@link SequencingObject} to create
	 * @return the created {@link SequencingObject}
	 */
	public SequencingObject createWithoutFileProcessor(SequencingObject object);
}
