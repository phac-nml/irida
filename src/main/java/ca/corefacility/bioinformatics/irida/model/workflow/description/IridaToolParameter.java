package ca.corefacility.bioinformatics.irida.model.workflow.description;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * A parameter for a particular tool in a workflow.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class IridaToolParameter {
	
	@XmlAttribute(name = "toolId")
	private String toolId;
	
	@XmlAttribute(name = "parameterName")
	private String parameterName;
	
	@XmlAttribute(name = "defaultValue")
	private String defaultValue;

	public IridaToolParameter() {
	}

	/**
	 * Builds a new {@link IridaToolParameter} object with the given
	 * information.
	 * 
	 * @param toolId
	 *            The id of the tool to adjust the parameter.
	 * @param parameterName
	 *            The name of the parameter.
	 * @param defaultValue
	 *            The default value of this parameter.
	 */
	public IridaToolParameter(String toolId, String parameterName, String defaultValue) {
		this.toolId = toolId;
		this.parameterName = parameterName;
		this.defaultValue = defaultValue;
	}

	/**
	 * The id of the tool to adjust.
	 * 
	 * @return The id of the tool with the parameter to set.
	 */
	public String getToolId() {
		return toolId;
	}

	/**
	 * The name of the parameter to adjust.
	 * 
	 * @return The name of the parameter to adjust.
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * A default value for the parameter.
	 * 
	 * @return A default value for the parameter.
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(toolId, parameterName, defaultValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaToolParameter) {
			IridaToolParameter other = (IridaToolParameter) obj;

			return Objects.equals(toolId, other.toolId) && Objects.equals(parameterName, other.parameterName)
					&& Objects.equals(defaultValue, other.defaultValue);
		}

		return false;
	}

	@Override
	public String toString() {
		return "IridaToolParameter [toolId=" + toolId + ", parameterName=" + parameterName + ", defaultValue="
				+ defaultValue + "]";
	}
}
