package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.Collection;

import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;

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

		// samples are always associated with a project. for a user to be
		// allowed to read a sample, the user must be part of the associated
		// project.
		Join<Project, Sample> projectSample = projectRepository.getProjectForSample(s);
		Collection<Join<Project, User>> projectUsers = userRepository.getUsersForProject(projectSample.getSubject());
		User u = userRepository.getUserByUsername(authentication.getName());
		for (Join<Project, User> projectUser : projectUsers) {
			if (u.equals(projectUser.getObject())) {
				return true;
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
