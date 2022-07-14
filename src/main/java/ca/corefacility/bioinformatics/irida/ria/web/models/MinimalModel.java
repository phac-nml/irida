package ca.corefacility.bioinformatics.irida.ria.web.models;

/**
 * Basic implementation for items that want to return a smaller model to the UI Usually should stay as just name and
 * id.
 */
public abstract class MinimalModel {
	private final Long id;
	private final String name;
	private final String key;

	public MinimalModel(Long id, String name, String key) {
		this.id = id;
		this.name = name;
		this.key = key + id;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}
}
