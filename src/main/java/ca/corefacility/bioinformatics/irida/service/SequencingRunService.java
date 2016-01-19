package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Service layer for SequencingRun objects
 * 
 */
public interface SequencingRunService extends CRUDService<Long, SequencingRun> {
	/**
	 * Create a join between a {@link SequencingObject} to a
	 * {@link SequencingRun}
	 * 
	 * @param run
	 *            The {@link SequencingRun}
	 * @param seqobject
	 *            The {@link SequencingObject}
	 */
	public void addSequenceFileToSequencingRun(SequencingRun run, SequencingObject seqobject);

}
