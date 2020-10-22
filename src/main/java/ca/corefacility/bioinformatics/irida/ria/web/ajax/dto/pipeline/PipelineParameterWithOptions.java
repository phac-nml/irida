package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;

import com.google.common.base.Strings;

/**
 * Represents a IRIDA Workflow Pipeline Parameter that has specific options to be render
 * within the UI.
 */
public class PipelineParameterWithOptions extends PipelineParameter {
	private List<SelectOption> options;

	public PipelineParameterWithOptions(String name, String label, String defaultValue, List<SelectOption> options) {
		super(name, label, defaultValue);
		this.options = options;
	}

	public List<SelectOption> getOptions() {
		return options;
	}

	/**
	 * Getter for the value, if there is no value, the value of the first option is returned.
	 *
	 * @return the default value for the parameter.
	 */
	@Override
	public String getValue() {
		if (Strings.isNullOrEmpty(super.getValue())) {
			return options.get(0)
					.getValue();
		}
		return super.getValue();
	}
}