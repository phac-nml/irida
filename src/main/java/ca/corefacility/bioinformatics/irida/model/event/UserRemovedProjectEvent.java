package ca.corefacility.bioinformatics.irida.model.event;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Event for when a {@link User} is removed from a {@link Project}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "project_event_user_removed")
public class UserRemovedProjectEvent extends ProjectEvent {
	@NotNull
	@ManyToOne(cascade = CascadeType.DETACH)
	private User user;

	public UserRemovedProjectEvent() {
	}

	public UserRemovedProjectEvent(Project project, User user) {
		super(project);
		this.user = user;
	}

	@Override
	public String getLabel() {
		return "User " + user.getLabel() + " removed from project " + getProject().getLabel();
	}

	public User getUser() {
		return user;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof UserRemovedProjectEvent) {
			UserRemovedProjectEvent p = (UserRemovedProjectEvent) other;
			return super.equals(other) && Objects.equals(user, p.user);
		}

		return false;
	}

}
