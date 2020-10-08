package ca.corefacility.bioinformatics.irida.model.joins.impl;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * A join table and class for users and projects.
 * 
 */
@Entity
@Table(name = "project_user", uniqueConstraints = @UniqueConstraint(columnNames = { "project_id", "user_id" }) )
@Audited
@EntityListeners(AuditingEntityListener.class)
public class ProjectUserJoin implements Join<Project, User> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@Enumerated(EnumType.STRING)
	private ProjectRole projectRole;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date createdDate;

	@Column(name = "email_subscription")
	@NotNull
	private boolean emailSubscription;

	public ProjectUserJoin() {
		createdDate = new Date();
		projectRole = ProjectRole.PROJECT_USER;
		emailSubscription = false;
	}

	public ProjectUserJoin(Project subject, User object, ProjectRole projectRole) {
		this();
		this.project = subject;
		this.user = object;
		this.projectRole = projectRole;
	}

	public Long getId() {
		return this.id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ProjectUserJoin) {
			ProjectUserJoin other = (ProjectUserJoin) o;
			return Objects.equals(project, other.project) && Objects.equals(user, other.user)
					&& Objects.equals(projectRole, other.projectRole);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(project, user, projectRole);
	}

	@Override
	public Project getSubject() {
		return project;
	}

	@Override
	public User getObject() {
		return user;
	}

	@Override
	public Date getTimestamp() {
		return getCreatedDate();
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Get the user's role on the project
	 * 
	 * @return A representation of the user's project role
	 */
	public ProjectRole getProjectRole() {
		return projectRole;
	}

	/**
	 * Set the user's role on the project
	 * 
	 * @param userRole
	 *            The representation of the user's role on the project
	 */
	public void setProjectRole(ProjectRole userRole) {
		this.projectRole = userRole;
	}

	public void setEmailSubscription(boolean emailSubscription) {
		this.emailSubscription = emailSubscription;
	}

	public boolean isEmailSubscription() {
		return emailSubscription;
	}

}
