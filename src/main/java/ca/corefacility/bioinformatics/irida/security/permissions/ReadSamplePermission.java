package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.ProjectUserJoinRepository;

/**
 * Confirms that the authenticated user is allowed to read a sample.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class ReadSamplePermission extends BasePermission<Sample> {

	private static final String PERMISSION_PROVIDED = "canReadSample";

	/**
	 * Construct an instance of {@link ReadSamplePermission}.
	 */
	public ReadSamplePermission() {
		super(Sample.class, "sampleRepository");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(Authentication authentication, Sample s) {
		UserRepository userRepository = getApplicationContext().getBean(UserRepository.class);
		ProjectRepository projectRepository = getApplicationContext().getBean(ProjectRepository.class);
		ProjectUserJoinRepository pujRepository = getApplicationContext().getBean(ProjectUserJoinRepository.class);

		// samples are always associated with a project. for a user to be
		// allowed to read a sample, the user must be part of the associated
		// project.

		User u = userRepository.loadUserByUsername(authentication.getName());

		Collection<ProjectSampleJoin> projectForSample = projectRepository.getProjectForSample(s);
		for (ProjectSampleJoin projectSample : projectForSample) {
			List<Join<Project, User>> projectUsers = pujRepository.getUsersForProject(projectSample.getSubject());
			for (Join<Project, User> projectUser : projectUsers) {
				if (u.equals(projectUser.getObject())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
