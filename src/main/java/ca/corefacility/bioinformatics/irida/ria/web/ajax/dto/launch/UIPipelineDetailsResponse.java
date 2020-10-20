package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.launch;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Model class to send details about a workflow pipeline to the UI
 * Used on the launch pipeline page.
 */
public class UIPipelineDetailsResponse extends AjaxResponse {
    private String name;
    private String description;
    private String type;

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
}
