package ca.corefacility.bioinformatics.irida.model.event;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
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

	public UserRoleSetProjectEvent() {
	}

	public UserRoleSetProjectEvent(ProjectUserJoin join) {
		super(join.getSubject());
		this.user = join.getObject();
		this.role = join.getProjectRole();
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

	@Override
	public boolean equals(Object other) {
		if (other instanceof UserRoleSetProjectEvent) {
			UserRoleSetProjectEvent p = (UserRoleSetProjectEvent) other;
			return super.equals(other) && Objects.equals(user, p.user) && Objects.equals(role, p.role);
		}

		return false;
	}
}
