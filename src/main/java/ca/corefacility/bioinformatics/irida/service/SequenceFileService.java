package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;

/**
 * Service for managing {@link SequenceFile} entities.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SequenceFileService extends CRUDService<Long, SequenceFile> {
	/**
	 * Persist the {@link SequenceFile} to the database and create a new
	 * relationship between the {@link SequenceFile} and a {@link Sample}
	 * 
	 * @param sequenceFile
	 *            the {@link SequenceFile} to be persisted.
	 * @param sample
	 *            The sample to add the file to
	 * @return the {@link Join} between the {@link SequenceFile} and its
	 *         {@link Sample}.
	 */
	public Join<Sample, SequenceFile> createSequenceFileInSample(SequenceFile sequenceFile, Sample sample);

	/**
	 * Get a {@link List} of {@link SequenceFile} references for a specific
	 * {@link Sample}.
	 * 
	 * @param sample
	 *            the {@link Sample} to get the {@link SequenceFile} references
	 *            from.
	 * @return the references to {@link SequenceFile}.
	 */
	public List<Join<Sample, SequenceFile>> getSequenceFilesForSample(Sample sample);

	/**
	 * Get a {@link List} of {@link SequenceFile} references for a specific
	 * {@link MiseqRun}.
	 * 
	 * @param miseqRun
	 *            the {@link MiseqRun} to get the {@link SequenceFile}
	 *            references from.
	 * @return the references to {@link SequenceFile}.
	 */
	public List<Join<MiseqRun, SequenceFile>> getSequenceFilesForMiseqRun(MiseqRun miseqRun);

	/**
	 * Add an {@link OverrepresentedSequence} to a {@link SequenceFile}.
	 * 
	 * @param sequenceFile
	 * @param sequence
	 */
	public void addOverrepresentedSequenceToSequenceFile(SequenceFile sequenceFile, OverrepresentedSequence sequence);
}
