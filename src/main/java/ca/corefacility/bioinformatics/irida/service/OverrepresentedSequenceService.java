package ca.corefacility.bioinformatics.irida.service;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface OverrepresentedSequenceService extends CRUDService<Long, OverrepresentedSequence> {
	/**
	 * Get all {@link OverrepresentedSequence}s for the specified
	 * {@link SequenceFile}.
	 * 
	 * @param sequenceFile
	 *            the file for which to load {@link OverrepresentedSequence}.
	 * @return the {@link OverrepresentedSequence}s for the {@link SequenceFile}
	 */
	public Set<OverrepresentedSequence> getOverrepresentedSequencesForSequenceFile(SequenceFile sequenceFile);
}
