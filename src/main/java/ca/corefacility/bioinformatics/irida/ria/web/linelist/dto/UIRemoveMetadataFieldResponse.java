package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

/**
 * Handles the return from delete a {@link ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField}
 * from a {@link ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartSample.Project}
 */
public class UIRemoveMetadataFieldResponse {
	private String message;

	public UIRemoveMetadataFieldResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
