package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;

import com.google.common.base.Strings;

public class PipelineParameterWithOptions {
	private final String name;
	private final String defaultValue;
	private final List<SelectOption> options;

	public PipelineParameterWithOptions(String name, String defaultValue, List<SelectOption> options) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.options = options;
	}

	public String getName() {
		return name;
	}

	public String getDefaultValue() {
		if(Strings.isNullOrEmpty(defaultValue)) {
			return options.get(0).getValue();
		}
		return defaultValue;
	}

	public List<SelectOption> getOptions() {
		return options;
	}
}
