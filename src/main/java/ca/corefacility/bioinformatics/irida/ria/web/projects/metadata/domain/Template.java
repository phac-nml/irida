package ca.corefacility.bioinformatics.irida.ria.web.projects.metadata.domain;

import java.util.List;

public class Template {
    private Long id;
    private String name;
    private List<String> fields;

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
