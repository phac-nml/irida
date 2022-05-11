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
	HOURLY(new int[] { 1 }, "%H:00"),
	/*
	 * Time Period: 7, 14, and 30 days
	 * Grouped by: month/day
	 */
	DAILY(new int[] { 7, 14, 30 }, "%b %e"),
	/*
	 * Time Period: 90 and 365 days
	 * Grouped by: month/year
	 */
	MONTHLY(new int[] { 90, 365 }, "%b %Y"),
	/*
	 * Time Period: 730, 1825, and 3650 days (2 years, 5 years, 10 years)
	 * Grouped by: year
	 */
	YEARLY(new int[] { 730, 1825, 3650 }, "%Y");

	private int[] values;
	private String groupByFormat;

	private StatisticTimePeriod(int[] values, String groupByFormat) {
		this.values = values;
		this.groupByFormat = groupByFormat;
	}

	public int[] getValues() {
		return values;
	}

	public String getGroupByFormat() {
		return groupByFormat;
	}
}
