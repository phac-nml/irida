package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.cart;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CartSample;

/**
 * Model for UI to represent a project and its sample within the cart.
 */
public class CartProject {
	private final Long id;
	private final String label;
	private  List<CartSample> samples;

	public CartProject(Long id, String label) {
		this.id = id;
		this.label = label;
	}

	public void setSamples(List<CartSample> samples) {
		this.samples = samples;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public List<CartSample> getSamples() {
		return samples;
	}
}
