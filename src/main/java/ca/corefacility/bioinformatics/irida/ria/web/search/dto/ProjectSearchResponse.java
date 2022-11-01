package ca.corefacility.bioinformatics.irida.ria.web.search.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * Response object for global search for {@link Project}
 */
public class ProjectSearchResponse extends SearchItem {
	private final Long id;
	private final String name;
	private final String organism;
	private final Long samples;
	private final Date createdDate;
	private final Date modifiedDate;
	private final boolean isRemote;

	public ProjectSearchResponse(Project project, Long sampleCount) {
		this.id = project.getId();
		this.name = project.getName();
		this.organism = project.getOrganism();
		this.samples = sampleCount;
		this.createdDate = project.getCreatedDate();
		this.modifiedDate = project.getModifiedDate();
		this.isRemote = project.isRemote();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOrganism() {
		return organism;
	}

	public Long getSamples() {
		return samples;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public boolean isRemote() {
		return isRemote;
	}
}
