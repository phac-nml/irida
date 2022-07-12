package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.util.Date;

/**
 * Base for UI models
 */
public abstract class BaseRecord {
	private final Long id;
	private final String key;
	private final String name;
	private final Date createdDate;
	private final Date modifiedDate;

	public BaseRecord(Long id, String name, Date createdDate, Date modifiedDate) {
		this.id = id;
		this.key = ModelKeys.valueOf(this.getClass().getSimpleName()).label + id;
		this.name = name;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public Long getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}
}