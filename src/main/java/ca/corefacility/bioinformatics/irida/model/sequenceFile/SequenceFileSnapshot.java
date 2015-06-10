package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.exceptions.RemoteFileNotCachedException;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Remote representation of an {@link IridaSequenceFile}. This object will point
 * to both a URI on a remote API and a local Path.
 */
@Entity
@Table(name = "remote_sequence_file")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class SequenceFileSnapshot implements IridaSequenceFile, IridaThing, VersionedFileFields<Long> {

	private static final Logger logger = LoggerFactory.getLogger(SequenceFileSnapshot.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(name = "remote_uri")
	private String remoteURI;

	@Column(name = "file_path", unique = true, nullable = true)
	private Path file;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, name = "created_date")
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date")
	private Date modifiedDate;

	// Key/value map of additional properties you could set on a sequence file.
	// This may contain optional sequencer specific properties.
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "property_key", nullable = false)
	@Column(name = "property_value", nullable = false)
	@CollectionTable(name = "remote_sequence_file_properties", joinColumns = @JoinColumn(name = "sequence_file_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"sequence_file_id", "property_key" }, name = "UK_SEQUENCE_FILE_PROPERTY_KEY"))
	private Map<String, String> optionalProperties;

	@NotNull
	@Column(name = "file_revision_number")
	private Long fileRevisionNumber;

	private SequenceFileSnapshot() {
		createdDate = new Date();
		fileRevisionNumber = 0L;
	}

	/**
	 * Create a new {@link SequenceFileSnapshot} based on an existing remote
	 * {@link SequenceFile}. This will copy the properties of the file along
	 * with its {@code self} rel.
	 * 
	 * @param base
	 *            The {@link SequenceFile} to base this copy on
	 */
	public SequenceFileSnapshot(SequenceFile base) {
		this();

		remoteURI = base.getSelfHref();
		optionalProperties = base.getOptionalProperties();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Path getFile() {
		if (file == null) {
			throw new RemoteFileNotCachedException(
					"The remote sequence files are not yet available as they have not been cached");
		}
		return file;
	}

	public void setFile(Path file) {
		this.file = file;
	}

	@Override
	public Map<String, String> getOptionalProperties() {
		return optionalProperties;
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
		if (file == null) {
			return "Unmirrored Sequence File";
		} else {
			return file.getFileName().toString();
		}

	}

	@Override
	public Long getFileRevisionNumber() {
		return fileRevisionNumber;
	}

	@Override
	public void incrementFileRevisionNumber() {
		fileRevisionNumber++;
	}

	public String getRemoteURI() {
		return remoteURI;
	}

	/**
	 * Return whether the file has been mirrored locally
	 * 
	 * @return boolean whether the file has been mirrored
	 */
	public boolean isMirrored() {
		if (file == null) {
			return false;
		}
		return true;
	}

	@JsonIgnore
	public String getFileSize() {
		String size = "N/A";
		try {
			size = IridaSequenceFile.humanReadableByteCount(Files.size(file), true);
		} catch (IOException e) {
			logger.error("Could not calculate file size: ", e);
		}
		return size;
	}

}
