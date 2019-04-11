package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

/**
 * Return value for adding samples to the samples cart.
 */
public class AddToCartResponse {
	/**
	 * Success message for indicating what was added to the cart.
	 */
	private String added;

	/**
	 * Information message for indicating if a sample (with the same id) is
	 * already in the cart.
	 */
	private String existing;

	/**
	 * Error message indicating samples that have the same name but different ids
	 * were added to the cart.
	 */
	private String duplicate;

	/**
	 * Total number of samples in the cart after the update.
	 */
	private int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getAdded() {
		return added;
	}

	public void setAdded(String added) {
		this.added = added;
	}

	public String getExisting() {
		return existing;
	}

	public void setExisting(String existing) {
		this.existing = existing;
	}

	public String getDuplicate() {
		return duplicate;
	}

	public void setDuplicate(String duplicate) {
		this.duplicate = duplicate;
	}
}
