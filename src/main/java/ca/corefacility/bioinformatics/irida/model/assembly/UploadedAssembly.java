package ca.corefacility.bioinformatics.irida.model.assembly;

import java.nio.file.Path;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;

/**
 * A {@link GenomeAssembly} implementation that was uploaded by a user or
 * service
 */
@Entity
@Table(name = "uploaded_assembly")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class UploadedAssembly extends GenomeAssembly {

	@NotNull
	@Column(name = "file_path", unique = true)
	private Path file;

	@Column(name = "file_revision_number")
	Long fileRevisionNumber;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "remote_status")
	private RemoteStatus remoteStatus;

	// default constructor for hibernate
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

	public void setFile(Path file) {
		this.file = file;
	}

	@Override
	public Long getFileRevisionNumber() {
		return fileRevisionNumber;
	}

	@Override
	public void incrementFileRevisionNumber() {
		fileRevisionNumber++;
	}

	@Override
	public RemoteStatus getRemoteStatus() {
		return remoteStatus;
	}

	@Override
	public void setRemoteStatus(RemoteStatus remoteStatus) {
		this.remoteStatus = remoteStatus;
	}
}
