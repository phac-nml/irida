package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.CascadeType;
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
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;

/**
 * A file that may be stored somewhere on the file system and belongs to a
 * particular {@link Sample}.
 * 
 */
@Entity
@Table(name = "sequence_file")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class SequenceFile implements IridaThing, Comparable<SequenceFile>, VersionedFileFields<Long>, IridaSequenceFile {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "file_path", unique = true)
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

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sequencing_run_id")
	private SequencingRun sequencingRun;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sequenceFile")
	private List<SampleSequenceFileJoin> samples;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@NotAudited
	@JoinColumn(name = "fastqc_analysis_id")
	private AnalysisFastQC fastqcAnalysis;

	public SequenceFile() {
		createdDate = new Date();
		fileRevisionNumber = 0L;
		optionalProperties = new HashMap<>();
	}

	/**
	 * Create a new {@link SequenceFile} with the given file Path
	 * 
	 * @param sampleFile
	 *            The Path to a {@link SequenceFile}
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
	public String getLabel() {
		return file.getFileName().toString();
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

	public Long getFileRevisionNumber() {
		return fileRevisionNumber;
	}

	public SequencingRun getSequencingRun() {
		return sequencingRun;
	}

	public void setSequencingRun(SequencingRun sequencingRun) {
		this.sequencingRun = sequencingRun;
	}

	/**
	 * Add one optional property to the map of properties
	 * 
	 * @param key
	 *            The key of the property to add
	 * @param value
	 *            The value of the property to add
	 */
	public void addOptionalProperty(String key, String value) {
		optionalProperties.put(key, value);
	}

	/**
	 * Get the Map of optional properties
	 * 
	 * @return A {@code Map<String,String>} of all the optional propertie
	 */
	public Map<String, String> getOptionalProperties() {
		return optionalProperties;
	}

	/**
	 * Get an individual optional property
	 * 
	 * @param key
	 *            The key of the property to read
	 * @return A String of the property's value
	 */
	public String getOptionalProperty(String key) {
		return optionalProperties.get(key);
	}

	/**
	 * Set the Map of optional properties
	 * 
	 * @param optionalProperties
	 *            A {@code Map<String,String>} of all the optional properties for this
	 *            object
	 */
	public void setOptionalProperties(Map<String, String> optionalProperties) {
		this.optionalProperties = optionalProperties;
	}

	@Override
	public void incrementFileRevisionNumber() {
		this.fileRevisionNumber++;
	}
	
	public AnalysisFastQC getFastQCAnalysis() {
		return this.fastqcAnalysis;
	}
	
	/**
	 * Set the {@link AnalysisFastQC} for this {@link SequenceFile}.
	 * 
	 * @param fastqcAnalysis
	 *            the analysis to set.
	 * @throws AnalysisAlreadySetException
	 *             if the analysis has already been set for this
	 *             {@link SequenceFile}.
	 */
	public void setFastQCAnalysis(final AnalysisFastQC fastqcAnalysis) throws AnalysisAlreadySetException {
		if (this.fastqcAnalysis == null) {
			this.fastqcAnalysis = fastqcAnalysis;
		} else {
			throw new AnalysisAlreadySetException("The FastQC Analysis can only be applied to a sequence file one time.");
		}
	}
}
