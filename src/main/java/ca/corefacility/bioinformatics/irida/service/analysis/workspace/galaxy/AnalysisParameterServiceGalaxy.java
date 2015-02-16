package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNoParameterException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowParameterException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaToolParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.AnalysisParameterService;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A Galaxy implementation for preparing parameters for an analysis.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Service
public class AnalysisParameterServiceGalaxy implements AnalysisParameterService<WorkflowInputsGalaxy> {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisParameterServiceGalaxy.class);
	
	private boolean ignoreDefaultValue(Map<String, String> parameters, String parameterName) {
		return parameters.containsKey(parameterName)
				&& Objects.equals(parameters.get(parameterName), IridaWorkflowParameter.IGNORE_DEFAULT_VALUE);
	}

	private boolean useDefaultValue(Map<String, String> parameters, String parameterName) {
		return parameters.get(parameterName) == null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkflowInputsGalaxy prepareAnalysisParameters(Map<String, String> parameters, IridaWorkflow iridaWorkflow)
			throws IridaWorkflowParameterException {
		checkNotNull(parameters, "parameters is null");
		checkNotNull(iridaWorkflow, "iridaWorkflow is null");

		WorkflowInputs inputs = new WorkflowInputs();
		Set<String> parameterNamesUsed = Sets.newHashSet();

		if (!iridaWorkflow.getWorkflowDescription().acceptsParameters()) {
			if (parameters.isEmpty()) {
				logger.debug("workflow " + iridaWorkflow + " does not accept parameters and no parameters passed.");
			} else {
				throw new IridaWorkflowNoParameterException("The workflow " + iridaWorkflow
						+ " does not accept parameters but parameters " + parameters + " were passed.");
			}
		} else {
			List<IridaWorkflowParameter> iridaParameters = iridaWorkflow.getWorkflowDescription().getParameters();
			for (IridaWorkflowParameter iridaParameter : iridaParameters) {
				String parameterName = iridaParameter.getName();
				String value = parameters.get(parameterName);
				parameterNamesUsed.add(parameterName);

				if (ignoreDefaultValue(parameters, parameterName)) {
					logger.debug("Parameter with name=" + parameterName + " will ignore the default value="
							+ iridaParameter.getDefaultValue());
				} else {
					if (useDefaultValue(parameters, parameterName)) {
						value = iridaParameter.getDefaultValue();
						logger.debug("Parameter with name=" + parameterName + ", for workflow=" + iridaWorkflow
								+ ", has no value set, using defaultValue=" + value);
					}

					for (IridaToolParameter iridaToolParameter : iridaParameter.getToolParameters()) {
						String toolId = iridaToolParameter.getToolId();
						String galaxyParameterName = iridaToolParameter.getParameterName();
						ParameterBuilderGalaxy parameterBuilder = new ParameterBuilderGalaxy(galaxyParameterName);

						logger.debug("Setting parameter iridaName=" + parameterName + ", galaxyToolId=" + toolId
								+ ", galaxyParameterName=" + galaxyParameterName + ", value=" + value);
						inputs.setToolParameter(toolId, parameterBuilder.getStartName(), parameterBuilder.buildForValue(value));
					}
				}
			}
		}

		Set<String> parameterNamesUnused = Sets.difference(parameters.keySet(), parameterNamesUsed);
		if (!parameterNamesUnused.isEmpty()) {
			throw new IridaWorkflowParameterException("The set of parameters " + parameterNamesUnused
					+ " are not defined in " + iridaWorkflow);
		} else {
			return new WorkflowInputsGalaxy(inputs);
		}
	}

	/**
	 * A class used to build up the parameter data structures used for Galaxy.
	 * 
	 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
	 *
	 */
	public static class ParameterBuilderGalaxy {

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
}
