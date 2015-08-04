package ca.corefacility.bioinformatics.irida.security.permissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.RemoteRelatedProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 * Permission checking if a user can update a {@link RemoteRelatedProject}. This
 * should be allowed if the user is a {@link ProjectRole#PROJECT_OWNER} on the
 * associated {@link Project}.
 */
@Component
public class ManageRemoteRelatedProjectPermission extends BasePermission<RemoteRelatedProject, Long> {
	private static final Logger logger = LoggerFactory.getLogger(ManageRemoteRelatedProjectPermission.class);
	private static final String PERMISSION_PROVIDED = "canManageRemoteRelatedProject";

	private UserRepository userRepository;
	private ProjectUserJoinRepository pujRepository;
	private ProjectRepository projectRepository;

	@Autowired
	public ManageRemoteRelatedProjectPermission(RemoteRelatedProjectRepository rrpRepository,
			ProjectRepository projectRepository, UserRepository userRepository, ProjectUserJoinRepository pujRepository) {
		super(RemoteRelatedProject.class, Long.class, rrpRepository);
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.projectRepository = projectRepository;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

	@Override
	protected boolean customPermissionAllowed(Authentication authentication, RemoteRelatedProject targetDomainObject) {
		Project localProject = targetDomainObject.getLocalProject();

		logger.trace("Checking project owner permission for project " + localProject);

		ProjectOwnerPermission projectOwnerPermission = new ProjectOwnerPermission(projectRepository, userRepository,
				pujRepository);
		return projectOwnerPermission.isAllowed(authentication, localProject);
	}

}
