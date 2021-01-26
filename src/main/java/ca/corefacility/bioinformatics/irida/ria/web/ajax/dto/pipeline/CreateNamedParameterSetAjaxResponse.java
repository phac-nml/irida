package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

public class CreateNamedParameterSetAjaxResponse extends AjaxResponse {
	private final SavedPipelineParameters pipelineParameters;

	public CreateNamedParameterSetAjaxResponse(SavedPipelineParameters pipelineParameters) {
		this.pipelineParameters = pipelineParameters;
	}

	public SavedPipelineParameters getPipelineParameters() {
		return pipelineParameters;
	}
}
