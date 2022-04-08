package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import com.google.common.collect.ImmutableList;

/**
 * Used for exporting the project samples table.  Prevents undefined from displaying in the table,
 * and adds the project name.
 */
public class ProjectSampleModel extends AbstractExportModel {
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
			"arrivalDate",
			"sequencedBy",
			"geographicLocationName",
			"geographicLocationName2",
			"geographicLocationName3",
			"isolationSource",
			"patientAge",
			"patientVaccinationNumber",
			"patientVaccinationDate",
			"latitude",
			"longitude",
			"owner"
	);
	private Project project;
	private Sample sample;
	private List<QCEntry> qcEntries;
	private ProjectSampleJoin join;

	public ProjectSampleModel(ProjectSampleJoin psj, List<QCEntry> qcEntries) {
		this.project = psj.getSubject();
		this.sample = psj.getObject();
		this.qcEntries = qcEntries;
		this.join = psj;
	}

	public String getId() {
		return checkNullId(sample.getId());
	}

	public String getSampleName() {
		return checkNullStrings(sample.getSampleName());
	}

	public Long getProjectId() {
		return project.getId();
	}

	public String getProjectName() {
		return checkNullStrings(project.getName());
	}

	public String getCreatedDate() {
		return checkNullDate(sample.getCreatedDate());
	}

	public String getModifiedDate() {
		return checkNullDate(sample.getModifiedDate());
	}

	public String getDescription() {
		return checkNullStrings(sample.getDescription());
	}

	public String getOrganism() {
		return checkNullStrings(sample.getOrganism());
	}

	public String getIsolate() {
		return checkNullStrings(sample.getIsolate());
	}

	public String getCollectedBy() {
		return checkNullStrings(sample.getCollectedBy());
	}

	public String getCollectionDate() {
		return checkNullDate(sample.getCollectionDate());
	}
	
	public String getArrivalDate() {
		return checkNullDate(sample.getArrivalDate());
	}

	public String getSequencedBy() {
		return checkNullStrings(sample.getSequencedBy());
	}

	public String getGeographicLocationName() {
		return checkNullStrings(sample.getGeographicLocationName());
	}

	public String getGeographicLocationName2() {
		return checkNullStrings(sample.getGeographicLocationName2());
	}

	public String getGeographicLocationName3() {
		return checkNullStrings(sample.getGeographicLocationName3());
	}

	public String getIsolationSource() {
		return checkNullStrings(sample.getIsolationSource());
	}

	public String getPatientAge() {
		return checkNullStrings(sample.getPatientAge());
	}

	public String getPatientVaccinationNumber() {
		return checkNullStrings(sample.getPatientVaccinationNumber());
	}

	public String getPatientVaccinationDate() {
		return checkNullDate(sample.getPatientVaccinationDate());
	}

	public String getLatitude() {
		return checkNullStrings(sample.getLatitude());
	}

	public String getLongitude() {
		return checkNullStrings(sample.getLongitude());
	}
	
	public List<QCEntry> getQcEntries(){
		return qcEntries;
	}
	
	public boolean isOwner(){
		return join.isOwner();
	}
}
