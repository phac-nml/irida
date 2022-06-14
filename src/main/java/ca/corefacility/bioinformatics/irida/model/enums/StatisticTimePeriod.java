package ca.corefacility.bioinformatics.irida.model.enums;

/**
 * Defines a arrays of time periods and group by formats used for getting statistics for the Admin Panel.
 */

public enum StatisticTimePeriod {
	/*
	 * Each enum attribute contains an int array of values
	 * which is the time period in days, and a groupByFormat
	 * which is used to group statistics by the defined string.
	 */

	/*
	 * Time Period: Last day
	 * Grouped by: HOUR
	 */
	HOURLY(new int[] { 1 }, "%H:00", "%H:00"),
	/*
	 * Time Period: 7, 14, and 30 days
	 * Grouped by: month/day
	 */
	DAILY(new int[] { 7, 14, 30 }, "%Y-%m-%d", "MMM d"),
	/*
	 * Time Period: 90 and 365 days
	 * Grouped by: month/year
	 */
	MONTHLY(new int[] { 90, 365 }, "%Y-%m-01", "MMM Y"),
	/*
	 * Time Period: 730, 1825, and 3650 days (2 years, 5 years, 10 years)
	 * Grouped by: year
	 */
	YEARLY(new int[] { 730, 1825, 3650 }, "%Y-01-01", "Y");

	private int[] values;
	private String groupByFormat;
	private String displayFormat;

	private StatisticTimePeriod(int[] values, String groupByFormat, String displayFormat) {
		this.values = values;
		this.groupByFormat = groupByFormat;
		this.displayFormat = displayFormat;
	}

	public int[] getValues() {
		return values;
	}

	public String getGroupByFormat() {
		return groupByFormat;
	}

	public String getDisplayFormat() {
		return displayFormat;
	}
}
