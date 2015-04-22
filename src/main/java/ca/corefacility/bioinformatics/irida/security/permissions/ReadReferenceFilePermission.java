package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectReferenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 * Permission testing if a given user can read a given reference file.
 * 
 *
 */
@Component
public class ReadReferenceFilePermission extends BasePermission<ReferenceFile, Long> {

	public static final String PERMISSION_PROVIDED = "canReadReferenceFile";

	private UserRepository userRepository;
	private ProjectUserJoinRepository pujRepository;
	private ProjectReferenceFileJoinRepository prfRepository;

	@Autowired
	public ReadReferenceFilePermission(ReferenceFileRepository referenceFileRepository, UserRepository userRepository,
			ProjectUserJoinRepository pujRepository, ProjectReferenceFileJoinRepository prfRepository) {
		super(ReferenceFile.class, Long.class, referenceFileRepository);
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.prfRepository = prfRepository;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean customPermissionAllowed(Authentication authentication, ReferenceFile targetDomainObject) {
		// get the logged in user
		User user = userRepository.loadUserByUsername(authentication.getName());
		// get the projects for the file
		List<Join<Project, ReferenceFile>> findProjectsForReferenceFile = prfRepository
				.findProjectsForReferenceFile(targetDomainObject);

		// for each project
		for (Join<Project, ReferenceFile> prJoin : findProjectsForReferenceFile) {
			// get the users on the project
			List<Join<Project, User>> usersForProject = pujRepository.getUsersForProject(prJoin.getSubject());
			for (Join<Project, User> puJoin : usersForProject) {
				// if the user is on the project, they can read the file
				if (puJoin.getObject().equals(user)) {
					return true;
				}
			}
		}

		return false;
	}
}
