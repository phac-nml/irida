package ca.corefacility.bioinformatics.irida.ria.web.pipelines.dto;

import java.util.Map;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO to allow easy access to a set of named parameters supplied by the client.
 * 
 *
 */
public class WorkflowParametersToSave {
	@JsonProperty("pipelineId")
	private final String pipelineId;
	@JsonProperty("parameterSetName")
	private final String parameterSetName;
	@JsonProperty("parameterValues")
	private final Map<String, String> parameterValues;
	
	private WorkflowParametersToSave() {
		this.pipelineId = null;
		this.parameterSetName = null;
		this.parameterValues = null;
	}
	
	/**
	 * Get an instance of {@link IridaWorkflowNamedParameters} that corresponds
	 * to this request.
	 * 
	 * @return an instance of {@link IridaWorkflowNamedParameters} for this DTO.
	 */
	public IridaWorkflowNamedParameters namedParameters() {
		return new IridaWorkflowNamedParameters(parameterSetName, UUID.fromString(pipelineId), parameterValues);
	}
	
	@Override
	public String toString() {
		return String.format("ParametersToSave [pipelineId=%s, parameterSetName=%s]", pipelineId.toString(),
				parameterSetName);
	}
}
