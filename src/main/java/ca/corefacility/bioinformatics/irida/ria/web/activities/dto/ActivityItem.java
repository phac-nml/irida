package ca.corefacility.bioinformatics.irida.ria.web.activities.dto;

/**
 * Used to create either the subject or the predicate of an activity.
 * Most subjects have links (user, project, etc...), if the activity has a link
 * the UI will create the anchor tag for it.
 */
public class ActivityItem {
	private final String href;
	private final String label;

	public ActivityItem(String href, String label) {
		this.href = href;
		this.label = label;
	}

	public String getHref() {
		return href;
	}

	public String getLabel() {
		return label;
	}
}


