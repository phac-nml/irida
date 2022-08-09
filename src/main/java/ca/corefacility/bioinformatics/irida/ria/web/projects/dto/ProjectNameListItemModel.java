package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

/**
 * Model for UI to represent a project list item.
 */
public class ProjectNameListItemModel {
	private Long id;
	private String name;

	public ProjectNameListItemModel(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
