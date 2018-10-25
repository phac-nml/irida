package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

public class CartResponse {
	private String added;
	private String existing;
	private String duplicate;
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
