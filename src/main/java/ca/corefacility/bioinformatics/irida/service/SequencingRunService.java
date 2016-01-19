package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
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

	/**
	 * Get the {@link SequencingRun} for the given {@link SequenceFile}
	 * 
	 * @param file
	 *            The {@link SequenceFile} for to get the run for
	 * @return A SequencingRun for the file
	 */
	public SequencingRun getSequencingRunForSequenceFile(SequenceFile file);
}
