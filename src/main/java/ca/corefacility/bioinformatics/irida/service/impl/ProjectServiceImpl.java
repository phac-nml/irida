package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * A specialized service layer for projects.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectServiceImpl extends CRUDServiceImpl<Long, Project> implements ProjectService {

	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
	private ProjectRepository projectRepository;
	private SampleRepository sampleRepository;
	private UserRepository userRepository;
	
	protected ProjectServiceImpl() {
		super(null, null, Project.class);
	}

	public ProjectServiceImpl(ProjectRepository projectRepository, SampleRepository sampleRepository,
			UserRepository userRepository, Validator validator) {
		super(projectRepository, validator, Project.class);
		this.projectRepository = projectRepository;
		this.sampleRepository = sampleRepository;
		this.userRepository = userRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional
	public Project create(Project p) {
		Project project = super.create(p);
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userRepository.getUserByUsername(userDetails.getUsername());
		addUserToProject(project, user, ProjectRole.PROJECT_OWNER);
		return project;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadProject')")
	public Project read(Long id) {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PostFilter("hasPermission(filterObject, 'canReadProject')")
	public Iterable<Project> findAll() {
		return super.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Join<Project, User> addUserToProject(Project project, User user, ProjectRole role) {
		return projectRepository.addUserToProject(project, user,role);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public void removeUserFromProject(Project project, User user) {
		projectRepository.removeUserFromProject(project, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public ProjectSampleJoin addSampleToProject(Project project, Sample sample) {
		logger.trace("Adding sample to project.");
		// the sample hasn't been persisted before, persist it before calling
		// the relationshipRepository.
		if (sample.getId() == null) {
			logger.trace("Going to validate and persist sample prior to creating relationship.");
			// validate the sample, then persist it:
			Set<ConstraintViolation<Sample>> constraintViolations = validator.validate(sample);
			if (constraintViolations.isEmpty()) {
				sample = sampleRepository.save(sample);
			} else {
				throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
			}
		}

		return projectRepository.addSampleToProject(project, sample);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public void removeSampleFromProject(Project project, Sample sample) {
		projectRepository.removeSampleFromProject(project, sample);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("isAuthenticated()")
	public List<Join<Project, User>> getProjectsForUser(User user) {
		return new ArrayList<Join<Project, User>>(projectRepository.getProjectsForUser(user));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("isAuthenticated()")
	public List<Join<Project, User>> getProjectsForUserWithRole(User user,ProjectRole role) {
		return new ArrayList<Join<Project, User>>(projectRepository.getProjectsForUserWithRole(user,role));	
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	public boolean userHasProjectRole(User user, Project project, ProjectRole projectRole) {
		return projectRepository.userHasProjectRole(user, project, projectRole);
	}
}
