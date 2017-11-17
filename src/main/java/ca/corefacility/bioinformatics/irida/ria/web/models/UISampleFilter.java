package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.Strings;

/**
 * User Interface model for available field to filter the Project Samples by.
 */
public class UISampleFilter {
	private final String dateRangeFormatPattern = "MMMMM dd, yyyy";
	private final DateFormat dateRangeFormatter = new SimpleDateFormat(dateRangeFormatPattern);

	private String name;
	private String organism;
	private String startDate;
	private String endDate;

	public String getName() {
		return Strings.isNullOrEmpty(name) ? "" : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganism() {
		return Strings.isNullOrEmpty(organism) ? "" : organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public Date getStartDate() {
		if (Strings.isNullOrEmpty(startDate)) {
			return null;
		} else {
			return new Date(Long.valueOf(startDate));
		}
	}

	public void setStartDate(String date) {
		this.startDate = date;
	}

	public Date getEndDate() {
		if (Strings.isNullOrEmpty(endDate)) {
			return null;
		} else {
			return new Date(Long.valueOf(endDate));
		}
	}

	public void setEndDate(String date) {
		this.endDate = date;
	}
}
