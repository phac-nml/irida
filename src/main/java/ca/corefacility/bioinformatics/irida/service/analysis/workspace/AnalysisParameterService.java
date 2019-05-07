package ca.corefacility.bioinformatics.irida.service.analysis.workspace;

import java.util.Map;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowParameterException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.WorkflowInputsGeneric;

/**
 * A service for setting up parameters for an analysis.
 *
 * @param <WorkflowInputsType> Type of workflow inputs for this service
 */
public interface AnalysisParameterService<WorkflowInputsType extends WorkflowInputsGeneric> {

	/**
	 * Prepares any parameters for this {@link IridaWorkflow}.
	 * 
	 * @param parameters
	 *            The parameters to use. 
	 * @param iridaWorkflow
	 *            The {@link IridaWorkflow} to prepare.
	 * @return A {@link WorkflowInputsGeneric} object defining the inputs to a
	 *         workflow.
	 * @throws IridaWorkflowParameterException
	 *             If there was an issue with the parameters.
	 */
	public WorkflowInputsType prepareAnalysisParameters(Map<String, String> parameters, IridaWorkflow iridaWorkflow)
			throws IridaWorkflowParameterException;
}
