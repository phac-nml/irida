package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;

public class DynamicSourceTool {
	private final String id;
	private final String label;
	private final List<SelectOption> parameters;

	public DynamicSourceTool(String id, String label, List<SelectOption> parameters) {
		this.id = id;
		this.label = label;
		this.parameters = parameters;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public List<SelectOption> getParameters() {
		return parameters;
	}
}
