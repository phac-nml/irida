package ca.corefacility.bioinformatics.irida.model.snapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

/**
 * Collection of {@link ProjectSnapshot}s, {@link SampleSnapshot}s, and
 * {@link SequenceFileSnapshot}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "snapshot")
public class Snapshot implements IridaThing {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<ProjectSnapshot> projects;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<SequenceFileSnapshot> sequenceFiles;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<SampleSnapshot> samples;

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

}
