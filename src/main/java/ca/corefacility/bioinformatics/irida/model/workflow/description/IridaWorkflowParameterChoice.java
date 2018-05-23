package ca.corefacility.bioinformatics.irida.model.workflow.description;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;

public class IridaWorkflowParameterChoice {
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String value;

	public IridaWorkflowParameterChoice() {
	}

	/**
	 * Parameter choice with informative name and acceptable Galaxy workflow value for the parameter
	 *
	 * @param name Human readable informative choice name
	 * @param value Galaxy workflow value for this choice
	 */
	public IridaWorkflowParameterChoice(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Get parameter choice informative name
	 *
	 * @return parameter choice informative name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get parameter choice acceptable Galaxy workflow value
	 *
	 * @return parameter choice acceptable Galaxy workflow value
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "IridaWorkflowParameterChoice{" + "name='" + name + '\'' + ", value='" + value + "'}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		IridaWorkflowParameterChoice that = (IridaWorkflowParameterChoice) o;
		return Objects.equals(getName(), that.getName()) && Objects.equals(getValue(), that.getValue());
	}

	@Override
	public int hashCode() {

		return Objects.hash(getName(), getValue());
	}
}
