package ca.corefacility.bioinformatics.irida.model.project;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.irida.IridaProject;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.model.user.Organization;

/**
 * A project object.
 * 
 */
@Entity
@Table(name = "project")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Project extends IridaResourceSupport implements IridaThing, IridaProject, Comparable<Project> {

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

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "project")
	private List<ProjectUserJoin> users;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "project")
	private List<ProjectSampleJoin> samples;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "subject")
	private List<RelatedProjectJoin> relatedProjects;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "relatedProject")
	private List<RelatedProjectJoin> projectsRelatedTo;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "project")
	private List<ProjectReferenceFileJoin> referenceFiles;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "localProject")
	private List<RemoteRelatedProject> remoteRelatedProjects;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private Organization organization;

	private String organism;

	public Project() {
		createdDate = new Date();
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

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Project) {
			Project p = (Project) other;
			return Objects.equals(createdDate, p.createdDate) && Objects.equals(name, p.name)
					&& Objects.equals(organization, p.organization);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate, modifiedDate, name, organization);
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

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}
}
