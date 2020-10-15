package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.cart;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CartSampleModel;

import java.util.List;

/**
 * Model for UI to represent a project and its sample within the cart.
 */
public class CartProjectModel {
	private final Long id;
	private final String label;
	private  List<CartSampleModel> samples;

	public CartProjectModel(Long id, String label) {
		this.id = id;
		this.label = label;
	}

	public void setSamples(List<CartSampleModel> samples) {
		this.samples = samples;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public List<CartSampleModel> getSamples() {
		return samples;
	}
}
