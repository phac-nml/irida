package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

import java.util.List;

public class PipelineLaunchDetails {
	private String name;
	private String description;
	private boolean shareWithProjects;
	private List<Parameter> parameters;
	private List<PipelineParameterWithOptions> parametersWithOptions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isShareWithProjects() {
		return shareWithProjects;
	}

	public void setShareWithProjects(boolean shareWithProjects) {
		this.shareWithProjects = shareWithProjects;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public List<PipelineParameterWithOptions> getParametersWithOptions() {
		return parametersWithOptions;
	}

	public void setParametersWithOptions(List<PipelineParameterWithOptions> parametersWithOptions) {
		this.parametersWithOptions = parametersWithOptions;
	}
}
