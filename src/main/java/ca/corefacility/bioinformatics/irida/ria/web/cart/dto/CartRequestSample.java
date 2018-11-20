package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Object to handle samples that have been sent to the server to be added to the
 * cart.
 */
public class CartRequestSample {
	/**
	 * {@link Sample} identifier.
	 */
	private Long id;

	/**
	 * {@link Sample} label.
	 */
	private String label;

	public CartRequestSample() {
	}

	public CartRequestSample(Long id, String label) {
		this.id = id;
		this.label = label;
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
}
