package ca.corefacility.bioinformatics.irida.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

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

	@NotNull
	@Transient
	private Path file;

	private Boolean enabled = true;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@Column(name = "filePath")
	private String stringPath;

	/* statistics computed by fastqc */
	private String fileType;
	private String encoding;
	private Integer totalSequences;
	private Integer filteredSequences;
	private Integer minLength;
	private Integer maxLength;
	private Short gcContent;

	public SequenceFile() {
		createdDate = new Date();
		modifiedDate = createdDate;
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
			return Objects.equals(file, sampleFile.file) && Objects.equals(createdDate, sampleFile.createdDate)
					&& Objects.equals(modifiedDate, sampleFile.modifiedDate);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(file, createdDate, modifiedDate);
	}

	@Override
	public int compareTo(SequenceFile other) {
		return modifiedDate.compareTo(other.modifiedDate);
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
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

	public void setStringPath() {
		stringPath = file.toFile().toString();
	}

	public void setRealPath() {
		file = Paths.get(stringPath);
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
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean valid) {
		this.enabled = valid;
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
}
