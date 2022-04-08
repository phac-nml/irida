package ca.corefacility.bioinformatics.irida.security.permissions.sample;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.BasePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ReadProjectPermission;

import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
/**
 * Confirms that the authenticated user is allowed to read a sample.
 * 
 * 
 */
@Component
public class ReadSampleCollectionPermission extends BasePermission<Sample, Long> {

	private static final String PERMISSION_PROVIDED = "canReadSampleCollection";

	private final ProjectSampleJoinRepository psjRepository;
	private final ReadProjectPermission readProjectPermission;

    private ProjectService projectService;
	private final UserRepository userRepository;

	/**
	 * Construct an instance of {@link ReadSamplePermission}
	 * 
	 * @param sampleRepository
	 *            The {@link SampleRepository}
	 * @param psjRepository
	 *            a {@link ProjectSampleJoinRepository}
	 * @param readProjectPermission
	 *            A {@link ReadProjectPermission} to test if you can read a
	 *            project
	 */
	@Autowired
	public ReadSampleCollectionPermission(final SampleRepository sampleRepository, final UserRepository userRepository,
			final ProjectSampleJoinRepository psjRepository, final ReadProjectPermission readProjectPermission, 
            ProjectService projectService) {
		super(Sample.class, Long.class, sampleRepository);
		this.psjRepository = psjRepository;
		this.readProjectPermission = readProjectPermission;

        this.projectService = projectService;
		this.userRepository = userRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(Authentication authentication, Sample s) {

		// samples are always associated with a project. for a user to be
		// allowed to read a sample, the user must be part of the associated
		// project.

        final User u = userRepository.loadUserByUsername(authentication.getName());
		List<Join<Project, Sample>> projectForSample = psjRepository.getProjectForSample(s);
        if (projectForSample.stream().anyMatch(j -> projectService.userHasProjectRole(u, j.getSubject(), ProjectRole.PROJECT_OWNER)) || projectForSample.stream().anyMatch(j -> projectService.userHasProjectRole(u, j.getSubject(), ProjectRole.PROJECT_USER))) {
            return true;
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
