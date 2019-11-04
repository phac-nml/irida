package ca.corefacility.bioinformatics.irida.model.project;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.joins.Join;

/**
 * {@link Join}-type relating {@link Project} to {@link ReferenceFile}.
 * 
 *
 */
@Entity
@Table(name = "project_referencefile", uniqueConstraints = @UniqueConstraint(columnNames = { "project_id",
		"reference_file_id" }))
@Audited
@EntityListeners(AuditingEntityListener.class)
public class ProjectReferenceFileJoin implements Join<Project, ReferenceFile> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "reference_file_id")
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
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof ProjectReferenceFileJoin) {
			ProjectReferenceFileJoin p = (ProjectReferenceFileJoin) o;
			return Objects.equals(project, p.project) && Objects.equals(referenceFile, p.referenceFile);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(project, referenceFile);
	}

	@Override
	public String getLabel() {
		return referenceFile.getLabel();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Project getSubject() {
		return this.project;
	}

	@Override
	public ReferenceFile getObject() {
		return this.referenceFile;
	}

	@Override
	public Date getTimestamp() {
		return this.createdDate;
	}

	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

}
