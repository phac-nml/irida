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
	
	private String phylogeneticTreeLabel;
	private String snpMatrixLabel;
	private String snpTableLabel;
	
	/**

	 */
	/**
	 * Creates a new RemoteWorkflowPhylogenomics.
	 * @param workflowId The ID of the implementing workflow.
	 * @param workflowChecksum The checksum of the implementing workflow.
	 * @param inputSequenceFilesLabel The label to use as input for sequence files.
	 * @param inputReferenceFileLabel The label to use as input for a reference file.
	 * @param phylogeneticTreeLabel  The label for the output phylogenetic tree.
	 * @param snpMatrixLabel  The label for the output SNP matrix.
	 * @param snpTableLabel  The label for the output SNP table.
	 */
	public RemoteWorkflowPhylogenomics(String workflowId,
			String workflowChecksum, String inputSequenceFilesLabel,
			String inputReferenceFileLabel, String phylogeneticTreeLabel,
			String snpMatrixLabel, String snpTableLabel) {
		super(workflowId, workflowChecksum);
		this.inputSequenceFilesLabel = inputSequenceFilesLabel;
		this.inputReferenceFileLabel = inputReferenceFileLabel;
		this.phylogeneticTreeLabel = phylogeneticTreeLabel;
		this.snpMatrixLabel = snpMatrixLabel;
		this.snpTableLabel = snpTableLabel;
	}

	public String getInputSequenceFilesLabel() {
		return inputSequenceFilesLabel;
	}

	public String getInputReferenceFileLabel() {
		return inputReferenceFileLabel;
	}

	public String getPhylogeneticTreeLabel() {
		return phylogeneticTreeLabel;
	}

	public String getSnpMatrixLabel() {
		return snpMatrixLabel;
	}

	public String getSnpTableLabel() {
		return snpTableLabel;
	}
}
