package ca.corefacility.bioinformatics.irida.model.snapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Collection of {@link ProjectSnapshot}s, {@link SampleSnapshot}s, and
 * {@link SequenceFileSnapshot}s
 * 
 *
 */
@Entity
@Table(name = "snapshot")
@EntityListeners(AuditingEntityListener.class)
public class Snapshot implements IridaThing {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date")
	private Date modifiedDate;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "snapshot_project_snapshot", joinColumns = @JoinColumn(name = "snapshot_id"), inverseJoinColumns = @JoinColumn(name = "project_snapshot"))
	private List<ProjectSnapshot> projects;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "snapshot_sequence_file_snapshot", joinColumns = @JoinColumn(name = "snapshot_id"), inverseJoinColumns = @JoinColumn(name = "sequence_file_snapshot"))
	private List<SequenceFileSnapshot> sequenceFiles;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "snapshot_sample_snapshot", joinColumns = @JoinColumn(name = "snapshot_id"), inverseJoinColumns = @JoinColumn(name = "sample_snapshot"))
	private List<SampleSnapshot> samples;

	@ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	public Snapshot() {
		createdDate = new Date();
		projects = new ArrayList<>();
		sequenceFiles = new ArrayList<>();
		samples = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public String getLabel() {
		return "AnalysisSnapshot " + createdDate;
	}

	public List<ProjectSnapshot> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectSnapshot> projects) {
		this.projects = projects;
	}

	public void addProject(ProjectSnapshot project) {
		projects.add(project);
	}

	public void setSamples(List<SampleSnapshot> samples) {
		this.samples = samples;
	}

	public List<SampleSnapshot> getSamples() {
		return samples;
	}

	public void addSample(SampleSnapshot sample) {
		samples.add(sample);
	}

	public void setSequenceFiles(List<SequenceFileSnapshot> sequenceFiles) {
		this.sequenceFiles = sequenceFiles;
	}

	public List<SequenceFileSnapshot> getSequenceFiles() {
		return sequenceFiles;
	}

	public void addSequenceFile(SequenceFileSnapshot file) {
		sequenceFiles.add(file);
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

}
