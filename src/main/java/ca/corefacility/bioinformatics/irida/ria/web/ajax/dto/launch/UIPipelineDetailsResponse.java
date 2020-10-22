package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.launch;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline.PipelineParameter;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline.PipelineParameterWithOptions;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline.SavedPipelineParameters;

/**
 * Model class to send details about a workflow pipeline to the UI
 * Used on the launch pipeline page.
 */
public class UIPipelineDetailsResponse extends AjaxResponse {
    private String name;
    private String description;
    private String type;
    private List<SavedPipelineParameters> savedPipelineParameters;
    private List<PipelineParameterWithOptions> parameterWithOptions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PipelineParameterWithOptions> getParameterWithOptions() {
        return parameterWithOptions;
    }

    public void setParameterWithOptions(List<PipelineParameterWithOptions> parameterWithOptions) {
        this.parameterWithOptions = parameterWithOptions;
    }

    public List<SavedPipelineParameters> getSavedPipelineParameters() {
        return savedPipelineParameters;
    }

    public void setSavedPipelineParameters(List<SavedPipelineParameters> savedPipelineParameters) {
        this.savedPipelineParameters = savedPipelineParameters;
    }
}