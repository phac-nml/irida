package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * User interface model for DataTables for {@link Project}
 */
public class DTProject implements DataTablesResponseModel {
	private Long id;
	private String name;
	private String organism;
	private Long samples;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isRemote;

	public DTProject(Project project, Long sampleCount) {
		this.id = project.getId();
		this.name = project.getName();
		this.organism = project.getOrganism();
		this.samples = sampleCount;
		this.createdDate = project.getCreatedDate();
		this.modifiedDate = project.getModifiedDate();
		this.isRemote = project.isRemote();
	}

	@Override
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
