package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * DataTables response object for {@link ProjectSampleJoin}
 */
public class DTProjectSamples implements DataTablesResponseModel {
	private Long id;
	private Long projectId;
	private String sampleName;
	private String organism;
	private String projectName;
	private Date createdDate;
	private Date modifiedDate;
	private List<String> qcEntries;
	private boolean owner;

	public DTProjectSamples(ProjectSampleJoin projectSampleJoin, List<String> qcEntries) {
		Project project = projectSampleJoin.getSubject();
		Sample sample = projectSampleJoin.getObject();

		this.id = sample.getId();
		this.sampleName = sample.getSampleName();
		this.organism = sample.getOrganism();
		this.projectName = project.getName();
		this.projectId = project.getId();
		this.createdDate = sample.getCreatedDate();
		this.modifiedDate = sample.getModifiedDate();
		this.qcEntries = qcEntries;
		this.owner = projectSampleJoin.isOwner();
	}

	public Long getId() {
		return this.id;
	}

	public String getSampleName() {
		return sampleName;
	}

	public String getOrganism() {
		return organism;
	}

	public Long getProjectId() {
		return projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public List<String> getQcEntries() {
		return qcEntries;
	}
	
	public boolean isOwner(){
		return owner;
	}
}
