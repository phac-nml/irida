package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Data transfer object for getting the samples that are in the cart.  Since a user
 * can only create a project with samples they can modify, they are seperated out into
 * locked and unlocked.
 */
public class CartSamplesByUserPermissions extends AjaxResponse {
	private final List<Sample> locked;
	private final List<Sample> unlocked;

	public CartSamplesByUserPermissions(List<Sample> locked, List<Sample> unlocked) {
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
