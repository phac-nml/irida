package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

public class CartSamples extends AjaxResponse {
	private final List<Sample> locked;
	private final List<Sample> unlocked;

	public CartSamples(List<Sample> locked, List<Sample> unlocked) {
		this.locked = locked;
		this.unlocked = unlocked;
	}

	public List<Sample> getLocked() {
		return locked;
	}

	public List<Sample> getUnlocked() {
		return unlocked;
	}
}
