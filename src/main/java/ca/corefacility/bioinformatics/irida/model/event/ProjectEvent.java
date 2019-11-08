package ca.corefacility.bioinformatics.irida.model.event;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Class storing events that happen on a {@link Project}.
 * 
 *
 */
@Entity
@Table(name = "project_event")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ProjectEvent implements IridaThing {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", updatable = false)
	private Date createdDate;

	@NotNull
	@ManyToOne(cascade = CascadeType.DETACH)
	private Project project;

	protected ProjectEvent() {
		createdDate = new Date();
	}

	public ProjectEvent(Project project) {
		this();
		this.project = project;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Long getId() {
		return id;
	}

	public Project getProject() {
		return project;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ProjectEvent) {
			ProjectEvent p = (ProjectEvent) other;
			return Objects.equals(project, p.project) && Objects.equals(createdDate, p.createdDate);
		}

		return false;
	}
}
