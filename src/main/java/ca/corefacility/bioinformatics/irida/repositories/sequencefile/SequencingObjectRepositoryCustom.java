package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Custom methods for {@link SequencingObjectRepository}
 */
public interface SequencingObjectRepositoryCustom {

	/**
	 * Update a sequencing object's file processing state with the given status
	 *
	 * @param objectId        ID of the sequencing object
	 * @param processor       File processor id string to set
	 * @param processingState processing state to set
	 */
	public int markFileProcessor(Long objectId, String processor, SequencingObject.ProcessingState processingState);
}
