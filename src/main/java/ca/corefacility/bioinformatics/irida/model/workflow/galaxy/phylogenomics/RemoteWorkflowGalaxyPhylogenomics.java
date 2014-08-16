package ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;

/**
 * A RemoteWorkflow for a phylogenomics analysis in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class RemoteWorkflowGalaxyPhylogenomics extends RemoteWorkflowGalaxy {

	private String inputSequenceFilesLabel;
	private String inputReferenceFileLabel;
	
	/**
	 * Creates a new RemoteWorkflowGalaxyPhylogenomics.
	 * @param workflowId The ID of the implementing workflow.
	 * @param workflowChecksum The checksum of the implementing workflow.
	 * @param inputSequenceFilesLabel The label to use as input for sequence files.
	 * @param inputReferenceFileLabel The label to use as input for a reference file.
	 */
	public RemoteWorkflowGalaxyPhylogenomics(String workflowId,
			String workflowChecksum, String inputSequenceFilesLabel,
			String inputReferenceFileLabel) {
		super(workflowId, workflowChecksum);
		this.inputSequenceFilesLabel = inputSequenceFilesLabel;
		this.inputReferenceFileLabel = inputReferenceFileLabel;
	}

	public String getInputSequenceFilesLabel() {
		return inputSequenceFilesLabel;
	}

	public String getInputReferenceFileLabel() {
		return inputReferenceFileLabel;
	}
}
