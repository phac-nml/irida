package ca.corefacility.bioinformatics.irida.ria.web.dto.list;

/**
 * Generic List item for UI responses.
 */
public abstract class ListItem {
	private final long id;

	public ListItem(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

}
