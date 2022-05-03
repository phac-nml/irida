package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Data transfer object for getting the samples that are in the cart.  Since a user
 * can only create a project with samples they can modify, they are separated out into
 * locked and unlocked.
 */
public class CartSamplesByUserPermissions extends AjaxResponse {
	private final List<CartProjectSample> locked;
	private final List<CartProjectSample> unlocked;

	public CartSamplesByUserPermissions(List<CartProjectSample> locked, List<CartProjectSample> unlocked) {
		this.locked = locked;
		this.unlocked = unlocked;
	}

	public List<CartProjectSample> getLocked() {
		return locked;
	}

	public List<CartProjectSample> getUnlocked() {
		return unlocked;
	}
}