package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

public class ItemNameAndId {
	private final Long id;
	private final String name;

	public ItemNameAndId(Long id, String label) {
		this.id = id;
		this.name = label;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
