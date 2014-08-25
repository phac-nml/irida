package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy;

import java.util.Set;

import javax.persistence.Transient;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

/**
 * Defines an AnalysisSubmission to a Galaxy execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <R> The RemoteWorkflow to submit.
 */
public abstract class AnalysisSubmissionGalaxy<R extends RemoteWorkflowGalaxy>
	extends AnalysisSubmission<R> {
	
	@Transient
	private WorkflowOutputs outputs;
	
	protected AnalysisSubmissionGalaxy() {
	}
	
	/**
	 * Builds a new AnalysisSubmissionGalaxy with the given information.
	 * @param inputFiles  A set of SequenceFiles to use for the analysis.
	 * @param remoteWorkflow  A RemoteWorkflow implementation for this analysis.
	 */
	public AnalysisSubmissionGalaxy(Set<SequenceFile> inputFiles,
			R remoteWorkflow) {
		super(inputFiles, remoteWorkflow);
	}

	public WorkflowOutputs getOutputs() {
		return outputs;
	}

	public void setOutputs(WorkflowOutputs outputs) {
		this.outputs = outputs;
	}
}
