package ca.corefacility.bioinformatics.irida.model.enums;

public enum GroupByFormat {
	HOURLY("%H:00"),
	DAILY("%m/%d"),
	MONTHLY("%m/%y"),
	YEARLY("%Y");

	private String groupByFormat;

	private GroupByFormat(String groupByFormat) {
		this.groupByFormat = groupByFormat;
	}

	public static GroupByFormat fromString(String timePeriod) {
		switch (timePeriod.toUpperCase()) {
		case "DAILY":
			return DAILY;
		case "MONTHLY":
			return MONTHLY;
		case "YEARLY":
			return YEARLY;
		default:
			return HOURLY;
		}
	}

	@Override
	public String toString() {
		return groupByFormat;
	}
}
