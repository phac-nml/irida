package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableItem;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.ProjectObject;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.SampleObject;

public class ProjectSampleTableItem extends AntTableItem {
	private String key;
	private SampleObject sample;
	private ProjectObject project;

	public ProjectSampleTableItem(ProjectSampleJoin join) {
		super("join-" + join.getId());
		this.sample = new SampleObject(join.getObject());
		this.project = new ProjectObject(join.getSubject());
	}

	public SampleObject getSample() {
		return sample;
	}

	public ProjectObject getProject() {
		return project;
	}

}
