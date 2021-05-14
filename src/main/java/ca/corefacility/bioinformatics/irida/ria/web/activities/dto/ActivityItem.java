package ca.corefacility.bioinformatics.irida.ria.web.activities.dto;

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


