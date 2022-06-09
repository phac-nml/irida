package ca.corefacility.bioinformatics.irida.ria.web.models;

public abstract class MinimalModel {
	private final Long id;
	private final String name;

	public MinimalModel(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
