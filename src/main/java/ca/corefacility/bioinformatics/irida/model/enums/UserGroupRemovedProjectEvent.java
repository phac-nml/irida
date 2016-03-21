package ca.corefacility.bioinformatics.irida.model.enums;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;

@Entity
public class UserGroupRemovedProjectEvent extends ProjectEvent {

	@NotNull
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "user_group_id")
	private final UserGroup userGroup;
	
	/**
	 * For hibernate
	 */
	@SuppressWarnings("unused")
	private UserGroupRemovedProjectEvent() {
		this.userGroup = null;
	}
	
	public UserGroupRemovedProjectEvent(final Project project, final UserGroup userGroup) {
		super(project);
		this.userGroup = userGroup;
	}
	
	@Override
	public String getLabel() {
		return "User " + userGroup.getLabel() + " removed from project " + getProject().getLabel();
	}
	
	public UserGroup getUserGroup() {
		return this.userGroup;
	}

}
