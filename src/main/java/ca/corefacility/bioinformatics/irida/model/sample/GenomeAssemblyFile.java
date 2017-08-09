package ca.corefacility.bioinformatics.irida.model.sample;

import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * An assembled genome derived from an uploaded file.
 */

//@Entity
//@Table(name = "genome_assembly_file")
//@Audited
//@EntityListeners(AuditingEntityListener.class)
public class GenomeAssemblyFile extends GenomeAssembly {

	@NotNull
	@Column(name = "file_path", unique = true)
	private Path file;

	public GenomeAssemblyFile() {
		super(new Date());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path getFile() {
		return file;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), file);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenomeAssemblyFile other = (GenomeAssemblyFile) obj;
		return super.equals(obj) && Objects.equals(this.file, other.file);
	}
}
