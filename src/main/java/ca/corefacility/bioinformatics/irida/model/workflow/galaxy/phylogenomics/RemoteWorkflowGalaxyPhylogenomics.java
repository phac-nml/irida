package ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;

public class RemoteWorkflowGalaxyPhylogenomics extends RemoteWorkflowGalaxy {

	private String inputSequenceFilesId;
	private String inputReferenceFilesId;
	
	public RemoteWorkflowGalaxyPhylogenomics(String workflowId,
			String workflowChecksum, String inputSequenceFilesId,
			String inputReferenceFilesId) {
		super(workflowId, workflowChecksum);
		this.inputSequenceFilesId = inputSequenceFilesId;
		this.inputReferenceFilesId = inputReferenceFilesId;
	}

	public String getInputSequenceFilesId() {
		return inputSequenceFilesId;
	}

	public String getInputReferenceFilesId() {
		return inputReferenceFilesId;
	}
}
