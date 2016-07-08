package ca.corefacility.bioinformatics.irida.model.project;

public enum ProjectSyncFrequency {
	NEVER(Integer.MAX_VALUE), DAILY(1), WEEKLY(7), MONTHLY(30), SEMIMONTHLY(60), QUARTERLY(90);

	private int days;

	private ProjectSyncFrequency(int days) {
		this.days = days;
	}

	public int getDays() {
		return days;
	}
}
