package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

import java.util.Date;

/**
 * Used as the base class of any item to be represented in an ant.design Table.
 */
public abstract class TableModel {
	private final Long id;
	private final String key;
	private String name;
	private final Date createdDate;
	private final Date modifiedDate;

	public TableModel(Long id, String name, Date createdDate, Date modifiedDate) {
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		key = String.valueOf(id); // Strictly for react
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

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}
}