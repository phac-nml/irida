package ca.corefacility.bioinformatics.irida.ria.web.components.ant.table;

import java.util.Date;

public abstract class TableModel {
	private Long id;
	private String name;
	private Date createdDate;
	private Date modifiedDate;

	public TableModel(Long id, String name, Date createdDate, Date modifiedDate) {
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public Long getId() {
		return id;
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
