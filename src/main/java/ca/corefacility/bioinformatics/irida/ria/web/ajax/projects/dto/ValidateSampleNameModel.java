package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto;

import java.util.List;

/**
 * Model for UI to represent a sample.
 */
public class ValidateSampleNameModel {
	private List<Long> ids;
	private String name;

	public ValidateSampleNameModel(List<Long> ids, String name) {
		this.ids = ids;
		this.name = name;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
