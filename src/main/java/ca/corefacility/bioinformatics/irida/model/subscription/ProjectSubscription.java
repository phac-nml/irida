package ca.corefacility.bioinformatics.irida.model.subscription;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Class for storing email subscriptions to {@link Project}s for {@link User}s .
 */
@Entity
@Table(name = "project_subscription")
@Audited
public class ProjectSubscription {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@NotNull
	User user;

	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	@NotNull
	Project project;

	public ProjectSubscription() {
	}

	public ProjectSubscription(User user, Project project) {
		super();
		this.user = user;
		this.project = project;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
