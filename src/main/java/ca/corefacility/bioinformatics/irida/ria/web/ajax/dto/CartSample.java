package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Used to represent a {@link ca.corefacility.bioinformatics.irida.model.sample.Sample} on the UI Cart Page.
 */
public class CartSample {
	private final Long id;
	private final String label;

	public CartSample(Sample sample) {
		this.id = sample.getId();
		this.label = sample.getLabel();
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
}
