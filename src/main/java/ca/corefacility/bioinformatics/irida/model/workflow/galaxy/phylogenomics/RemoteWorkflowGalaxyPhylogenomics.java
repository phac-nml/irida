package ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;

public class RemoteWorkflowGalaxyPhylogenomics extends RemoteWorkflowGalaxy {

	private String inputSequenceFilesLabel;
	private String inputReferenceFilesLabel;
	
	public RemoteWorkflowGalaxyPhylogenomics(String workflowId,
			String workflowChecksum, String inputSequenceFilesLabel,
			String inputReferenceFilesLabel) {
		super(workflowId, workflowChecksum);
		this.inputSequenceFilesLabel = inputSequenceFilesLabel;
		this.inputReferenceFilesLabel = inputReferenceFilesLabel;
	}

	public String getInputSequenceFilesLabel() {
		return inputSequenceFilesLabel;
	}

	public String getInputReferenceFilesLabel() {
		return inputReferenceFilesLabel;
	}
}
