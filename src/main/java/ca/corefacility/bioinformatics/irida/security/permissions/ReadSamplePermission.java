package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 * Confirms that the authenticated user is allowed to read a sample.
 * 
 * 
 */
@Component
public class ReadSamplePermission extends BasePermission<Sample, Long> {

	private static final String PERMISSION_PROVIDED = "canReadSample";

	private UserRepository userRepository;
	private ProjectUserJoinRepository pujRepository;
	private ProjectSampleJoinRepository psjRepository;

	/**
	 * Construct an instance of {@link ReadSamplePermission}.
	 */
	@Autowired
	public ReadSamplePermission(SampleRepository sampleRepository, UserRepository userRepository,
			ProjectUserJoinRepository pujRepository, ProjectSampleJoinRepository psjRepository) {
		super(Sample.class, Long.class, sampleRepository);
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.psjRepository = psjRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(Authentication authentication, Sample s) {

		// samples are always associated with a project. for a user to be
		// allowed to read a sample, the user must be part of the associated
		// project.

		User u = userRepository.loadUserByUsername(authentication.getName());

		List<Join<Project, Sample>> projectForSample = psjRepository.getProjectForSample(s);
		for (Join<Project, Sample> projectSample : projectForSample) {
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
