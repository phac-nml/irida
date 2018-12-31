package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Object to handle samples that have been sent to the server to be added to the
 * cart.
 */
public class CartSampleRequest {
	/**
	 * {@link Sample} identifier.
	 */
	private Long id;

	/**
	 * {@link Sample} label.
	 */
	private String label;

	/**
	 * {@link boolean} whether the sample is editable by the current user.
	 */
	private boolean editable;

	public CartSampleRequest() {
	}

	public CartSampleRequest(Long id, String label, boolean editable) {
		this.id = id;
		this.label = label;
		this.editable = editable;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
