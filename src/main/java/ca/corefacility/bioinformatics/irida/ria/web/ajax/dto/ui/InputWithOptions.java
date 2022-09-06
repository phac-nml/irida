package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui;

import java.util.List;

import com.google.common.base.Strings;

/**
 * Represents a IRIDA Workflow Pipeline Parameter that has specific options to be rendered
 * within the UI.
 */
public class InputWithOptions extends Input {
	private final List<SelectOption> options;

	private Boolean required;

	public InputWithOptions(String name, String label, String defaultValue, List<SelectOption> options, Boolean required) {
		super(name, label, defaultValue);
		this.options = options;
		this.required = required;
	}

	public List<SelectOption> getOptions() {
		return options;
	}

	public Boolean getRequired() { return required; }

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