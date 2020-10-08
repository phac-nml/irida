package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

/**
 * Used as a response for encapsulating the file provenance
 */

public class AnalysisProvenanceResponse {
	private String filename;
	private AnalysisToolExecution createdByTool;

	public AnalysisProvenanceResponse(){
	}

	public AnalysisProvenanceResponse(String filename, AnalysisToolExecution createdByTool) {
		this.filename=filename;
		this.createdByTool=createdByTool;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public AnalysisToolExecution getCreatedByTool() {
		return createdByTool;
	}

	public void setTools(AnalysisToolExecution createdByTool) {
		this.createdByTool = createdByTool;
	}
}
