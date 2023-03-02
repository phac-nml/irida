package ca.corefacility.bioinformatics.irida.ria.web.search.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableItem;

/**
 * Generic response item for the global search table
 */
public class SearchItem extends AntTableItem {
	private final Long id;
	private final String name;
	private final Date createdDate;
	private final Date modifiedDate;

	public SearchItem(Long id, String name, Date createdDate, Date modifiedDate) {
		super(id);
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
