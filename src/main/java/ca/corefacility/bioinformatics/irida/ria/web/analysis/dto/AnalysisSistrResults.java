package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.Map;

public class AnalysisSistrResults {
	private String sampleName;
	private Boolean parse_results_error;
	private Map<String, Object> result;

	public AnalysisSistrResults() {
	}

	public AnalysisSistrResults(String sampleName, Boolean parse_results_error, Map<String, Object> result) {
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

	public Map<String, Object> getResult() {
		return result;
	}

	public void setResult(Map<String, Object> result) {
		this.result = result;
	}
}
