package ca.corefacility.bioinformatics.irida.ria.web.activities.dto;

import java.util.Date;
import java.util.List;

public class Activity {
	private final Long id;
	private String type;
	private final String sentence;
	private final Date date;
	private final List<ActivityItem> items;

	public Activity(Long id, String type, String sentence, Date date, List<ActivityItem> items) {
		this.id = id;
		this.type = type;
		this.sentence = sentence;
		this.date = date;
		this.items = items;
	}

	public Long getId() {
		return id;
	}

	public String getSentence() {
		return sentence;
	}

	public Date getDate() {
		return date;
	}

	public List<ActivityItem> getItems() {
		return items;
	}

	public String getType() {
		return type;
	}
}
