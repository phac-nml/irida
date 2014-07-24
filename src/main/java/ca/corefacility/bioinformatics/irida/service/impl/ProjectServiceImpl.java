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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.RelatedProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectUserJoinSpecification;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * A specialized service layer for projects.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Service
public class ProjectServiceImpl extends CRUDServiceImpl<Long, Project> implements ProjectService {

	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
	private static final String USER_JOIN_PROJECT_PROPERTY = "project";

	private ProjectUserJoinRepository pujRepository;
	private ProjectSampleJoinRepository psjRepository;
	private SampleRepository sampleRepository;
	private UserRepository userRepository;
	private ProjectRepository projectRepository;
	private RelatedProjectRepository relatedProjectRepository;

	protected ProjectServiceImpl() {
		super(null, null, Project.class);
	}

	@Autowired
	public ProjectServiceImpl(ProjectRepository projectRepository, SampleRepository sampleRepository,
			UserRepository userRepository, ProjectUserJoinRepository pujRepository,
			ProjectSampleJoinRepository psjRepository, RelatedProjectRepository relatedProjectRepository,
			Validator validator) {
		super(projectRepository, validator, Project.class);
		this.projectRepository = projectRepository;
		this.sampleRepository = sampleRepository;
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.psjRepository = psjRepository;
		this.relatedProjectRepository = relatedProjectRepository;
	}

	@Override
	public Iterable<Project> findAll() {
		return super.findAll();
	}

	@Override
	public Project read(Long id) {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	@Transactional
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
	public void removeUserFromProject(Project project, User user) throws ProjectWithoutOwnerException {
		ProjectUserJoin projectJoinForUser = pujRepository.getProjectJoinForUser(project, user);
		if(!allowRoleChange(projectJoinForUser)){
			throw new ProjectWithoutOwnerException("Removing this user would leave the project without an owner");
		}
		pujRepository.removeUserFromProject(project, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Join<Project, User> updateUserProjectRole(Project project, User user, ProjectRole projectRole)
			throws ProjectWithoutOwnerException {
		ProjectUserJoin projectJoinForUser = pujRepository.getProjectJoinForUser(project, user);
		if (projectJoinForUser == null) {
			throw new EntityNotFoundException("Join between this project and user does not exist. User: " + user
					+ " Project: " + project);
		}

		if (!allowRoleChange(projectJoinForUser)) {
			throw new ProjectWithoutOwnerException("This role change would leave the project without an owner");
		}

		projectJoinForUser.setProjectRole(projectRole);
		return pujRepository.save(projectJoinForUser);
	}

	private boolean allowRoleChange(ProjectUserJoin originalJoin) {
		// if they're not a project owner, no worries
		if (!originalJoin.getProjectRole().equals(ProjectRole.PROJECT_OWNER)) {
			return true;
		}

		List<Join<Project, User>> usersForProjectByRole = pujRepository.getUsersForProjectByRole(
				originalJoin.getSubject(), ProjectRole.PROJECT_OWNER);
		if (usersForProjectByRole.size() > 1) {
			// if there's more than one owner, no worries
			return true;
		} else {
			// if there's only 1 owner, they're leaving a projcet without an
			// owner!
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
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
	public void removeSampleFromProject(Project project, Sample sample) {
		psjRepository.removeSampleFromProject(project, sample);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Join<Project, User>> getProjectsForUser(User user) {
		return pujRepository.getProjectsForUser(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<Project> searchProjectsByName(String name, int page, int size, Direction order,
			String... sortProperties) {
		if (sortProperties.length == 0) {
			sortProperties = new String[] { CREATED_DATE_SORT_PROPERTY };
		}

		return projectRepository.findAll(ProjectSpecification.searchProjectName(name), new PageRequest(page, size,
				order, sortProperties));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProjectUserJoin> searchProjectsByNameForUser(User user, String term, int page, int size,
			Direction order, String... sortProperties) {
		if (sortProperties.length == 0) {
			sortProperties = new String[] { CREATED_DATE_SORT_PROPERTY };
		}
		// Drilling down into project for the sort properties
		for (int i = 0; i < sortProperties.length; i++) {
			sortProperties[i] = USER_JOIN_PROJECT_PROPERTY + "." + sortProperties[i];
		}

		return pujRepository.findAll(ProjectUserJoinSpecification.searchProjectNameWithUser(term, user),
				new PageRequest(page, size, order, sortProperties));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Join<Project, User>> getProjectsForUserWithRole(User user, ProjectRole role) {
		return pujRepository.getProjectsForUserWithRole(user, role);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public boolean userHasProjectRole(User user, Project project, ProjectRole projectRole) {
		List<Join<Project, User>> projects = getProjectsForUserWithRole(user, projectRole);
		return projects.contains(new ProjectUserJoin(project, user, projectRole));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RelatedProjectJoin addRelatedProject(Project subject, Project relatedProject) {
		if (subject.equals(relatedProject)) {
			throw new IllegalArgumentException("Project cannot be related to itself");
		}

		try {
			RelatedProjectJoin relation = relatedProjectRepository
					.save(new RelatedProjectJoin(subject, relatedProject));
			return relation;
		} catch (DataIntegrityViolationException e) {
			throw new EntityExistsException("Project " + subject.getLabel() + " is already related to "
					+ relatedProject.getLabel(), e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RelatedProjectJoin> getRelatedProjects(Project project) {
		return relatedProjectRepository.getRelatedProjectsForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RelatedProjectJoin> getReverseRelatedProjects(Project project) {
		return relatedProjectRepository.getReverseRelatedProjects(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeRelatedProject(RelatedProjectJoin relatedProject) {
		relatedProjectRepository.delete(relatedProject);
	}
}
