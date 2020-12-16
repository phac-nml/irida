package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import java.nio.file.Path;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * A temporary file which required by an {@link AnalysisSubmission} when
 * the storage type is an object store.
 */
@Entity
@Table(name = "analysis_submission_temp_files")
public class AnalysisSubmissionTempFile implements IridaThing {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id;

	@NotNull
	@Column(name = "analysis_submission_id")
	private final Long analysisSubmissionId;

	@NotNull
	@Column(name = "temp_file_path")
	private final Path filePath;

	@NotNull
	@Column(name = "temp_file_directory_path")
	private final Path fileDirectoryPath;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private final Date createdDate;

	/**
	 * for hibernate
	 */
	@SuppressWarnings("unused")
	private AnalysisSubmissionTempFile() {
		this.analysisSubmissionId = null;
		this.filePath = null;
		this.fileDirectoryPath = null;
		this.id = null;
		this.createdDate = null;
	}

	/**
	 * Create a new {@link AnalysisSubmissionTempFile} with the given file
	 * analysis submission id, file path, and directory path.
	 *
	 * @param analysisSubmissionId The id of the {@link AnalysisSubmission}
	 * @param filePath The path to the temporary file
	 * @param fileDirectoryPath The path to the temporary file directory
	 */
	public AnalysisSubmissionTempFile(Long analysisSubmissionId, Path filePath, Path fileDirectoryPath) {
		this.analysisSubmissionId = analysisSubmissionId;
		this.filePath = filePath;
		this.fileDirectoryPath = fileDirectoryPath;
		this.id = null;
		this.createdDate = new Date();
	}

	/**
	 * Get the implementation-specific file label.
	 *
	 * @return the file label.
	 */
	@Override
	public String getLabel() {
		return filePath.getFileName().toString();
	}

	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public Path getFilePath() {
		return filePath;
	}

	public Path getFileDirectoryPath() {
		return fileDirectoryPath;
	}
	
}
