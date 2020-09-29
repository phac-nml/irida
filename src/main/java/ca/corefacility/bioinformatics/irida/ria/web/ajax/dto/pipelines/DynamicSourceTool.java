package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;

import java.util.List;

public class DynamicSourceTool {
	private final String id;
	private final String label;
	private  List<SelectOption> options;

	public DynamicSourceTool(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setOptions(List<SelectOption> options) {
		this.options = options;
	}

	public List<SelectOption> getOptions() {
		return options;
	}
}
