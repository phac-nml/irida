package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Defines an AnalysisSubmission to a Galaxy execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public abstract class AnalysisSubmissionGalaxy implements AnalysisSubmission<RemoteWorkflowGalaxy> {
	
	private RemoteWorkflowGalaxy remoteWorkflow;
	private Set<SequenceFile> inputFiles;
	private String sequenceFileInputLabel;
	
	/**
	 * Builds a new AnalysisSubmissionGalaxy with the given information.
	 * @param inputFiles  A set of SequenceFiles to use for the analysis.
	 * @param sequenceFileInputLabel  A label within the RemoteWorkflow implementation for the sequence files.
	 * @param remoteWorkflow  A RemoteWorkflow implementation for this analysis.
	 */
	public AnalysisSubmissionGalaxy(Set<SequenceFile> inputFiles,
			String sequenceFileInputLabel,
			RemoteWorkflowGalaxy remoteWorkflow) {
		this.remoteWorkflow = remoteWorkflow;
		this.sequenceFileInputLabel = sequenceFileInputLabel;
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

	@Override
	public String getSequenceFileInputLabel() {
		return sequenceFileInputLabel;
	}

	public void setSequenceFileInputLabel(String sequenceFileInputLabel) {
		this.sequenceFileInputLabel = sequenceFileInputLabel;
	}

	public void setRemoteWorkflow(RemoteWorkflowGalaxy remoteWorkflow) {
		this.remoteWorkflow = remoteWorkflow;
	}

	public void setInputFiles(Set<SequenceFile> inputFiles) {
		this.inputFiles = inputFiles;
	}
}
