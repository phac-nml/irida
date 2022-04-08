package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.Date;
import java.util.List;

import com.google.common.base.Strings;

/**
 * Used the get the form details submitted via a post to export samples to a CSV.
 */
public class ExportToFileModel {
	private List<String> sampleNames;
	private List<Long> associated;
	private String search;
	private Date startDate;
	private Date endDate;
	private String type;
	private String name;
	private String organism;
	private String description;
	private String collectedBy;
	private String strain;

	public ExportToFileModel() {
	}

	public ExportToFileModel(List<String> sampleNames, List<Long> associated, String search, Date startDate,
			Date endDate, String type, String name, String organism, String description, String collectedBy, String strain) {
		this.sampleNames = sampleNames;
		this.associated = associated;
		this.search = search;
		this.startDate = startDate;
		this.endDate = endDate;
		this.type = type;
		this.name = name;
		this.organism = organism;
		this.description = description;
		this.collectedBy = collectedBy;
		this.strain = strain;
	}

	public List<String> getSampleNames() {
		return sampleNames;
	}

	public void setSampleNames(List<String> sampleNames) {
		this.sampleNames = sampleNames;
	}

	public List<Long> getAssociated() {
		return associated;
	}

	public void setAssociated(List<Long> associated) {
		this.associated = associated;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return Strings.isNullOrEmpty(name) ? "" : name;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public String getCollectedBy() {
		return collectedBy;
	}

	public void setCollectedBy(String collectedBy) {
		this.collectedBy = collectedBy;
	}
	public String getStrain() {
		return strain;
	}

	public void setStrain(String strain) {
		this.strain = strain;
	}
}
