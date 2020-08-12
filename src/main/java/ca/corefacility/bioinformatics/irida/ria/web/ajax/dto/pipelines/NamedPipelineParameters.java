package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

import java.util.List;

public class NamedPipelineParameters {
	private final Long id;
	private final String label;
	private final List<Parameter> parameters;

	public NamedPipelineParameters(Long id, String label, List<Parameter> parameters) {
		this.id = id;
		this.label = label;
		this.parameters = parameters;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}
}
