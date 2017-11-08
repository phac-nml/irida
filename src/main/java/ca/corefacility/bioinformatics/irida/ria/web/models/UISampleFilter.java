package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User Interface model for available field to filter the Project Samples by.
 */
public class UISampleFilter {
	private final String dateRangeFormatPattern = "MMMMM dd, yyyy";
	private final DateFormat dateRangeFormatter = new SimpleDateFormat(dateRangeFormatPattern);

	private String name;
	private String organism;
	private List<Long> associated;
	private Date startDate;
	private Date endDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public List<Long> getAssociated() {
		return associated;
	}

	public void setAssociated(List<Long> associated) {
		this.associated = associated;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDateRange() {
		if (startDate == null || endDate == null) {
			return "";
		}
		return dateRangeFormatter.format(startDate) + " - " + dateRangeFormatter.format(endDate);
	}
}
