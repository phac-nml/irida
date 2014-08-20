package ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;

/**
 * A RemoteWorkflow for a phylogenomics analysis in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class RemoteWorkflowPhylogenomics extends RemoteWorkflowGalaxy {

	private String inputSequenceFilesLabel;
	private String inputReferenceFileLabel;
	
	private String outputPhylogeneticTreeName;
	private String outputSsnpMatrixName;
	private String outputSnpTableName;
	
	/**

	 */
	/**
	 * Creates a new RemoteWorkflowPhylogenomics.
	 * @param workflowId The ID of the implementing workflow.
	 * @param workflowChecksum The checksum of the implementing workflow.
	 * @param inputSequenceFilesLabel The label to use as input for sequence files.
	 * @param inputReferenceFileLabel The label to use as input for a reference file.
	 * @param outputPhylogeneticTreeName  The name for the output phylogenetic tree.
	 * @param outputSnpMatrixName  The name for the output SNP matrix.
	 * @param outputSnpTableName  The name for the output SNP table.
	 */
	public RemoteWorkflowPhylogenomics(String workflowId,
			String workflowChecksum, String inputSequenceFilesLabel,
			String inputReferenceFileLabel, String outputPhylogeneticTreeName,
			String outputSnpMatrixName, String outputSnpTableName) {
		super(workflowId, workflowChecksum);
		this.inputSequenceFilesLabel = inputSequenceFilesLabel;
		this.inputReferenceFileLabel = inputReferenceFileLabel;
		this.outputPhylogeneticTreeName = outputPhylogeneticTreeName;
		this.outputSsnpMatrixName = outputSnpMatrixName;
		this.outputSnpTableName = outputSnpTableName;
	}

	public String getInputSequenceFilesLabel() {
		return inputSequenceFilesLabel;
	}

	public String getInputReferenceFileLabel() {
		return inputReferenceFileLabel;
	}

	public String getOutputPhylogeneticTreeName() {
		return outputPhylogeneticTreeName;
	}

	public String getOutputSnpMatrixName() {
		return outputSsnpMatrixName;
	}

	public String getOutputSnpTableName() {
		return outputSnpTableName;
	}
}
