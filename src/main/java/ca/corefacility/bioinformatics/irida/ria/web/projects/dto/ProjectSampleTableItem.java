package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableItem;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.ProjectObject;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.SampleObject;

/**
 * Representation of a row used in the Project Samples table.
 */
public class ProjectSampleTableItem extends AntTableItem {
	private final Boolean owner;
	private final SampleObject sample;
	private final ProjectObject project;

	public ProjectSampleTableItem(ProjectSampleJoin join) {
		super(join.getId());
		this.owner = join.isOwner();
		this.sample = new SampleObject(join.getObject());
		this.project = new ProjectObject(join.getSubject());
	}

	public SampleObject getSample() {
		return sample;
	}

	public ProjectObject getProject() {
		return project;
	}

	public Boolean getOwner() {
		return owner;
	}
}
