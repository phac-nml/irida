package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import com.google.common.collect.ImmutableList;

/**
 * Used for exporting the project samples table.  Prevents undefined from displaying in the table,
 * and adds the project name.
 */
public class ProjectSampleModel {
	/**
	 * Attributes on the {@link Sample}
	 */
	public static List<String> attributes = ImmutableList.of(
			"id",
			"sampleName",
			"projectName",
			"createdDate",
			"modifiedDate",
			"description",
			"organism",
			"isolate",
			"strain",
			"collectedBy",
			"collectionDate",
			"geographicLocationName",
			"isolationSource",
			"latitude",
			"longitude"
	);
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
