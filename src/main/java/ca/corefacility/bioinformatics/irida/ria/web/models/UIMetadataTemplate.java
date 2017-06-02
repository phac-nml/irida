package ca.corefacility.bioinformatics.irida.ria.web.models;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

import java.util.ArrayList;
import java.util.List;

/**
 * User interface model for a {@link MetadataTemplate}
 * This is required for creating a new {@link MetadataTemplate} since the UI create new {@link MetadataTemplateField}
 * only lists {@link String} representations of them.
 */
public class UIMetadataTemplate {
    private Long id;
    private String name;
    private List<String> fields;

    public UIMetadataTemplate() {
    }

    public UIMetadataTemplate(MetadataTemplate metadataTemplate) {
        this.id = metadataTemplate.getId();
        this.name = metadataTemplate.getName();
        this.fields = new ArrayList<>();
        for (MetadataTemplateField field : metadataTemplate.getFields()) {
            this.fields.add(field.getLabel());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
