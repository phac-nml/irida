package ca.corefacility.bioinformatics.irida.model.project;

import java.io.InputStream;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl.RelativePathTranslatorListener;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A reference file to be associated with a {@link Project}.
 * 
 *
 */
@Entity
@Table(name = "reference_file")
@Audited
@EntityListeners({AuditingEntityListener.class, RelativePathTranslatorListener.class})
public class ReferenceFile implements VersionedFileFields<Long>, MutableIridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "filePath", unique = true)
	@NotNull(message = "{reference.file.file.notnull}")
	private Path file;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	private Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "referenceFile")
	private ProjectReferenceFileJoin project;

	private Long fileRevisionNumber; // the filesystem file revision number

	private Long fileLength;

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof ReferenceFile) {
			return Objects.equals(file, ((ReferenceFile) o).file);
		}

		return false;
	}

	public ReferenceFile() {
		this.createdDate = new Date();
		this.fileRevisionNumber = 0L;
	}

	public ReferenceFile(Path file) {
		this();
		this.file = file;
	}

	@Override
	public String getLabel() {
		return file.getFileName().toString();
	}

	@Override
	public Long getId() {
		return this.id;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;
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

	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

	@Override
	public void incrementFileRevisionNumber() {
		this.fileRevisionNumber++;
	}

	@Override
	public Long getFileRevisionNumber() {
		return fileRevisionNumber;
	}

	public Long getFileLength() {
		return fileLength;
	}

	public void setFileLength(Long fileLength) {
		this.fileLength = fileLength;
	}

	@JsonIgnore
	public String getFileSize() {
		return IridaFiles.getFileSize(file);
	}


	/**
	 * Gets reference file input stream
	 *
	 * @return returns input stream.
	 */
	@JsonIgnore
	public InputStream getFileInputStream() {
		return IridaFiles.getFileInputStream(file);
	}
}
