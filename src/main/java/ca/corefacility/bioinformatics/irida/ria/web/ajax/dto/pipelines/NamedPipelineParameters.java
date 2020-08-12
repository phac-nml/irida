package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

import java.util.List;

public class NamedPipelineParameters {
	private final Long id;
	private final String label;
	private final List<PipelineParameters> parameters;

	public NamedPipelineParameters(Long id, String label, List<PipelineParameters> parameters) {
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

	public List<PipelineParameters> getParameters() {
		return parameters;
	}
}
