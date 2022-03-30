package ca.corefacility.bioinformatics.irida.ria.web.activities.dto;

import java.util.Date;
import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.dto.list.ListItem;

/**
 * Model for an Activity (Event).
 */
public class Activity extends ListItem {
	private Long id;
	private String type;
	private final String description;
	private final Date date;
	private final List<ActivityItem> items;

	public Activity(Long id, String type, String description, Date date, List<ActivityItem> items) {
		this.id = id;
		this.type = type;
		this.description = description;
		this.date = date;
		this.items = items;
	}

	public Long getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public Date getDate() {
		return date;
	}

	public List<ActivityItem> getItems() {
		return items;
	}
}
