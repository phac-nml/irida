package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used as a response for encapsulating tool execution parameters
 */

public class AnalysisToolExecutionParameters {

	@JsonProperty("parameterName")
	private String parameterName;

	@JsonProperty("parameterValue")
	private String parameterValue;

	public AnalysisToolExecutionParameters() {
	}

	public AnalysisToolExecutionParameters(String parameterName, String parameterValue) {
		this.parameterName=parameterName;
		this.parameterValue=parameterValue;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
}
