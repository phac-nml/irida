package ca.corefacility.bioinformatics.irida.model.workflow.description;

import java.util.List;

public interface IridaWorkflowDynamicSource {
    Object getSource();

    String getName();
}
