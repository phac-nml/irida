package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.IridaPermissionEvaluator.Permission;

/**
 * Confirms that the authenticated user is allowed to read a sample.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class ReadSamplePermission implements Permission, ApplicationContextAware {

	private static final String PERMISSION_PROVIDED = "canReadSample";

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public boolean isAllowed(Authentication authentication, Object targetDomainObject) {
		UserRepository userRepository = applicationContext.getBean(UserRepository.class);
		ProjectRepository projectRepository = applicationContext.getBean(ProjectRepository.class);
		SampleRepository sampleRepository = applicationContext.getBean(SampleRepository.class);

		Sample s;

		if (targetDomainObject instanceof Long) {
			s = sampleRepository.read((Long) targetDomainObject);
		} else if (targetDomainObject instanceof Sample) {
			s = (Sample) targetDomainObject;
		} else {
			throw new IllegalArgumentException("Parameter to " + getClass().getName()
					+ " must be of type Long or Sample.");
		}

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

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
