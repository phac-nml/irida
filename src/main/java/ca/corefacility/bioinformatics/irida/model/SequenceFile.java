package ca.corefacility.bioinformatics.irida.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileOverrepresentedSequenceJoin;

/**
 * A file that may be stored somewhere on the file system and belongs to a
 * particular {@link Sample}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "sequence_file")
@Audited
public class SequenceFile implements IridaThing, Comparable<SequenceFile> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "{sequencefile.file.notnull}")
	@Transient
	private Path file;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@Column(name = "filePath", unique = true)
	private String stringPath;

	/* statistics computed by fastqc */
	private String fileType;
	private String encoding;
	private Integer totalSequences;
	private Integer filteredSequences;
	private Long totalBases;
	private Integer minLength;
	private Integer maxLength;
	private Short gcContent;
	@Lob
	private byte[] perBaseQualityScoreChart;
	@Lob
	private byte[] perSequenceQualityScoreChart;
	@Lob
	private byte[] duplicationLevelChart;
	private String samplePlate;
	private String sampleWell;
	private String i7IndexId;
	private String i7Index;
	private String i5IndexId;
	private String i5Index;
	private Long fileRevisionNumber; // the filesystem file revision number

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "miseqRun_id")
	private MiseqRun miseqRun;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sequenceFile")
	private List<SampleSequenceFileJoin> samples;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sequenceFile")
	private List<SequenceFileOverrepresentedSequenceJoin> overrepresentedSequences;

	public SequenceFile() {
		createdDate = new Date();
		modifiedDate = createdDate;
		fileRevisionNumber = 1L;
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
		setStringPath();

	}

	@PostLoad
	public void postLoad() {
		setRealPath();
	}

	@PrePersist
	@PreUpdate
	public void prePersist() {
		setStringPath();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SequenceFile) {
			SequenceFile sampleFile = (SequenceFile) other;
			return Objects.equals(stringPath, sampleFile.stringPath);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(stringPath);
	}

	@Override
	public int compareTo(SequenceFile other) {
		return modifiedDate.compareTo(other.modifiedDate);
	}

	/**
	 * Box and whisker plot showing per-base quality scores as a PNG-formatted
	 * image in a byte array.
	 * 
	 * @return
	 */
	public byte[] getPerBaseQualityScoreChart() {
		return perBaseQualityScoreChart;
	}

	public void setPerBaseQualityScoreChart(byte[] perBaseQualityScoreChart) {
		this.perBaseQualityScoreChart = perBaseQualityScoreChart;
	}

	/**
	 * Line chartshowing per-sequence quality scores as a PNG-formatted image in
	 * a byte array.
	 * 
	 * @return
	 */
	public byte[] getPerSequenceQualityScoreChart() {
		return perSequenceQualityScoreChart;
	}

	public void setPerSequenceQualityScoreChart(byte[] perSequenceQualityScoreChart) {
		this.perSequenceQualityScoreChart = perSequenceQualityScoreChart;
	}

	/**
	 * Line chartshowing duplication-level as a PNG-formatted image in a byte
	 * array.
	 * 
	 * @return
	 */
	public byte[] getDuplicationLevelChart() {
		return duplicationLevelChart;
	}

	public void setDuplicationLevelChart(byte[] duplicationLevelChart) {
		this.duplicationLevelChart = duplicationLevelChart;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getStringPath() {
		return stringPath;
	}

	public void setStringPath(String stringPath) {
		this.stringPath = stringPath;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Integer getTotalSequences() {
		return totalSequences;
	}

	public void setTotalSequences(Integer totalSequences) {
		this.totalSequences = totalSequences;
	}

	public Integer getFilteredSequences() {
		return filteredSequences;
	}

	public void setFilteredSequences(Integer filteredSequences) {
		this.filteredSequences = filteredSequences;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public Short getGcContent() {
		return gcContent;
	}

	public void setGcContent(Short gcContent) {
		this.gcContent = gcContent;
	}

	public Long getTotalBases() {
		return totalBases;
	}

	public void setTotalBases(Long totalBases) {
		this.totalBases = totalBases;
	}

	public void setStringPath() {
		if (file != null) {
			stringPath = file.toFile().toString();
		}
	}

	public void setRealPath() {
		if (stringPath != null) {
			file = Paths.get(stringPath);
		}
	}

	public Path getFile() {
		return file;
	}

	public void setFile(Path file) {
		this.file = file;
		setStringPath();
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
	public Date getTimestamp() {
		return createdDate;
	}

	@Override
	public void setTimestamp(Date date) {
		this.createdDate = date;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getSamplePlate() {
		return samplePlate;
	}

	public void setSamplePlate(String samplePlate) {
		this.samplePlate = samplePlate;
	}

	public String getSampleWell() {
		return sampleWell;
	}

	public void setSampleWell(String sampleWell) {
		this.sampleWell = sampleWell;
	}

	public String getI7IndexId() {
		return i7IndexId;
	}

	public void setI7IndexId(String i7IndexId) {
		this.i7IndexId = i7IndexId;
	}

	public String getI7Index() {
		return i7Index;
	}

	public void setI7Index(String i7Index) {
		this.i7Index = i7Index;
	}

	public String getI5IndexId() {
		return i5IndexId;
	}

	public void setI5IndexId(String i5IndexId) {
		this.i5IndexId = i5IndexId;
	}

	public String getI5Index() {
		return i5Index;
	}

	public void setI5Index(String i5Index) {
		this.i5Index = i5Index;
	}

	public Long getFileRevisionNumber() {
		return fileRevisionNumber;
	}

	public void setFileRevisionNumber(Long fileRevisionNumber) {
		this.fileRevisionNumber = fileRevisionNumber;
	}

	public MiseqRun getMiseqRun() {
		return miseqRun;
	}

	public void setMiseqRun(MiseqRun miseqRun) {
		this.miseqRun = miseqRun;
	}

	public List<SampleSequenceFileJoin> getSamples() {
		return samples;
	}

	public void setSamples(List<SampleSequenceFileJoin> samples) {
		this.samples = samples;
	}

	public List<SequenceFileOverrepresentedSequenceJoin> getOverrepresentedSequences() {
		return overrepresentedSequences;
	}

	public void setOverrepresentedSequences(List<SequenceFileOverrepresentedSequenceJoin> overrepresentedSequences) {
		this.overrepresentedSequences = overrepresentedSequences;
	}
}
