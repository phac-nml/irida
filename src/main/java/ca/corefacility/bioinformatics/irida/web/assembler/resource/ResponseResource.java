package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResource<Type> {
    private Type resource;
    private List<String> warnings;

    public ResponseResource(Type resource) {
        this.resource = resource;
    }

    public Type getResource() {
        return resource;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings= warnings;
    }
}
