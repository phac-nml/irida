package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;

/**
 * Store file references to files produced by a workflow execution that we
 * otherwise don't want to parse metadata from.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "analysis_output_file")
@Audited
public class AnalysisOutputFile implements IridaThing, VersionedFileFields<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "file_path", unique = true)
	@NotNull(message = "{analysis.output.file.file.notnull}")
	@JsonIgnore
	private Path file;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false)
	private final Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date")
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JsonIgnore
	private Analysis analysis;

	@NotNull(message = "{analysis.output.file.execution.manager.file.id}")
	@Column(name = "execution_manager_file_id")
	private String executionManagerFileId;

	@Column(name = "file_revision_number")
	private Long fileRevisionNumber; // the filesystem file revision number

	@NotNull
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "tool_execution_id")
	@NotAudited
	private ToolExecution createdByTool;

	private AnalysisOutputFile() {
		this.createdDate = new Date();
		this.fileRevisionNumber = 0L;
	}

	public AnalysisOutputFile(Path file, String executionManagerFileId) {
		this();
		this.file = file;
		this.executionManagerFileId = executionManagerFileId;
	}

	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

	@Override
	public Long getFileRevisionNumber() {
		return this.fileRevisionNumber;
	}

	@Override
	public void incrementFileRevisionNumber() {
		this.fileRevisionNumber++;
	}

	@Override
	public String getLabel() {
		return this.file.getFileName().toString();
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Path getFile() {
		return file;
	}

	public void setFile(Path file) {
		this.file = file;
	}

	public Analysis getAnalysis() {
		return analysis;
	}

	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	public String getExecutionManagerFileId() {
		return executionManagerFileId;
	}

	public void setExecutionManagerFileId(String executionManagerFileId) {
		this.executionManagerFileId = executionManagerFileId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setFileRevisionNumber(Long fileRevisionNumber) {
		this.fileRevisionNumber = fileRevisionNumber;
	}

	public final ToolExecution getCreatedByTool() {
		return createdByTool;
	}

	public final void setCreatedByTool(ToolExecution createdByTool) {
		this.createdByTool = createdByTool;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(file, executionManagerFileId, fileRevisionNumber);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof AnalysisOutputFile) {
			AnalysisOutputFile a = (AnalysisOutputFile) o;
			return Objects.equals(file, a.file) && Objects.equals(executionManagerFileId, a.executionManagerFileId)
					&& Objects.equals(fileRevisionNumber, a.fileRevisionNumber);
		}

		return false;
	}
}
