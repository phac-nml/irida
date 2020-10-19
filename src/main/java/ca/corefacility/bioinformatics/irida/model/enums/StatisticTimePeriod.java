package ca.corefacility.bioinformatics.irida.model.enums;

public enum StatisticTimePeriod {
	DAILY(7,14,30),
	MONTHLY(90, 365),
	YEARLY(730, 1825, 3650);

	private int [] values;

	private StatisticTimePeriod(int... values) {
		this.values = values;
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
}
