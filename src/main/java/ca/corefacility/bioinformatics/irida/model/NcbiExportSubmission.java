package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.export.NcbiBioSampleFiles;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Class storing a request to upload sequence data to NCBI.
 */
@Entity
@Table(name = "ncbi_export_submission")
@EntityListeners(AuditingEntityListener.class)
public class NcbiExportSubmission implements IridaThing {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;

	@Column(name = "bio_project_id", nullable = false)
	private String bioProjectId;

	@Column(name = "namespace", nullable = false)
	private String ncbiNamespace;

	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="ncbi_export_submission_biosample")
	private List<NcbiBioSampleFiles> bioSampleFiles;

	@Column(name = "created_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@LastModifiedDate
	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@Column(name="release_date")
	@Temporal(TemporalType.DATE)
	private Date release_date;

	@Column(name = "upload_state", nullable = false)
	@Enumerated(EnumType.STRING)
	private ExportUploadState uploadState;

	public NcbiExportSubmission() {
		uploadState = ExportUploadState.NEW;
		createdDate = new Date();
	}

	public NcbiExportSubmission(Project project, String bioProjectId, String ncbiNamespace, Date release_date,
			List<NcbiBioSampleFiles> bioSampleFiles) {
		this();
		this.project = project;
		this.bioProjectId = bioProjectId;
		this.ncbiNamespace = ncbiNamespace;
		this.release_date = release_date;
		this.bioSampleFiles = bioSampleFiles;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public String getLabel() {
		return "NCBI Submission Project " + project.getLabel() + " " + createdDate;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public List<NcbiBioSampleFiles> getBioSampleFiles() {
		return bioSampleFiles;
	}

	public void setUploadState(ExportUploadState uploadState) {
		this.uploadState = uploadState;
	}

	public ExportUploadState getUploadState() {
		return uploadState;
	}

	public String getNcbiNamespace() {
		return ncbiNamespace;
	}

	public void setNcbiNamespace(String ncbiNamespace) {
		this.ncbiNamespace = ncbiNamespace;
	}

	public String getBioProjectId() {
		return bioProjectId;
	}

	public void setBioProjectId(String bioProjectId) {
		this.bioProjectId = bioProjectId;
	}
	
	public Date getRelease_date() {
		return release_date;
	}
}
