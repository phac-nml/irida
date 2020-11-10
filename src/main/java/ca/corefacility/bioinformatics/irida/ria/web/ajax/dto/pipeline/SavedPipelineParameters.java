package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline;

import java.util.List;

/**
 * Used by the UI for displaying a named pipeline parameter set.
 */
public class SavedPipelineParameters {
    private  Long id;
    private  String label;
    private  List<PipelineParameter> parameters;

    public SavedPipelineParameters() {
    }

    public SavedPipelineParameters(Long id, String label, List<PipelineParameter> parameters) {
        this.id = id;
        this.label = label;
        this.parameters = parameters;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public List<PipelineParameter> getParameters() {
        return parameters;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setParameters(List<PipelineParameter> parameters) {
        this.parameters = parameters;
    }
}
