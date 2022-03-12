package ca.corefacility.bioinformatics.irida.ria.web.activities;

/**
 * Define specific type activities.
 * NOTE: These are used in the UI on the activities pages, please update there if
 * you update them here.
 */
public enum ActivityType {
	PROJECT_USER_ROLE("project_user_role_updated"),
	PROJECT_USER_REMOVED("project_user_removed"),
	PROJECT_SAMPLE_ADDED("project_sample_added"),
	PROJECT_SAMPLE_REMOVED("project_sample_removed"),
	PROJECT_SAMPLE_DATA_ADDED("project_sample_data_added"),
	PROJECT_USER_GROUP_ADDED("project_user_group_added"),
	PROJECT_USER_GROUP_REMOVED("project_user_group_removed");

	public final String label;

	private ActivityType(String label) {
		this.label = label;
	}
}
