package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;

/**
 * UI model for the priority and available priorities for a projects automated pipelines
 */
public class Priorities {
	private String priority;
	private List<SelectOption> priorities;

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public List<SelectOption> getPriorities() {
		return priorities;
	}

	public void setPriorities(List<SelectOption> priorities) {
		this.priorities = priorities;
	}
}
