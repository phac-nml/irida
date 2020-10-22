package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline;

import java.util.List;

public class SavedPipelineParameters {
    private final Long id;
    private final String label;
    private final List<PipelineParameter> parameters;

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
}
