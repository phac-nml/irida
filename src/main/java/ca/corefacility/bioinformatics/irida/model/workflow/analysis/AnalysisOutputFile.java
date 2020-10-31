package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl.RelativePathTranslatorListener;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.AnalysisAjaxController;

/**
 * Store file references to files produced by a workflow execution that we
 * otherwise don't want to parse metadata from.
 * 
 *
 */
@Entity
@Table(name = "analysis_output_file")
@EntityListeners(RelativePathTranslatorListener.class)
public class AnalysisOutputFile extends IridaResourceSupport implements IridaThing, VersionedFileFields<Long> {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisOutputFile.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id;

	@Column(name = "file_path", unique = true)
	@NotNull(message = "{analysis.output.file.file.notnull}")
	@com.fasterxml.jackson.annotation.JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	private final Path file;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false)
	private final Date createdDate;

	@NotNull(message = "{analysis.output.file.execution.manager.file.id}")
	@Column(name = "execution_manager_file_id")
	private final String executionManagerFileId;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "tool_execution_id")
	private final ToolExecution createdByTool;
	
	@Column(name = "label_prefix")
	private final String labelPrefix;

	/**
	 * for hibernate
	 */
	@SuppressWarnings("unused")
	private AnalysisOutputFile() {
		this.createdDate = new Date();
		this.id = null;
		this.file = null;
		this.executionManagerFileId = null;
		this.createdByTool = null;
		this.labelPrefix = null;
	}

	/**
	 * Create a new instance of {@link AnalysisOutputFile}.
	 * 
	 * @param file
	 *            the file that this resource owns.
	 * @param labelPrefix
	 *            the label prefix to use for this file.
	 * @param executionManagerFileId
	 *            the identifier for this file in the execution manager that it
	 *            was created by.
	 * @param createdByTool
	 *            the tools that were used to create the file.
	 */
	public AnalysisOutputFile(final Path file, final String labelPrefix, final String executionManagerFileId,
			final ToolExecution createdByTool) {
		this.id = null;
		this.createdDate = new Date();
		this.file = file;
		this.executionManagerFileId = executionManagerFileId;
		this.createdByTool = createdByTool;
		this.labelPrefix = labelPrefix;
	}

	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * This intentionally always returns 0. We're abusing
	 * {@link VersionedFileFields} so that we can get support from
	 * {@link FilesystemSupplementedRepository}, even though
	 * {@link AnalysisOutputFile} is immutable and cannot be versioned.
	 * 
	 * @return *always* {@code 0L} for {@link AnalysisOutputFile}.
	 */
	@Override
	public Long getFileRevisionNumber() {
		return 0L;
	}

	/**
	 * This intentionally does nothing. We're abusing
	 * {@link VersionedFileFields} so that we can get support from
	 * {@link FilesystemSupplementedRepository}, even though
	 * {@link AnalysisOutputFile} is immutable and cannot be versioned.
	 */
	@Override
	public void incrementFileRevisionNumber() {
	}

	@Override
	public String getLabel() {
		return Strings.isNullOrEmpty(labelPrefix) ? file.toFile().getName() : labelPrefix + '-' + file.toFile().getName();
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public Path getFile() {
		return file;
	}

	public String getExecutionManagerFileId() {
		return executionManagerFileId;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public ToolExecution getCreatedByTool() {
		return createdByTool;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(file, executionManagerFileId);
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
			return Objects.equals(file, a.file) && Objects.equals(executionManagerFileId, a.executionManagerFileId);
		}

		return false;
	}

	/**
	 * Read the bytes for an image output file
	 *
	 * @return the bytes for the file
	 */
	public byte[] getBytesForFile() {
		byte[] bytes = new byte[0];
		try{
			bytes = Files.readAllBytes(getFile());
		} catch (IOException e) {
			logger.error("Unable to read file.", e);
		} finally {
			return bytes;
		}
	}
}
