package ca.corefacility.bioinformatics.irida.model.assembly;

import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;

@Entity
@Table(name = "uploaded_assembly")
@EntityListeners(AuditingEntityListener.class)
public class UploadedAssembly extends GenomeAssembly implements VersionedFileFields<Long> {

	@NotNull
	@Column(name = "file_path", unique = true)
	private Path file;

	Long fileRevisionNumber;

	protected UploadedAssembly(){
		super();
	}

	public UploadedAssembly(Path file) {
		super();
		this.fileRevisionNumber=0L;
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
