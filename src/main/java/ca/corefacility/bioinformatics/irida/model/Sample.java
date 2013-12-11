package ca.corefacility.bioinformatics.irida.model;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

/**
 * A biological sample. Each sample may correspond to many files.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "sample")
@Audited
public class Sample implements IridaThing, Comparable<Sample> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Note: The unique constraint makes sense programmatically, however it does
	 * not make sense to have a unique constraint for an external identifier
	 * from the perspective of a user; especially since the external identifier
	 * is provided entirely externally from the system.
	 */
	@NotNull
	@Size(min = 3)
	// @Column(unique = true)
	private String externalSampleId;

	@NotNull
	@Size(min = 3)
	private String sampleName;

	@Lob
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE,mappedBy = "sample")
	private List<ProjectSampleJoin> projects;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE,mappedBy = "sample")
	private List<SampleSequenceFileJoin> sequenceFiles;

	public Sample() {
		createdDate = new Date();
		modifiedDate = createdDate;
	}

	/**
	 * Create a new {@link Sample} with the given name
	 * 
	 * @param sampleName
	 *            The name of the sample
	 */
	public Sample(String sampleName) {
		this.sampleName = sampleName;
	}

	/**
	 * Create a new {@link Sample} with the given name and ID
	 * 
	 * @param name
	 *            The sampleName of the sample
	 * @param sampleId
	 *            The ID of the sample
	 */
	public Sample(String sampleName, String sampleId) {
		this.sampleName = sampleName;
		this.externalSampleId = sampleId;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Sample) {
			Sample sample = (Sample) other;
			return Objects.equals(createdDate, sample.createdDate) && Objects.equals(modifiedDate, sample.modifiedDate)
					&& Objects.equals(sampleName, sample.sampleName) && Objects.equals(id, sample.id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate, sampleName, modifiedDate, id);
	}

	@Override
	public int compareTo(Sample other) {
		return modifiedDate.compareTo(other.modifiedDate);
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public String getExternalSampleId() {
		return externalSampleId;
	}

	public void setExternalSampleId(String sampleId) {
		this.externalSampleId = sampleId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getLabel() {
		return sampleName;
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

	public List<ProjectSampleJoin> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectSampleJoin> projects) {
		this.projects = projects;
	}

	public List<SampleSequenceFileJoin> getSequenceFiles() {
		return sequenceFiles;
	}

	public void setSequenceFiles(List<SampleSequenceFileJoin> sequenceFiles) {
		this.sequenceFiles = sequenceFiles;
	}
}
