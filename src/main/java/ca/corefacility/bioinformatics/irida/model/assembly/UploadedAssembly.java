package ca.corefacility.bioinformatics.irida.model.assembly;

import java.nio.file.Path;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;

/**
 * A {@link GenomeAssembly} implementation that was uploaded by a user or service
 */
@Entity
@Table(name = "uploaded_assembly")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class UploadedAssembly extends GenomeAssembly implements VersionedFileFields<Long> {

	@NotNull
	@Column(name = "file_path", unique = true)
	private Path file;

	@Column(name = "file_revision_number")
	Long fileRevisionNumber;

	//default constructor for hibernate
	protected UploadedAssembly() {
		super();
	}

	public UploadedAssembly(Path file) {
		super();
		this.fileRevisionNumber = 0L;
		this.file = file;
	}

	@Override
	public Path getFile() {
		return file;
	}

	@Override
	public Long getFileRevisionNumber() {
		return fileRevisionNumber;
	}

	@Override
	public void incrementFileRevisionNumber() {
		fileRevisionNumber++;
	}
}
