package ca.corefacility.bioinformatics.irida.model.assembly;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;

/**
 * Defines a genome assembly which can be associated with a sample.
 */
@Entity
@Table(name = "genome_assembly")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class GenomeAssembly extends IridaResourceSupport implements IridaThing {

	private static final Logger logger = LoggerFactory.getLogger(GenomeAssembly.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
	private Date createdDate;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "genomeAssembly")
	private List<SampleGenomeAssemblyJoin> sampleGenomeAssemblies;

	protected GenomeAssembly() {
		this.id = null;
		this.createdDate = null;
		this.sampleGenomeAssemblies = null;
	}

	public GenomeAssembly(Date createdDate) {
		this.createdDate = createdDate;
		this.sampleGenomeAssemblies = Lists.newArrayList();
	}

	@Override
	public String getLabel() {
		return getFile().getFileName().toString();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Get the size of the genome assembly files
	 * @return file size
	 * @throws IOException if the file cannot be read
	 */
	public long getFileSize() throws IOException {
		return Files.size(getFile());
	}

	/**
	 * Add a sample to this assembly
	 * @param join the {@link SampleGenomeAssemblyJoin} to add
	 */
	public void addSampleGenomeAssemblyJoin(SampleGenomeAssemblyJoin join) {
		if (!sampleGenomeAssemblies.contains(join)) {
			sampleGenomeAssemblies.add(join);
		}
	}

	/**
	 * Get human-readable file size.
	 * 
	 * @return A human-readable file size.
	 */
	public String getReadableFileSize()  {
		String size = "N/A";
		try {
			size = IridaSequenceFile.humanReadableByteCount(getFileSize(), true);
		} catch (NoSuchFileException e) {
			logger.error("Could not find file " + getFile());
		} catch (IOException e) {
			logger.error("Could not calculate file size: ", e);
		}
		return size;
	}

	/**
	 * Gets the assembly file.
	 * 
	 * @return The assembly file.
	 */
	public abstract Path getFile();

	@Override
	public int hashCode() {
		return Objects.hash(id, createdDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenomeAssembly other = (GenomeAssembly) obj;
		return Objects.equals(this.id, other.id) && Objects.equals(this.createdDate, other.createdDate);
	}
}