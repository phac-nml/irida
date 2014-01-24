package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * A specialized service layer for projects.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Service
public class ProjectServiceImpl extends CRUDServiceImpl<Long, Project> implements ProjectService {

	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
	private ProjectUserJoinRepository pujRepository;
	private ProjectSampleJoinRepository psjRepository;
	private SampleRepository sampleRepository;
	private UserRepository userRepository;

	protected ProjectServiceImpl() {
		super(null, null, Project.class);
	}

	@Autowired
	public ProjectServiceImpl(ProjectRepository projectRepository, SampleRepository sampleRepository,
			UserRepository userRepository, ProjectUserJoinRepository pujRepository,
			ProjectSampleJoinRepository psjRepository, Validator validator) {
		super(projectRepository, validator, Project.class);
		this.sampleRepository = sampleRepository;
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.psjRepository = psjRepository;
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
		User user = userRepository.loadUserByUsername(userDetails.getUsername());
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
		try {
			ProjectUserJoin join = pujRepository.save(new ProjectUserJoin(project, user, role));
			return join;
		} catch (DataIntegrityViolationException e) {
			throw new EntityExistsException("The user [" + user.getId() + "] already belongs to project ["
					+ project.getId() + "]");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public void removeUserFromProject(Project project, User user) {
		pujRepository.removeUserFromProject(project, user);
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
				throw new ConstraintViolationException(constraintViolations);
			}
		}

		ProjectSampleJoin join = new ProjectSampleJoin(project, sample);

		try {
			return psjRepository.save(join);
		} catch (DataIntegrityViolationException e) {
			throw new EntityExistsException("Sample [" + sample.getId() + "] has already been added to project ["
					+ project.getId() + "]");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public void removeSampleFromProject(Project project, Sample sample) {
		psjRepository.removeSampleFromProject(project, sample);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("isAuthenticated()")
	public List<Join<Project, User>> getProjectsForUser(User user) {
		return pujRepository.getProjectsForUser(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("isAuthenticated()")
	public List<Join<Project, User>> getProjectsForUserWithRole(User user, ProjectRole role) {
		return pujRepository.getProjectsForUserWithRole(user, role);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	public boolean userHasProjectRole(User user, Project project, ProjectRole projectRole) {
		List<Join<Project, User>> projects = getProjectsForUserWithRole(user, projectRole);
		return projects.contains(new ProjectUserJoin(project, user));
	}
}
