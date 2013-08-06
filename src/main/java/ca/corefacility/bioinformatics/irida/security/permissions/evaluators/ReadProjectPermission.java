package ca.corefacility.bioinformatics.irida.security.permissions.evaluators;

import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.security.permissions.evaluators.IridaPermissionEvaluator.Permission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.UserService;

/**
 * Confirms that the authenticated user is allowed to read a project.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class ReadProjectPermission implements Permission, ApplicationContextAware {

	private static final String PERMISSION_PROVIDED = "canReadProject";

	private ApplicationContext applicationContext;
	private ProjectService projectService;
	private UserService userService;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public boolean isAllowed(Authentication authentication, Object targetDomainObject) {
		// administrators can see all projects.
		if (authentication.getAuthorities().contains(Role.ROLE_ADMIN)) {
			return true;
		}

		this.projectService = applicationContext.getBean(ProjectService.class);
		this.userService = applicationContext.getBean(UserService.class);

		Project p;

		// get the project from the database (if necessary)
		if (targetDomainObject instanceof Long) {
			p = projectService.read((Long) targetDomainObject);
		} else if (targetDomainObject instanceof Project) {
			p = (Project) targetDomainObject;
		} else {
			throw new IllegalArgumentException("Parameter to " + getClass().getName()
					+ " must be of type Long or Project.");
		}

		// if not an administrator, then we need to figure out if the
		// authenticated user is participating in the project.
		User u = userService.getUserByUsername(authentication.getName());
		Collection<Join<Project, User>> projectUsers = userService.getUsersForProject(p);

		for (Join<Project, User> projectUser : projectUsers) {
			if (projectUser.getObject().equals(u)) {
				// this user is participating in the project.
				return true;
			}
		}

		return false;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
