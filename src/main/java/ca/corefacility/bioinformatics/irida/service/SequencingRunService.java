package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Service layer for SequencingRun objects
 * 
 */
public interface SequencingRunService extends CRUDService<Long, SequencingRun> {
	/**
	 * Create a join between a {@link SequenceFile} to a {@link SequencingRun}
	 * 
	 * @param run
	 *            The {@link SequencingRun}
	 * @param file
	 *            The {@link SequenceFile}
	 */
	public void addSequenceFileToSequencingRun(SequencingRun run, SequenceFile file);

	/**
	 * Get the {@link SequencingRun} for the given {@link SequenceFile}
	 * 
	 * @param file
	 *            The {@link SequenceFile} for to get the run for
	 * @return A SequencingRun for the file
	 */
	public SequencingRun getSequencingRunForSequenceFile(SequenceFile file);
}
