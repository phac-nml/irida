package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.list;

public abstract class ListItem {
	private final long id;

	public ListItem(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

}
