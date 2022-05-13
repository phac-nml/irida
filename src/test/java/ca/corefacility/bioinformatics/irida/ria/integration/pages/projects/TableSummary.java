package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableSummary {

	private int selected;
	private int total;

	public TableSummary(String summary) {
		Pattern pattern = Pattern.compile("Selected: (\\d+) of (\\d+)");
		Matcher matcher = pattern.matcher(summary);

		if (matcher.find()) {
			this.selected = Integer.parseInt(matcher.group(1));
			this.total = Integer.parseInt(matcher.group(2));
		}
	}

	public int getSelected() {
		return selected;
	}

	public int getTotal() {
		return total;
	}
}
