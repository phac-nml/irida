package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.util.Date;

import com.google.common.base.Strings;

/**
 * User Interface model for available field to filter the Project Samples by.
 */
public class UISampleFilter {

	private String name;
	private String description;
	private String collectedBy;
	private String organism;
	private String strain;
	private String startDate;
	private String endDate;

	public String getName() {
		return Strings.isNullOrEmpty(name) ? "" : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return Strings.isNullOrEmpty(description) ? "" : description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCollectedBy() {
		return Strings.isNullOrEmpty(collectedBy) ? "" : collectedBy;
	}

	public void setCollectedBy(String collectedBy) {
		this.collectedBy = collectedBy;
	}

	public String getOrganism() {
		return Strings.isNullOrEmpty(organism) ? "" : organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getStrain() {
		return Strings.isNullOrEmpty(strain) ? "" : strain;
	}

	public void setStrain(String strain) {
		this.strain = strain;
	}

	/**
	 * Get the start date of the filter
	 *
	 * @return start date
	 */
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

	/**
	 * Get the end date of the filter
	 *
	 * @return end date
	 */
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
