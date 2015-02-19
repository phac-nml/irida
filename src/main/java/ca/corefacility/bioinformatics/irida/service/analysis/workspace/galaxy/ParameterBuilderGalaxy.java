package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaToolParameter;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * A class used to build up the parameter data structures used for Galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class ParameterBuilderGalaxy {

	private List<String> parameterNames;

	/**
	 * Builds a new {@link ParameterBuilderGalaxy} with the given
	 * information.
	 * 
	 * @param parameterName
	 *            The encoding of the name of the Galaxy parameters.
	 */
	public ParameterBuilderGalaxy(String parameterName) {
		checkNotNull(parameterName, "parameterName is null");
		checkArgument(!"".equals(parameterName), "parameterName is empty");

		parameterNames = Splitter.on(IridaToolParameter.PARAMETER_NAME_SEPARATOR).splitToList(parameterName);

		checkArgument(parameterNames.size() >= 1);
	}

	/**
	 * Returns the very first name of the parameter list.
	 * 
	 * @return The very first name of the parameter list.
	 */
	public String getStartName() {
		return parameterNames.get(0);
	}

	/**
	 * Builds up a Galaxy data structure for encoding the parameters mapping
	 * to the value.
	 * 
	 * @param value
	 *            The value to map all the given parameter names to.
	 * @return A {@link Map} encoding the nested data structure.
	 */
	public Object buildForValue(String value) {
		List<String> parameterNamesSubList = Lists.newArrayList(parameterNames);
		parameterNamesSubList.remove(0); // remove the first 'startName' element
		
		return buildForMapRecursive(parameterNamesSubList, value);
	}

	private Object buildForMapRecursive(List<String> parameterNames, String value) {
		if (parameterNames.size() == 0) {
			return value;
		} else if (parameterNames.size() == 1) {
			String name = parameterNames.remove(0);
			return ImmutableMap.of(name, value);
		} else {
			String name = parameterNames.remove(0);
			return ImmutableMap.of(name, buildForMapRecursive(parameterNames, value));
		}
	}
}