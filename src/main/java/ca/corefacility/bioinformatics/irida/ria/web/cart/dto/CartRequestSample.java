package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

public class CartRequestSample {
	private Long id;
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
