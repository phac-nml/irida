package ca.corefacility.bioinformatics.irida.model.workflow.execution;

/**
 * Defines acceptable file types for input to a workflow.
 *
 */
public enum InputFileType {
	
	/**
	 * The fastqsanger file type (quality scores are Phred+33).
	 */
	FASTQ_SANGER("fastqsanger"),
	
	/**
	 * A FASTA formatted file.
	 */
	FASTA("fasta");

	private String fileType;

	private InputFileType(String type) {
		this.fileType = type;
	}

	@Override
	public String toString() {
		return fileType;
	}
}
