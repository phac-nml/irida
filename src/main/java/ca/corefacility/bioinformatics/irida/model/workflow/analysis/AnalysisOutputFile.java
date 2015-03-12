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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;

/**
 * Store file references to files produced by a workflow execution that we
 * otherwise don't want to parse metadata from.
 * 
 *
 */
@Entity
@Table(name = "analysis_output_file")
public class AnalysisOutputFile implements IridaThing, VersionedFileFields<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final Long id;

	@Column(name = "file_path", unique = true)
	@NotNull(message = "{analysis.output.file.file.notnull}")
	@JsonIgnore
	private final Path file;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false)
	private final Date createdDate;

	@NotNull(message = "{analysis.output.file.execution.manager.file.id}")
	@Column(name = "execution_manager_file_id")
	private final String executionManagerFileId;

	@Column(name = "file_revision_number")
	private final Long fileRevisionNumber; // the filesystem file revision number

	@NotNull
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "tool_execution_id")
	private final ToolExecution createdByTool;

	/**
	 * for hibernate
	 */
	@SuppressWarnings("unused")
	private AnalysisOutputFile() {
		this.createdDate = new Date();
		this.fileRevisionNumber = 0L;
		this.id = null;
		this.file = null;
		this.executionManagerFileId = null;
		this.createdByTool = null;
	}

	/**
	 * Create a new instance of {@link AnalysisOutputFile}.
	 * 
	 * @param file
	 *            the file that this resource owns.
	 * @param executionManagerFileId
	 *            the identifier for this file in the execution manager that it
	 *            was created by.
	 * @param analysis
	 *            the analysis that owns this resource.
	 * @param createdByTool
	 *            the tools that were used to create the file.
	 */
	public AnalysisOutputFile(final Path file, final String executionManagerFileId, final ToolExecution createdByTool) {
		this.id = null;
		this.createdDate = new Date();
		this.file = file;
		this.executionManagerFileId = executionManagerFileId;
		this.createdByTool = createdByTool;
		this.fileRevisionNumber = 0L;
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
		//this.fileRevisionNumber++;
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
		return this.createdDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		throw new UnsupportedOperationException("AnalysisOutputFile is immutable.");
	}

	public Path getFile() {
		return file;
	}

	public String getExecutionManagerFileId() {
		return executionManagerFileId;
	}

	public void setId(Long id) {
		throw new UnsupportedOperationException("AnalysisOutputFile is immutable.");
	}

	public final ToolExecution getCreatedByTool() {
		return createdByTool;
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
