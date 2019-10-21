package ca.corefacility.bioinformatics.irida.model.project;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.irida.IridaProject;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSynchronizable;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A project object.
 * 
 */
@Entity
@Table(name = "project")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Project extends IridaResourceSupport
		implements MutableIridaThing, IridaProject, Comparable<Project>, RemoteSynchronizable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@NotAudited
	private Date modifiedDate;

	@Lob
	private String projectDescription;

	private String remoteURL;

	private String organism;
	
	@Min(1)
	@Column(name = "genome_size", nullable = true)
	private Long genomeSize;
	
	@Min(1)
	@Column(name = "minimum_coverage", nullable = true)
	private Integer minimumCoverage;
	
	@Min(1)
	@Column(name = "maximum_coverage", nullable = true)
	private Integer maximumCoverage;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "remote_status")
	private RemoteStatus remoteStatus;
	
	@Column(name = "sync_frequency")
	@Enumerated(EnumType.STRING)
	private ProjectSyncFrequency syncFrequency;

	@Column(name = "analysis_priority")
	@Enumerated(EnumType.STRING)
	private AnalysisSubmission.Priority analysisPriority;

	/*
	 * This group of properties are here to ensure cascading deletion by JPA when a project is deleted.  They are not used within the class.
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "project")
	private List<ProjectUserJoin> users;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "project")
	private List<UserGroupProjectJoin> groups;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "project")
	private List<ProjectSampleJoin> samples;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "subject")
	private List<RelatedProjectJoin> relatedProjects;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "relatedProject")
	private List<RelatedProjectJoin> projectsRelatedTo;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "project")
	private List<ProjectReferenceFileJoin> referenceFiles;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "project")
	private List<ProjectMetadataTemplateJoin> metadataTemplates;

	@NotAudited
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "project")
	private List<ProjectEvent> events;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project")
	private List<ProjectAnalysisSubmissionJoin> analysisSubmissions;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "submittedProject")
	private List<AnalysisSubmissionTemplate> analysisTemplates;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project")
	private List<NcbiExportSubmission> ncbiSubmissions;
	//End of cascade deletion properties

	public Project() {
		createdDate = new Date();
		analysisPriority = AnalysisSubmission.Priority.LOW;
	}

	/**
	 * Create a new {@link Project} with the given name
	 * 
	 * @param name
	 *            The name of the project
	 */
	public Project(String name) {
		this();
		this.name = name;
	}

	@Override
	public Long getId() {
		return id;
	}
	
	@Override
	public void setId(final Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Project) {
			Project p = (Project) other;
			return Objects.equals(createdDate, p.createdDate) && Objects.equals(modifiedDate, modifiedDate) && Objects
					.equals(name, p.name);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate, modifiedDate, name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Project p) {
		return modifiedDate.compareTo(p.modifiedDate);
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getRemoteURL() {
		return remoteURL;
	}

	public void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	@Override
	public RemoteStatus getRemoteStatus() {
		return remoteStatus;
	}
	
	@Override
	public void setRemoteStatus(RemoteStatus remoteStatus) {
		this.remoteStatus = remoteStatus;
	}
	
	public ProjectSyncFrequency getSyncFrequency() {
		return syncFrequency;
	}

	public void setSyncFrequency(ProjectSyncFrequency syncFrequency) {
		this.syncFrequency = syncFrequency;
	}
	
	public Long getGenomeSize() {
		return genomeSize;
	}
	
	public void setGenomeSize(Long genomeSize) {
		this.genomeSize = genomeSize;
	}
	
	public Integer getMinimumCoverage() {
		return minimumCoverage;
	}
	
	public void setMinimumCoverage(Integer minimumCoverage) {
		this.minimumCoverage = minimumCoverage;
	}
	
	public Integer getMaximumCoverage() {
		return maximumCoverage;
	}
	
	public void setMaximumCoverage(Integer maximumCoverage) {
		this.maximumCoverage = maximumCoverage;
	}

	@JsonIgnore
	public AnalysisSubmission.Priority getAnalysisPriority() {
		return analysisPriority;
	}

	@JsonIgnore
	public void setAnalysisPriority(AnalysisSubmission.Priority analysisPriority) {
		this.analysisPriority = analysisPriority;
	}
}
