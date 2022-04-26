package ca.corefacility.bioinformatics.irida.model.assembly;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "genomeAssembly")
	private List<SampleGenomeAssemblyJoin> sampleGenomeAssemblies;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, mappedBy = "inputAssemblies")
	private List<AnalysisSubmission> analysisSubmissions;

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
	 *
	 * @return file size
	 * @throws IOException if the file cannot be read
	 */
	@JsonIgnore
	public long getFileSizeBytes() throws IOException {
		return Files.size(getFile());
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
	 * Get human-readable file size.
	 *
	 * @return A human-readable file size.
	 */
	@JsonIgnore
	public String getFileSize() {
		String size = "N/A";
		try {
			size = IridaSequenceFile.humanReadableByteCount(getFileSizeBytes(), true);
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
	@Schema(implementation = String.class)
	public abstract Path getFile();

	@Override
	public String getFileName() {
		return getFile().getFileName().toString();
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
}