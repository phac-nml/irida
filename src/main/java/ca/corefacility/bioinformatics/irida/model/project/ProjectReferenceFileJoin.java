package ca.corefacility.bioinformatics.irida.model.project;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.joins.Join;

/**
 * {@link Join}-type relating {@link Project} to {@link ReferenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "project_referencefile")
@Audited
public class ProjectReferenceFileJoin implements Join<Project, ReferenceFile> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "referencefile_id")
	private ReferenceFile referenceFile;

	public ProjectReferenceFileJoin() {
		this.createdDate = new Date();
	}

	public ProjectReferenceFileJoin(Project project, ReferenceFile referenceFile) {
		this();
		this.project = project;
		this.referenceFile = referenceFile;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public Project getSubject() {
		return this.project;
	}

	@Override
	public void setSubject(Project subject) {
		this.project = subject;
	}

	@Override
	public ReferenceFile getObject() {
		return this.referenceFile;
	}

	@Override
	public void setObject(ReferenceFile object) {
		this.referenceFile = object;
	}

	@Override
	public Date getTimestamp() {
		return this.createdDate;
	}

}
