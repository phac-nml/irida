package ca.corefacility.bioinformatics.irida.model.event;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;

/**
 * Event created when a {@link UserGroup} is added or has role changed on a {@link Project}.
 *
 */
@Entity
public class UserGroupRoleSetProjectEvent extends ProjectEvent {
	@NotNull
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "user_group_id")
	private final UserGroup userGroup;

	@NotNull
	@Enumerated(EnumType.STRING)
	private final ProjectRole role;
	
	/**
	 * for hibernate
	 */
	@SuppressWarnings("unused")
	private UserGroupRoleSetProjectEvent() {
		this.userGroup = null;
		this.role = null;
	}
	
	public UserGroupRoleSetProjectEvent(final UserGroupProjectJoin join) {
		super(join.getSubject());
		this.userGroup = join.getObject();
		this.role = join.getProjectRole();
	}
	
	public UserGroup getUserGroup() {
		return this.userGroup;
	}
	
	public ProjectRole getRole() {
		return this.role;
	}
	
	@Override
	public String getLabel() {
		Project project = getProject();
		return "User group " + userGroup.getLabel() + " has role " + role.toString() + " on project " + project.getLabel();
	}

}
