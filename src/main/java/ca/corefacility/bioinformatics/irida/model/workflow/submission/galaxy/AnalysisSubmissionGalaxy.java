package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy;

import java.util.Set;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Defines an AnalysisSubmission to a Galaxy execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <R> The RemoteWorkflow to submit.
 */
public abstract class AnalysisSubmissionGalaxy<R extends RemoteWorkflowGalaxy>
	implements AnalysisSubmission<R> {
	
	private R remoteWorkflow;
	private Set<SequenceFile> inputFiles;
	
	private GalaxyAnalysisId remoteAnalysisId;
	private WorkflowOutputs outputs;
	
	/**
	 * Builds a new AnalysisSubmissionGalaxy with the given information.
	 * @param inputFiles  A set of SequenceFiles to use for the analysis.
	 * @param remoteWorkflow  A RemoteWorkflow implementation for this analysis.
	 */
	public AnalysisSubmissionGalaxy(Set<SequenceFile> inputFiles,
			R remoteWorkflow) {
		this.remoteWorkflow = remoteWorkflow;
		this.inputFiles = inputFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public R getRemoteWorkflow() {
		return remoteWorkflow;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<SequenceFile> getInputFiles() {
		return inputFiles;
	}

	public void setRemoteWorkflow(R remoteWorkflow) {
		this.remoteWorkflow = remoteWorkflow;
	}

	public void setInputFiles(Set<SequenceFile> inputFiles) {
		this.inputFiles = inputFiles;
	}
	
	public void setRemoteAnalysisId(GalaxyAnalysisId remoteAnalysisId) {
		this.remoteAnalysisId = remoteAnalysisId;
	}

	public GalaxyAnalysisId getRemoteAnalysisId() {
		return remoteAnalysisId;
	}

	public void setOutputs(WorkflowOutputs outputs) {
		this.outputs = outputs;
	}

	public WorkflowOutputs getOutputs() {
		return outputs;
	}
}
