package ca.corefacility.bioinformatics.irida.ria.web.components.ant.table;

import java.util.Date;

/**
 * Used as the base class of any item to be represented in an ant.design Table.
 */
public abstract class TableModel {
	private Long id;
	private String key;
	private String name;
	private Date createdDate;
	private Date modifiedDate;

	public TableModel(Long id, String name, Date createdDate, Date modifiedDate) {
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.key = String.valueOf(id); // Strictly for react
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
