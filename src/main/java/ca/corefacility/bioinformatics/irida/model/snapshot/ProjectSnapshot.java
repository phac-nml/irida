package ca.corefacility.bioinformatics.irida.model.snapshot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.irida.IridaProject;

/**
 * Snapshot taken of an {@link IridaProject} object
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "project_snapshot")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@EntityListeners(AuditingEntityListener.class)
public class ProjectSnapshot implements IridaProject {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "snapshot_id")
	private Long snapshotId;

	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Lob
	@Column(name = "project_description")
	private String projectDescription;

	@Column(name = "remote_url")
	private String remoteURL;

	@Column(name = "organism")
	private String organism;

	public ProjectSnapshot(IridaProject project) {
		this.id = project.getId();
		this.name = project.getName();
		this.projectDescription = project.getProjectDescription();
		this.remoteURL = project.getRemoteURL();
		this.organism = project.getOrganism();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getProjectDescription() {
		return projectDescription;
	}

	@Override
	public String getRemoteURL() {
		return remoteURL;
	}

	@Override
	public String getOrganism() {
		return organism;
	}

}
