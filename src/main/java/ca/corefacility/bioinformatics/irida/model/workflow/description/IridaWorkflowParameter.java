package ca.corefacility.bioinformatics.irida.model.workflow.description;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowParameterException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.*;

import javax.xml.bind.annotation.*;

/**
 * Defines input parameters for a workflow which can be adjusted.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class IridaWorkflowParameter {

	/**
	 * Can be passed as the value of a parameter instructing IRIDA to ignore the default value.
	 */
	public static final String IGNORE_DEFAULT_VALUE = "";

	@XmlAttribute(name = "name")
	private String name;

	@XmlAttribute(name = "defaultValue")
	private String defaultValue;

	@XmlAttribute(name = "required")
	private boolean required = false;

	@XmlElementWrapper(name = "dynamicSource")
	@XmlElements({ @XmlElement(name = "galaxyToolDataTable", type = IridaWorkflowDynamicSourceGalaxy.class), })
	// In order to satisfy the @XMLElementWrapper annotation we need to store dynamicSources in a
	// collection type, but there should only be at most one <dynamicSource> element per <parameter> element
	// in the workflow definition .xml file
	private List<IridaWorkflowDynamicSourceGalaxy> dynamicSource;

	@XmlElementWrapper(name = "choices")
	@XmlElements({ @XmlElement(name = "choice", type = IridaWorkflowParameterChoice.class) })
	private List<IridaWorkflowParameterChoice> choices;

	@XmlElement(name = "toolParameter")
	private List<IridaToolParameter> toolParameters;

	public IridaWorkflowParameter() {
	}

	/**
	 * Creates a new {@link IridaWorkflowParameter} which maps to the given tool parameters.
	 * 
	 * @param name           The name of the parameter.
	 * @param defaultValue   The default value of this parameter.
	 * @param toolParameters The tool parameters corresponding to this named parameter.
	 */
	public IridaWorkflowParameter(String name, String defaultValue, List<IridaToolParameter> toolParameters) {
		checkNotNull(name, "name is null");
		checkNotNull(defaultValue, "defaultValue is null");
		checkNotNull(toolParameters, "toolParameters is null");
		checkArgument(toolParameters.size() > 0, "toolParameters has no elements");

		this.name = name;
		this.defaultValue = defaultValue;
		this.toolParameters = toolParameters;
	}

	/**
	 * Creates a new {@link IridaWorkflowParameter} which maps to the given tool parameters.
	 *
	 * @param name           The name of the parameter.
	 * @param required       The default value of this parameter.
	 * @param dynamicSource  Any dynamic sources for parameters (eg. Galaxy Tool Data Table)
	 * @param toolParameters The tool parameters corresponding to this named parameter.
	 */
	public IridaWorkflowParameter(String name, boolean required, IridaWorkflowDynamicSourceGalaxy dynamicSource,
			List<IridaToolParameter> toolParameters) {
		checkNotNull(name, "name is null");
		checkNotNull(toolParameters, "toolParameters is null");
		checkArgument(toolParameters.size() > 0, "toolParameters has no elements");

		this.name = name;
		this.required = required;
		this.toolParameters = toolParameters;
		this.dynamicSource = Collections.singletonList(dynamicSource);
		this.defaultValue = null;

	}

	/**
	 * Creates a new {@link IridaWorkflowParameter} which maps to the given tool parameters.
	 *
	 * @param name           Parameter name.
	 * @param required       Is the parameter required?
	 * @param choices        Restricted set of choices for values of parameter.
	 * @param toolParameters Tool parameters for this named parameter.
	 */
	public IridaWorkflowParameter(String name, boolean required, List<IridaWorkflowParameterChoice> choices,
			List<IridaToolParameter> toolParameters) {
		checkNotNull(name, "name is null");
		checkNotNull(toolParameters, "toolParameters is null");
		checkArgument(toolParameters.size() > 0, "toolParameters has no elements");

		this.name = name;
		this.required = required;
		this.choices = choices;
		this.toolParameters = toolParameters;
	}

	/**
	 * Gets the name of this parameter.
	 * 
	 * @return The name of this parameter.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets a list of tools whose values are affected by this parameter.
	 * 
	 * @return A {@link List} of tools whose values are affected by this parameter.
	 */
	public List<IridaToolParameter> getToolParameters() {
		return toolParameters;
	}

	/**
	 * Gets the default value for this parameter.
	 * 
	 * @return The default value for this parameter.
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Whether or not this parameter is required to be set manually before launching a pipeline.
	 *
	 * @return Boolean representing whether or not this parameter is required to be set manually before launching a
	 *         pipeline.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Gets the dynamic source for the parameter.
	 *
	 * @return The dynamic source for this parameter.
	 * @throws IridaWorkflowParameterException If multiple dynamic sources are associated with one parameter.
	 */
	public IridaWorkflowDynamicSourceGalaxy getDynamicSource() throws IridaWorkflowParameterException {
		if (dynamicSource != null) {
			if (dynamicSource.size() > 1) {
				throw new IridaWorkflowParameterException("Limit of one dynamic source per parameter.");
			} else {
				return dynamicSource.get(0);
			}
		} else {
			return null;
		}
	}

	/**
	 * Get the list of choices for this parameter
	 *
	 * @return list of choices for this parameter
	 */
	public List<IridaWorkflowParameterChoice> getChoices() {
		return choices;
	}

	/**
	 * Whether or not this parameter pulls its value from a Dynamic Source (eg. a Galaxy Tool Data Table)
	 *
	 * @return Boolean representing whether or not this parameter has a dynamic source
	 */
	public Boolean hasDynamicSource() {
		return dynamicSource != null && dynamicSource.size() > 0;
	}

	/**
	 * Does this parameter have a set of restricted choices?
	 *
	 * @return If this parameter has a list of choices
	 */
	public Boolean hasChoices() {
		return choices != null && choices.size() > 0;
	}

	/**
	 * Is the list of choices empty?
	 *
	 * @return if the list of choices is empty
	 */
	public Boolean isChoicesEmpty() {
		return choices != null && choices.isEmpty();
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, defaultValue, toolParameters, required, dynamicSource, choices);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflowParameter) {
			IridaWorkflowParameter other = (IridaWorkflowParameter) obj;

			return Objects.equals(name, other.name) && Objects.equals(defaultValue, other.defaultValue)
					&& Objects.equals(toolParameters, other.toolParameters) && Objects.equals(required, other.required)
					&& Objects.equals(dynamicSource, other.dynamicSource) && Objects.equals(choices, other.choices);
		}

		return false;
	}

	@Override
	public String toString() {
		return "IridaWorkflowParameter [name=" + name + ", required=" + required + ", defaultValue="
				+ ((defaultValue == null) ? "null" : defaultValue) + ", dynamicSource="
				+ ((dynamicSource == null) ? "null" : dynamicSource.get(0).toString()) + ", choices="
				+ ((choices == null) ? "null" : choices.toString()) + "]";
	}
}
