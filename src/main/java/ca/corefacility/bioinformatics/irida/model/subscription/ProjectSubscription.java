package ca.corefacility.bioinformatics.irida.model.subscription;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;

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

	@NotNull
	@Column(name = "email_subscription")
	boolean emailSubscription;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
	private Date createdDate;

	public ProjectSubscription() {
	}

	public ProjectSubscription(User user, Project project, boolean emailSubscription) {
		super();
		this.user = user;
		this.project = project;
		this.createdDate = new Date();
		this.emailSubscription = emailSubscription;
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

	public boolean isEmailSubscription() {
		return emailSubscription;
	}

	public void setEmailSubscription(boolean emailSubscription) {
		this.emailSubscription = emailSubscription;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}