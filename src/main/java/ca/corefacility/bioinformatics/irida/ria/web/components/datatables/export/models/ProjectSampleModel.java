package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.export.models;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Used for exporting the project samples table.  Prevents undefined from displaying in the table,
 * and adds the project name.
 */
public class ProjectSampleModel {
	private Project project;
	private Sample sample;

	public ProjectSampleModel(ProjectSampleJoin psj) {
		this.project = psj.getSubject();
		this.sample = psj.getObject();
	}

	public Long getId() {
		return sample.getId();
	}

	public String getSampleName() {
		return sample.getSampleName();
	}

	public String getProjectName() {
		return project.getName();
	}

	public String getCreatedDate() {
		return sample.getCreatedDate() != null ? sample.getCreatedDate().toString() : "";
	}

	public String getModifiedDate() {
		return sample.getModifiedDate() != null ? sample.getModifiedDate().toString() : "";
	}

	public String getDescription() {
		return sample.getDescription() != null ? sample.getDescription() : "";
	}

	public String getOrganism() {
		return sample.getOrganism() != null ? sample.getOrganism() : "";
	}

	public String getIsolate() {
		return sample.getIsolate() != null ?  sample.getIsolate() : "";
	}

	public String getCollectedBy() {
		return sample.getCollectedBy() != null ? sample.getCollectedBy() : "";
	}

	public String getCollectionDate() {
		return sample.getCollectionDate() != null ? sample.getCollectionDate().toString() : "";
	}

	public String getGeographicLocationName() {
		return sample.getGeographicLocationName() != null ? sample.getGeographicLocationName() : "";
	}

	public String getIsolationSource() {
		return sample.getIsolationSource() != null ? sample.getIsolationSource() : "";
	}

	public String getLatitude() {
		return sample.getLatitude() != null ? sample.getLatitude() : "";
	}

	public String getLongitude() {
		return sample.getLongitude() != null ? sample.getLongitude() : "";
	}
}
