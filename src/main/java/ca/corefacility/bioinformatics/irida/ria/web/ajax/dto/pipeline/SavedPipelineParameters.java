package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.Input;

/**
 * Used by the UI for displaying a named pipeline parameter set.
 */
public class SavedPipelineParameters {
	private Long id;
	private String label;
	private List<Input> parameters;

	public SavedPipelineParameters() {
	}

	public SavedPipelineParameters(Long id, String label, List<Input> parameters) {
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

	public List<Input> getParameters() {
		return parameters;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setParameters(List<Input> parameters) {
		this.parameters = parameters;
	}
}
