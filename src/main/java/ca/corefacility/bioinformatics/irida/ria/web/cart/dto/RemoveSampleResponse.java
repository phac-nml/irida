package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

/**
 * Return object for the UI for when a sample is removed from the cart.
 */
public class RemoveSampleResponse {
	private int count;

	public RemoveSampleResponse() {
	}

	public RemoveSampleResponse(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}
}
