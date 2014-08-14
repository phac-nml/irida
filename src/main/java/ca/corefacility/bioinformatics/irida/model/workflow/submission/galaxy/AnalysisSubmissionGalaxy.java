package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

public abstract class AnalysisSubmissionGalaxy implements AnalysisSubmission<RemoteWorkflowGalaxy> {
	
	private RemoteWorkflowGalaxy remoteWorkflow;
	private Set<SequenceFile> inputFiles;
	
	public AnalysisSubmissionGalaxy(Set<SequenceFile> inputFiles, RemoteWorkflowGalaxy remoteWorkflow) {
		this.remoteWorkflow = remoteWorkflow;
		this.inputFiles = inputFiles;
	}

	@Override
	public RemoteWorkflowGalaxy getRemoteWorkflow() {
		return remoteWorkflow;
	}

	@Override
	public Set<SequenceFile> getInputFiles() {
		return inputFiles;
	}

	public void setRemoteWorkflow(RemoteWorkflowGalaxy remoteWorkflow) {
		this.remoteWorkflow = remoteWorkflow;
	}

	public void setInputFiles(Set<SequenceFile> inputFiles) {
		this.inputFiles = inputFiles;
	}
}
