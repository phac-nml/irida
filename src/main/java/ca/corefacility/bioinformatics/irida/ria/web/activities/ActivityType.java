package ca.corefacility.bioinformatics.irida.ria.web.activities;

public enum ActivityType {
	PROJECT_USER_ROLE("project_user_added"),
	PROJECT_SAMPLE_ADDED("project_sample_added"),
	PROJECT_SAMPLE_DATA_ADDED("project_sample_data_added");

	public final String label;

	private ActivityType(String label) {
		this.label = label;
	}
}
