package ca.corefacility.bioinformatics.irida.model.enums;

/**
 * Defines a arrays of time periods and group by formats used
 * for getting statistics for the Admin Panel.
 *
 */

public enum StatisticTimePeriod {
	HOURLY(new int[] {1}, "%H:00"),
	DAILY(new int[] {7,14,30}, "%%m/%d"),
	MONTHLY(new int[] {90, 365}, "%m/%y"),
	YEARLY(new int[] {730, 1825, 3650}, "%Y");

	private int [] values;
	private String groupByFormat;

	private StatisticTimePeriod(int[] values, String groupByFormat) {
		this.values = values;
		this.groupByFormat = groupByFormat;
	}

	public int[] getDaily() {
		return DAILY.values;
	}

	public int[] getMonthly() {
		return MONTHLY.values;
	}

	public int[] getYearly() {
		return YEARLY.values;
	}

	public int[] getValues() {
		return values;
	}

	public void setValues(int[] values) {
		this.values = values;
	}

	public String getGroupByFormat() {
		return groupByFormat;
	}

	public void setGroupByFormat(String groupByFormat) {
		this.groupByFormat = groupByFormat;
	}

}
