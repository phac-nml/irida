package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;

public class DynamicSource {
	private final String id;
	private final String label;
	private final List<SelectOption> options;

	public DynamicSource(String id, String label, List<SelectOption> options) {
		this.id = id;
		this.label = label;
		this.options = options;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public List<SelectOption> getOptions() {
		return options;
	}
}
