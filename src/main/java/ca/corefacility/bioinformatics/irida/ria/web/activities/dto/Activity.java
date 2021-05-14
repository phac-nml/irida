package ca.corefacility.bioinformatics.irida.ria.web.activities.dto;

import java.util.Date;
import java.util.List;

public class Activity {
	private final String sentence;
	private final Date date;
	private final List<ActivityItem> items;

	public Activity(String sentence, Date date, List<ActivityItem> items) {
		this.sentence = sentence;
		this.date = date;
		this.items = items;
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
}
