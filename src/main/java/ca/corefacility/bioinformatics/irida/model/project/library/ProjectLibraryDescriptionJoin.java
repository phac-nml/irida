package ca.corefacility.bioinformatics.irida.model.project.library;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * A {@link Join} type for storing the relationship between a {@link Project}
 * and {@link LibraryDescription}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "project_library_description", uniqueConstraints = @UniqueConstraint(columnNames = { "project_id",
		"library_description_id" }))
@Audited
@EntityListeners(AuditingEntityListener.class)
public class ProjectLibraryDescriptionJoin implements Join<Project, LibraryDescription> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id", nullable = false)
	private final Project project;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "library_description_id", nullable = false)
	private final LibraryDescription libraryDescription;

	@Column(name = "default_library_description", nullable = false)
	private final Boolean defaultLibraryDescription;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false)
	private Date createdDate;

	/**
	 * For hibernate.
	 */
	@SuppressWarnings("unused")
	private ProjectLibraryDescriptionJoin() {
		this.id = null;
		this.project = null;
		this.libraryDescription = null;
		this.defaultLibraryDescription = null;
	}

	public ProjectLibraryDescriptionJoin(final Project project, final LibraryDescription libraryDescription,
			final Boolean defaultLibraryDescription) {
		this.id = null;
		this.project = project;
		this.libraryDescription = libraryDescription;
		this.defaultLibraryDescription = defaultLibraryDescription;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof ProjectLibraryDescriptionJoin) {
			final ProjectLibraryDescriptionJoin pj = (ProjectLibraryDescriptionJoin) o;
			return Objects.equals(project, pj.project) && Objects.equals(libraryDescription, pj.libraryDescription)
					&& Objects.equals(defaultLibraryDescription, pj.defaultLibraryDescription);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(project, libraryDescription, defaultLibraryDescription);
	}

	public Boolean isDefaultLibraryDescription() {
		return this.defaultLibraryDescription;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		throw new UnsupportedOperationException("ProjectLibraryDescriptionJoin is immutable!");
	}

	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

	@Override
	public Project getSubject() {
		return this.project;
	}

	@Override
	public LibraryDescription getObject() {
		return this.libraryDescription;
	}

	@Override
	public Date getTimestamp() {
		return this.createdDate;
	}

	@Override
	public void setSubject(Project subject) {
		throw new UnsupportedOperationException("ProjectLibraryDescriptionJoin is immutable!");
	}

	@Override
	public void setObject(LibraryDescription object) {
		throw new UnsupportedOperationException("ProjectLibraryDescriptionJoin is immutable!");
	}
}
