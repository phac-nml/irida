package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

import java.util.List;

public class PipelineParametersSaveRequest {
	private String name;
	private List<Parameter> parameters;

	public PipelineParametersSaveRequest() {
	}

	public PipelineParametersSaveRequest(String name, List<Parameter> parameters) {
		this.name = name;
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
}
