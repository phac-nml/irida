package ca.corefacility.bioinformatics.irida.model.remote;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * RelatedProjectJoin for remote projects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Entity
@Audited
@Table(name = "remote_related_project", uniqueConstraints = @UniqueConstraint(columnNames = { "project_id",
		"remote_api_id", "remoteProjectID" }, name = "UK_REMOTE_RELATED_PROJECT"))
@EntityListeners(AuditingEntityListener.class)
public class RemoteRelatedProject implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id")
	@NotNull
	private Project localProject;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "remote_api_id")
	@NotNull
	private RemoteAPI remoteAPI;

	@NotNull
	private Long remoteProjectID;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	public RemoteRelatedProject() {
		createdDate = new Date();
	}

	public RemoteRelatedProject(Project localProject, RemoteAPI remoteAPI, Long remoteProjectID) {
		this.localProject = localProject;
		this.remoteAPI = remoteAPI;
		this.remoteProjectID = remoteProjectID;
	}

	/**
	 * @return the localProject
	 */
	public Project getLocalProject() {
		return localProject;
	}

	/**
	 * @param localProject
	 *            the localProject to set
	 */
	public void setLocalProject(Project localProject) {
		this.localProject = localProject;
	}

	/**
	 * @return the remoteAPI
	 */
	public RemoteAPI getRemoteAPI() {
		return remoteAPI;
	}

	/**
	 * @param remoteAPI
	 *            the remoteAPI to set
	 */
	public void setRemoteAPI(RemoteAPI remoteAPI) {
		this.remoteAPI = remoteAPI;
	}

	/**
	 * @return the remoteProjectID
	 */
	public Long getRemoteProjectID() {
		return remoteProjectID;
	}

	/**
	 * @param remoteProjectID
	 *            the remoteProjectID to set
	 */
	public void setRemoteProjectID(Long remoteProjectID) {
		this.remoteProjectID = remoteProjectID;
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

	@Override
	public String getLabel() {
		return remoteAPI.getName() + " - " + remoteProjectID.toString();
	}

	@Override
	public Long getId() {
		return id;
	}

}
