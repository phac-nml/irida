package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesExportable;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * DataTables response object for {@link ProjectSampleJoin}
 */
public class DTProjectSamples implements DataTablesResponseModel, DataTablesExportable {
	private final String dataPattern = "MMM dd, yyyy";
	private final DateFormat dateFormatter = new SimpleDateFormat(dataPattern);

	private Long id;
	private Long projectId;
	private String sampleName;
	private String organism;
	private String projectName;
	private Date createdDate;
	private Date modifiedDate;
	private List<String> qcEntries;
	private Double coverage;
	private boolean owner;

	public DTProjectSamples(ProjectSampleJoin projectSampleJoin, List<String> qcEntries, Double coverage) {
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
		this.coverage = coverage;
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

	public Double getCoverage() {
		return coverage;
	}

	public boolean isOwner(){
		return owner;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getExportableTableRow() {
		List<String> data = new ArrayList<>();
		data.add(String.valueOf(this.getId()));
		data.add(this.getSampleName());
		data.add(this.getOrganism());
		data.add(String.valueOf(this.getProjectId()));
		data.add(this.getProjectName());
		data.add(this.getCreatedDate() != null ? dateFormatter.format(this.getCreatedDate()) : "");
		data.add(this.getModifiedDate() != null ? dateFormatter.format(this.getModifiedDate()) : "");
		data.add(this.getCoverage() != null ? String.valueOf(this.getCoverage()) : "");
		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getExportableTableHeaders(MessageSource messageSource, Locale locale) {
		List<String> headers = new ArrayList<>();
		headers.add(messageSource.getMessage("iridaThing.id", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.name", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.organism", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.project-id", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.project", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.created", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.modified", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.coverage", new Object[] {}, locale));
		return headers;
	}
}
