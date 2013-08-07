package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.IridaPermissionEvaluator.Permission;

/**
 * Evaluate whether or not an authenticated user can read a sequence file.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class ReadSequenceFilePermission implements Permission, ApplicationContextAware {

	private static final String PERMISSION_PROVIDED = "canReadSequenceFile";

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public boolean isAllowed(Authentication authentication, Object targetDomainObject) {
		// fast pass for administrators
		if (authentication.getAuthorities().contains(Role.ROLE_ADMIN)) {
			return true;
		}

		// similar to samples, an authenticated user can only read a sequence
		// file if they are participating in the project owning the sample
		// owning the sequence file.
		ProjectRepository projectRepository = applicationContext.getBean(ProjectRepository.class);
		SampleRepository sampleRepository = applicationContext.getBean(SampleRepository.class);
		UserRepository userRepository = applicationContext.getBean(UserRepository.class);
		SequenceFileRepository sequenceFileRepository = applicationContext.getBean(SequenceFileRepository.class);

		SequenceFile sf;

		if (targetDomainObject instanceof Long) {
			sf = sequenceFileRepository.read((Long) targetDomainObject);
		} else if (targetDomainObject instanceof SequenceFile) {
			sf = (SequenceFile) targetDomainObject;
		} else {
			throw new IllegalArgumentException("Parameter to " + getClass().getName()
					+ " must be of type Long or SequenceFile.");
		}

		Join<Sample, SequenceFile> sampleSequenceFile = sampleRepository.getSampleForSequenceFile(sf);
		Join<Project, Sample> projectSample = projectRepository.getProjectForSample(sampleSequenceFile.getSubject());
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
