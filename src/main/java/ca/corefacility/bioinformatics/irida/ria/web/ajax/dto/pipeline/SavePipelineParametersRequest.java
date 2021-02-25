package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline;

import java.util.Map;

/**
 * Used to save new values for Saved Pipeline Parameters on the Pipeline Launch Page
 */
public class SavePipelineParametersRequest {
	private String label;
	private Map<String, String> parameters;

	public SavePipelineParametersRequest() {
	}

	public SavePipelineParametersRequest(String label, Map<String, String> parameters) {
		this.label = label;
		this.parameters = parameters;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
}
