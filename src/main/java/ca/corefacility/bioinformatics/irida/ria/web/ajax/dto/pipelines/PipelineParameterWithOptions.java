package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;

import com.google.common.base.Strings;

public class PipelineParameterWithOptions {
	private  String label;
	private  String name;
	private  String value;
	private  List<SelectOption> options;

	public PipelineParameterWithOptions() {
	}

	public PipelineParameterWithOptions(String name, String label, String defaultValue, List<SelectOption> options) {
		this.label =label;
		this.name = name;
		this.value = defaultValue;
		this.options = options;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		if(Strings.isNullOrEmpty(value)) {
			return options.get(0).getValue();
		}
		return value;
	}

	public List<SelectOption> getOptions() {
		return options;
	}
}
