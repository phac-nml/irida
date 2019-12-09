package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

/**
 * Used as a response for encapsulating a newick string
 * and an optional server message.
 */

public class AnalysisTree {
	private String newick;
	private String message;

	public AnalysisTree() {
	}

	public AnalysisTree(String newick, String message) {
		this.newick=newick;
		this.message=message;
	}

	public String getNewick() {
		return newick;
	}

	public void setNewick(String newick) {
		this.newick = newick;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
