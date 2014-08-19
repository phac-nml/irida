package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;

/**
 * Defines a submission to an AnalysisService for executing a remote workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <T> Defines the RemoteWorkflow implementing this analysis.
 */
public interface AnalysisSubmission<T extends RemoteWorkflow> {

	/**
	 * Gets a RemoteWorkflow implementing this submission.
	 * @return  A RemoteWorkflow implementing this submission.
	 */
	public T getRemoteWorkflow();
	
	/**
	 * Gets the set of input sequence files.
	 * @return  The set of input sequence files.
	 */
	public Set<SequenceFile> getInputFiles();
}
