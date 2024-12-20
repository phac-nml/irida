package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

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
	private final List<String> quality;
	private final String qcStatus;
	private final Long coverage;

	public ProjectSampleTableItem(ProjectSampleJoin join, List<String> quality, String qcStatus, Long coverage) {
		super(join.getId());
		this.owner = join.isOwner();
		this.sample = new SampleObject(join.getObject());
		this.project = new ProjectObject(join.getSubject());
		this.quality = quality;
		this.qcStatus = qcStatus;
		this.coverage = coverage;
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

	public List<String> getQuality() {
		return quality;
	}

	public String getQcStatus() {
		return qcStatus;
	}

	public Long getCoverage() {
		return coverage;
	}
}
