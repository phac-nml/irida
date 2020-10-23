package ca.corefacility.bioinformatics.irida.model.enums;

/**
 * Defines a set of formats which admin statistics
 * can be grouped by
 *
 */

public enum GroupByFormat {
	HOURLY("%H:00"),
	DAILY("%m/%d"),
	MONTHLY("%m/%y"),
	YEARLY("%Y");

	private String groupByFormat;

	private GroupByFormat(String groupByFormat) {
		this.groupByFormat = groupByFormat;
	}

	/**
	 * Gets the group by format from a string
	 *
	 * @param timePeriod The time period for which to get the group by format
	 * @return the group by format for the time period
	 */
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
