package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

/**
 * Used as a response for encapsulating a SISTR result object as well
 * as the sample name and if there was a parsing error
 */


public class AnalysisSistrResults {
	private String sampleName;
	private Boolean parse_results_error;
	private SistrResult result;

	public AnalysisSistrResults() {
	}

	public AnalysisSistrResults(String sampleName, Boolean parse_results_error, SistrResult result) {
		this.sampleName=sampleName;
		this.parse_results_error=parse_results_error;
		this.result=result;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public Boolean getParse_results_error() {
		return parse_results_error;
	}

	public void setParse_results_error(Boolean parse_results_error) {
		this.parse_results_error = parse_results_error;
	}

	public SistrResult getResult() {
		return result;
	}

	public void setResult(SistrResult result) {
		this.result = result;
	}
}
