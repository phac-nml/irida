package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * The resource for displaying the API responses.
 *
 * @param <Type> The type of the object to be displayed in the response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResource<Type> {
	private Type resource;
	@Schema(hidden = true)
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
		this.warnings = warnings;
	}
}
