package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Response for creating a new set of named parameters on the pipeline launch page.
 */
public class CreateNamedParameterSetAjaxResponse extends AjaxResponse {
	private final SavedPipelineParameters pipelineParameters;

	public CreateNamedParameterSetAjaxResponse(SavedPipelineParameters pipelineParameters) {
		this.pipelineParameters = pipelineParameters;
	}

	public SavedPipelineParameters getPipelineParameters() {
		return pipelineParameters;
	}
}
