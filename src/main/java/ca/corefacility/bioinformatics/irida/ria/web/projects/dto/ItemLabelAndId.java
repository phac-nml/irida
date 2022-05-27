package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

public class ItemLabelAndId {
	private final Long id;
	private final String label;

	public ItemLabelAndId(Long id, String label) {
		this.id = id;
		this.label = label;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
}
