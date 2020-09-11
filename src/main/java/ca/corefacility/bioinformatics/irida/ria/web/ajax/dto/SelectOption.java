package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

public class SelectOption {
	private long value; // Should be id for the item
	private String text; // Should be the name for the item;

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
