package ca.corefacility.bioinformatics.irida.model.assembly;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Defines a genome assembly which can be associated with a sample.
 */
@Entity
@Table(name = "genome_assembly")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public abstract class GenomeAssembly extends IridaRepresentationModel
		implements IridaThing, IridaSequenceFile, VersionedFileFields<Long> {

	private static final Logger logger = LoggerFactory.getLogger(GenomeAssembly.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
	private Date createdDate;

	@Column(name = "storage_type")
	private String storageType;

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
		this.storageType = IridaFiles.getStorageType();
	}

	@Override
	public String getLabel() {
		return getFile().getFileName()
				.toString();
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
	 * Get the size of the genome assembly files in bytes
	 *
	 * @return file size
	 * @throws IOException if the file cannot be read
	 */
	@JsonIgnore
	public Long getFileSizeBytes() throws IOException {
		return IridaFiles.getFileSizeBytes(getFile());
	}

	/**
	 * Add a sample to this assembly
	 *
	 * @param join the {@link SampleGenomeAssemblyJoin} to add
	 */
	public void addSampleGenomeAssemblyJoin(SampleGenomeAssemblyJoin join) {
		if (!sampleGenomeAssemblies.contains(join)) {
			sampleGenomeAssemblies.add(join);
		}
	}

	/**
	 * Gets the assembly file size as a human readable string.
	 *
	 * @return The assembly file size.
	 */
	public String getFileSize() {
		return IridaFiles.getFileSize(getFile());
	}

	/**
	 * Gets the assembly file.
	 *
	 * @return The assembly file.
	 */
	@Schema(implementation = String.class)
	public abstract Path getFile();

	@Override
	public String getFileName() {
		return getFile().getFileName()
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, createdDate);
	}

	public void setId(Long id) {
		this.id = id;
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

	/**
	 * Gets assembly file input stream
	 *
	 * @return returns input stream.
	 */
	@JsonIgnore
	public InputStream getFileInputStream() {
		return IridaFiles.getFileInputStream(getFile());
	}

	public String getStorageType(){
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}
}