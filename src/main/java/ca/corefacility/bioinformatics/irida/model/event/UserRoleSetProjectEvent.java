package ca.corefacility.bioinformatics.irida.model.event;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

@Entity
@Table(name = "project_event_user_role_set")
public class UserRoleSetProjectEvent extends ProjectEvent {
	@NotNull
	@ManyToOne(cascade = CascadeType.DETACH)
	private User user;

	@NotNull
	@Enumerated(EnumType.STRING)
	private ProjectRole role;

	public UserRoleSetProjectEvent(Project project, User user, ProjectRole role) {
		super(project);
		this.user = user;
		this.role = role;
	}

	@Override
	public String getLabel() {
		Project project = getProject();
		return "User " + user.getLabel() + " has role " + role.toString() + " on project " + project.getLabel();
	}

	public User getUser() {
		return user;
	}

	public ProjectRole getRole() {
		return role;
	}
}
