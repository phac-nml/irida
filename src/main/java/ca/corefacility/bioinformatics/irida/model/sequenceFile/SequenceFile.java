package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSynchronizable;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl.RelativePathTranslatorListener;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A file that may be stored somewhere on the file system and belongs to a
 * particular {@link Sample}.
 */
@Entity
@Table(name = "sequence_file")
@Audited
@EntityListeners({ AuditingEntityListener.class, RelativePathTranslatorListener.class })
public class SequenceFile extends IridaRepresentationModel
		implements MutableIridaThing, Comparable<SequenceFile>, VersionedFileFields<Long>, IridaSequenceFile,
		RemoteSynchronizable {

	private static final Logger logger = LoggerFactory.getLogger(SequenceFile.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "{sequencefile.file.notnull}")
	@Column(name = "file_path", unique = true)
	@Schema(implementation = String.class)
	private Path file;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, name = "created_date", updatable = false)
	private Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date")
	private Date modifiedDate;

	@Column(name = "upload_sha256")
	private String uploadSha256;

	@Column(name = "file_revision_number")
	private Long fileRevisionNumber; // the filesystem file revision number

	// Key/value map of additional properties you could set on a sequence file.
	// This may contain optional sequencer specific properties.
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "property_key", nullable = false)
	@Column(name = "property_value", nullable = false)
	@CollectionTable(name = "sequence_file_properties", joinColumns = @JoinColumn(name = "sequence_file_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"sequence_file_id", "property_key" }, name = "UK_SEQUENCE_FILE_PROPERTY_KEY"))
	private Map<String, String> optionalProperties;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@NotAudited
	@JoinColumn(name = "fastqc_analysis_id")
	private AnalysisFastQC fastqcAnalysis;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "remote_status")
	private RemoteStatus remoteStatus;


	public SequenceFile() {
		createdDate = new Date();
		fileRevisionNumber = 0L;
		optionalProperties = new HashMap<>();
	}

	/**
	 * Get the implementation-specific file label.
	 *
	 * @return the file label.
	 */
	@Override
	public String getLabel() {
		return file.getFileName().toString();
	}

	/**
	 * Create a new {@link SequenceFile} with the given file Path
	 *
	 * @param sampleFile The Path to a {@link SequenceFile}
	 */
	public SequenceFile(Path sampleFile) {
		this();
		this.file = sampleFile;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SequenceFile) {
			SequenceFile sampleFile = (SequenceFile) other;
			return Objects.equals(file, sampleFile.file);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(file);
	}

	@Override
	public int compareTo(SequenceFile other) {
		return modifiedDate.compareTo(other.modifiedDate);
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Path getFile() {
		return file;
	}

	public void setFile(Path file) {
		this.file = file;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@JsonIgnore
	public Long getFileRevisionNumber() {
		return fileRevisionNumber;
	}

	/**
	 * Add one optional property to the map of properties
	 *
	 * @param key   The key of the property to add
	 * @param value The value of the property to add
	 */
	@JsonAnySetter
	public void addOptionalProperty(String key, String value) {
		optionalProperties.put(key, value);
	}

	/**
	 * Get the Map of optional properties
	 *
	 * @return A {@code Map<String,String>} of all the optional propertie
	 */
	@JsonAnyGetter
	public Map<String, String> getOptionalProperties() {
		return optionalProperties;
	}

	/**
	 * Get an individual optional property
	 *
	 * @param key The key of the property to read
	 * @return A String of the property's value
	 */
	public String getOptionalProperty(String key) {
		return optionalProperties.get(key);
	}


	/**
	 * Set the Map of optional properties
	 *
	 * @param optionalProperties A {@code Map<String,String>} of all the optional properties
	 *                           for this object
	 */
	public void setOptionalProperties(Map<String, String> optionalProperties) {
		this.optionalProperties = optionalProperties;
	}

	@Override
	public void incrementFileRevisionNumber() {
		this.fileRevisionNumber++;
	}

	@Override
	public String getFileName() {
		return getFile().getFileName()
				.toString();
	}

	@JsonIgnore
	public AnalysisFastQC getFastQCAnalysis() {
		return this.fastqcAnalysis;
	}

	/**
	 * Set the {@link AnalysisFastQC} for this {@link SequenceFile}.
	 *
	 * @param fastqcAnalysis the analysis to set.
	 * @throws AnalysisAlreadySetException if the analysis has already been set for this
	 *                                     {@link SequenceFile}.
	 */
	@JsonIgnore
	public void setFastQCAnalysis(final AnalysisFastQC fastqcAnalysis) throws AnalysisAlreadySetException {
		if (this.fastqcAnalysis == null) {
			this.fastqcAnalysis = fastqcAnalysis;
		} else {
			throw new AnalysisAlreadySetException(
					"The FastQC Analysis can only be applied to a sequence file one time.");
		}
	}

	@Override
	public RemoteStatus getRemoteStatus() {
		return remoteStatus;
	}

	@Override
	public void setRemoteStatus(RemoteStatus remoteStatus) {
		this.remoteStatus = remoteStatus;
	}

	/**
	 * Get the sha256 checksum for a file when it was uploaded. NOTE: This
	 * checksum may not be the same as the sha256 for the current file. The
	 * checksum from the originally uploaded file is saved so an uploader can
	 * verify its file upload.
	 *
	 * @return the string sha256 for the uploaded file.
	 */
	public String getUploadSha256() {
		return uploadSha256;
	}

	public void setUploadSha256(String uploadSha256) {
		this.uploadSha256 = uploadSha256;
	}

	/**
	 * Gets the sequence file size.
	 *
	 * @return The sequence file size.
	 */
	@JsonIgnore
	public String getFileSize() {
		return IridaFiles.getFileSize(file);
	}

	/**
	 * Checks if a file is gzipped.
	 *
	 * @return boolean if file is gzipped or not.
	 * @throws IOException if file cannot be read
	 */
	@JsonIgnore
	public boolean isGzipped() throws IOException {
		return IridaFiles.isGzipped(file);
	}

	/**
	 * Gets sequence file input stream
	 *
	 * @return returns input stream.
	 */
	@JsonIgnore
	public InputStream getFileInputStream() {
		return IridaFiles.getFileInputStream(file);
	}

	/**
	 * Get file size in bytes
	 *
	 * @return if file exists or not
	 */
	public Long getFileSizeBytes() {
		return IridaFiles.getFileSizeBytes(getFile());
	}
}
