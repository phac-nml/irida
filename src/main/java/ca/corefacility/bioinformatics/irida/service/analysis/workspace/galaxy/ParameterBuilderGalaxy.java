package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaToolParameter;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * A class used to build up the parameter data structures used for Galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class ParameterBuilderGalaxy {

	/**
	 * An id for a particular parameter.
	 * 
	 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
	 *
	 */
	public static class ParameterId {
		private String toolId;
		private String startName;

		/**
		 * Builds a new parameter id with the given information.
		 * 
		 * @param toolId
		 *            The id of the tool.
		 * @param startName
		 *            The start name for the parameter.
		 */
		public ParameterId(String toolId, String startName) {
			checkNotNull(toolId, "toolId is null");
			checkNotNull(startName, "startName is null");

			this.toolId = toolId;
			this.startName = startName;
		}

		public String getToolId() {
			return toolId;
		}

		public String getStartName() {
			return startName;
		}

		@Override
		public int hashCode() {
			return Objects.hash(toolId, startName);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			else if (obj instanceof ParameterId) {
				ParameterId other = (ParameterId) obj;

				return Objects.equals(toolId, other.toolId) && Objects.equals(startName, other.startName);
			}

			return false;
		}

		@Override
		public String toString() {
			return "[toolId=" + toolId + ", startName=" + startName + "]";
		}
	}

	private Map<ParameterId, Object> parameterMapping;

	/**
	 * Builds a new {@link ParameterBuilderGalaxy}.
	 */
	public ParameterBuilderGalaxy() {
		parameterMapping = new HashMap<>();
	}

	/**
	 * Adds a new parameter name/value mapping for Galaxy.
	 * 
	 * @param toolId
	 *            The id of the tool for the parameter.
	 * 
	 * @param parameterName
	 *            The name of the parameter.
	 * @param value
	 *            The value of the parameter.
	 */
	@SuppressWarnings("unchecked")
	public void addParameter(String toolId, String parameterName, String value) {
		checkArgument(!Strings.isNullOrEmpty(toolId), "toolId is null or empty");
		checkArgument(!Strings.isNullOrEmpty(parameterName), "parameterName is null or empty");
		checkNotNull(value, "value is null");

		List<String> parameterSubNames = Lists.newArrayList(Splitter.on(IridaToolParameter.PARAMETER_NAME_SEPARATOR)
				.split(parameterName));
		checkArgument(parameterSubNames.size() >= 1, "invalid parameterName=" + parameterName);

		String firstParameterName = parameterSubNames.remove(0);
		ParameterId parameterId = new ParameterId(toolId, firstParameterName);

		if (parameterSubNames.size() == 0) {
			checkArgument(!parameterMapping.containsKey(parameterId), "already contain mapping for " + parameterId
					+ " to value " + value);
			parameterMapping.put(parameterId, value);
		} else {
			Map<String, Object> parameterMap;
			if (parameterMapping.containsKey(parameterId)) {
				Object parameterMapObj = parameterMapping.get(parameterId);

				// I am the one constructing these objects so I can assume it
				// will be a map.
				parameterMap = (Map<String, Object>) parameterMapObj;
			} else {
				parameterMap = new HashMap<>();
				parameterMapping.put(parameterId, parameterMap);
			}

			insertToMapRecursive(parameterSubNames, parameterMap, value);
		}
	}

	/**
	 * Gets a {@link Set} of the starting part of the names of any parameters.
	 * 
	 * @return A {@link Set} of the starting part of the names of any
	 *         parameters.
	 */
	public Set<ParameterId> getParameterIds() {
		return parameterMapping.keySet();
	}

	/**
	 * Gets the mapped object for the id of the parameter.
	 * 
	 * @param parameterId
	 *            The id of the parameter.
	 * @return A mapping for parameter id.
	 */
	public Object getMappingForParameterId(ParameterId parameterId) {
		return parameterMapping.get(parameterId);
	}

	@SuppressWarnings("unchecked")
	private void insertToMapRecursive(List<String> parameterNames, Map<String, Object> parameterMap, String value) {
		if (parameterNames.size() == 1) {
			String name = parameterNames.remove(0);
			checkArgument(!parameterMap.containsKey(name), "parameterMap already contains key with name \"" + name
					+ "\" mapping to value \"" + value + "\"");

			parameterMap.put(name, value);
		} else {
			String name = parameterNames.remove(0);

			Map<String, Object> parameterSubMap;
			if (parameterMap.containsKey(name)) {
				Object parameterSubMapObj = parameterMap.get(name);
				parameterSubMap = (Map<String, Object>) parameterSubMapObj;
			} else {
				parameterSubMap = new HashMap<>();
				parameterMap.put(name, parameterSubMap);
			}

			insertToMapRecursive(parameterNames, parameterSubMap, value);
		}
	}
}