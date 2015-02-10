package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowParameterException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaToolParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.AnalysisParameterService;

import com.github.jmchilton.blend4j.galaxy.beans.ToolParameter;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
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

		List<IridaWorkflowParameter> iridaParameters = iridaWorkflow.getWorkflowDescription().getParameters();
		for (IridaWorkflowParameter iridaParameter : iridaParameters) {
			String parameterName = iridaParameter.getName();
			String value = parameters.get(parameterName);
			parameterNamesUsed.add(parameterName);

			if (value == null) {
				value = iridaParameter.getDefaultValue();
				logger.debug("Parameter with name=" + parameterName + ", for workflow=" + iridaWorkflow
						+ ", has no value set, using defaultValue=" + value);
			}

			for (IridaToolParameter iridaToolParameter : iridaParameter.getToolParameters()) {
				String toolId = iridaToolParameter.getToolId();
				String galaxyParameterName = iridaToolParameter.getParameterName();

				logger.debug("Setting parameter iridaName=" + parameterName + ", galaxyToolId=" + toolId + ", value="
						+ value);
				inputs.setToolParameter(toolId, new ToolParameter(galaxyParameterName, value));
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
}
