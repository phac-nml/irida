package ca.corefacility.bioinformatics.irida.model.user.group;

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

import ca.corefacility.bioinformatics.irida.constraints.MetadataRoleValidate;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * A relationship between a {@link UserGroup} and {@link Project}. This relationship mirrors the relationship defined by
 * {@link ProjectUserJoin}, but for {@link UserGroup}.
 */
@Entity
@Table(name = "user_group_project", uniqueConstraints = @UniqueConstraint(columnNames = {
		"project_id", "user_group_id" }))
@Audited
@EntityListeners(AuditingEntityListener.class)
@MetadataRoleValidate
public class UserGroupProjectJoin implements Join<Project, UserGroup> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id")
	@NotNull
	private final Project project;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "user_group_id")
	@NotNull
	private final UserGroup userGroup;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "project_role")
	private ProjectRole projectRole;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "metadata_role")
	private ProjectMetadataRole metadataRole;

	@NotNull
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
	private Date createdDate;

	/**
	 * for hibernate
	 */
	@SuppressWarnings("unused")
	private UserGroupProjectJoin() {
		this.id = null;
		this.createdDate = null;
		this.project = null;
		this.userGroup = null;
		this.projectRole = null;
	}

	/**
	 * Create a new {@link UserGroupProjectJoin}.
	 *
	 * @param project      the {@link Project} that you're permitting the {@link UserGroup} to access.
	 * @param userGroup    the {@link UserGroup} being permitted to access the {@link Project}.
	 * @param role         The Role the users in the group should have
	 * @param metadataRole the {@link ProjectMetadataRole} users in the group should have
	 */
	public UserGroupProjectJoin(final Project project, final UserGroup userGroup, final ProjectRole role,
			final ProjectMetadataRole metadataRole) {
		this.id = null;
		this.createdDate = new Date();
		this.project = project;
		this.userGroup = userGroup;
		this.projectRole = role;
		this.metadataRole = metadataRole;
	}

	@Override
	public int hashCode() {
		return Objects.hash(project, userGroup, projectRole, createdDate);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof UserGroupProjectJoin) {
			final UserGroupProjectJoin u = (UserGroupProjectJoin) o;
			return Objects.equals(u.project, this.project) && Objects.equals(u.userGroup, this.userGroup)
					&& Objects.equals(u.projectRole, this.projectRole) && Objects.equals(u.createdDate,
					this.createdDate);
		}

		return false;
	}

	public ProjectRole getProjectRole() {
		return this.projectRole;
	}

	public void setProjectRole(final ProjectRole projectRole) {
		this.projectRole = projectRole;
	}

	@Override
	public Long getId() {
		return this.id;
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
	public UserGroup getObject() {
		return this.userGroup;
	}

	@Override
	public Date getTimestamp() {
		return this.createdDate;
	}

	public ProjectMetadataRole getMetadataRole() {
		return metadataRole;
	}

	public void setMetadataRole(ProjectMetadataRole metadataRole) {
		this.metadataRole = metadataRole;
	}

}
