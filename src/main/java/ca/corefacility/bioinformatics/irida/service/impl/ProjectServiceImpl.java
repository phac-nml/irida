package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.List;
import java.util.Map;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.events.annotations.LaunchesProjectEvent;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityRevisionDeletedException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectReferenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectReferenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.RelatedProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectUserJoinSpecification;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.util.SequenceFileUtilities;

/**
 * A specialized service layer for projects.
 * 
 */
@Service
public class ProjectServiceImpl extends CRUDServiceImpl<Long, Project> implements ProjectService {

	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

	private final ProjectUserJoinRepository pujRepository;
	private final ProjectSampleJoinRepository psjRepository;
	private final SampleRepository sampleRepository;
	private final UserRepository userRepository;
	private final RelatedProjectRepository relatedProjectRepository;
	private final ReferenceFileRepository referenceFileRepository;
	private final ProjectReferenceFileJoinRepository prfjRepository;
	private final SequenceFileUtilities sequenceFileUtilities;

	@Autowired
	public ProjectServiceImpl(ProjectRepository projectRepository, SampleRepository sampleRepository,
			UserRepository userRepository, ProjectUserJoinRepository pujRepository,
			ProjectSampleJoinRepository psjRepository, RelatedProjectRepository relatedProjectRepository,
			ReferenceFileRepository referenceFileRepository, ProjectReferenceFileJoinRepository prfjRepository,
			SequenceFileUtilities sequenceFileUtilities, Validator validator) {
		super(projectRepository, validator, Project.class);
		this.sampleRepository = sampleRepository;
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.psjRepository = psjRepository;
		this.relatedProjectRepository = relatedProjectRepository;
		this.referenceFileRepository = referenceFileRepository;
		this.prfjRepository = prfjRepository;
		this.sequenceFileUtilities = sequenceFileUtilities;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadProject')")
	public Revisions<Integer, Project> findRevisions(Long id) throws EntityRevisionDeletedException {
		return super.findRevisions(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<Project> search(Specification<Project> specification, int page, int size, Direction order,
			String... sortProperties) {
		return super.search(specification, page, size, order, sortProperties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadProject')")
	public Page<Revision<Integer, Project>> findRevisions(Long id, Pageable pageable)
			throws EntityRevisionDeletedException {
		return super.findRevisions(id, pageable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	@PostFilter("hasPermission(filterObject, 'canReadProject')")
	public Iterable<Project> findAll() {
		return super.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#id, 'canReadProject')")
	public Project read(Long id) {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_USER')")
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
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or hasPermission(#id, 'isProjectOwner')")
	public Project update(final Long id, final Map<String, Object> updateProperties) {
		return super.update(id, updateProperties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or hasPermission(#id, 'isProjectOwner')")
	public void delete(final Long id) {
		super.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@LaunchesProjectEvent(UserRoleSetProjectEvent.class)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'isProjectOwner')")
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
	@LaunchesProjectEvent(UserRemovedProjectEvent.class)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'isProjectOwner')")
	public void removeUserFromProject(Project project, User user) throws ProjectWithoutOwnerException {
		ProjectUserJoin projectJoinForUser = pujRepository.getProjectJoinForUser(project, user);
		if (!allowRoleChange(projectJoinForUser)) {
			throw new ProjectWithoutOwnerException("Removing this user would leave the project without an owner");
		}
		pujRepository.delete(projectJoinForUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@LaunchesProjectEvent(UserRoleSetProjectEvent.class)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project,'isProjectOwner')")
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
	@LaunchesProjectEvent(SampleAddedProjectEvent.class)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#project, 'isProjectOwner')")
	public ProjectSampleJoin addSampleToProject(Project project, Sample sample) {
		logger.trace("Adding sample to project.");

		// Check to ensure a sample with this sequencer id doesn't exist in this
		// project already
		if (sampleRepository.getSampleBySequencerSampleId(project, sample.getSequencerSampleId()) != null) {
			throw new EntityExistsException("Sample with sequencer id '" + sample.getSequencerSampleId()
					+ "' already exists in project " + project.getId());
		}

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
	@LaunchesProjectEvent(SampleAddedProjectEvent.class)
	@PreAuthorize("hasRole('ROLE_ADMIN') or ( hasPermission(#source, 'isProjectOwner') and hasPermission(#destination, 'isProjectOwner'))")
	public ProjectSampleJoin moveSampleBetweenProjects(Project source, Project destination, Sample sample) {
		ProjectSampleJoin join = addSampleToProject(destination, sample);
		removeSampleFromProject(source, sample);

		return join;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'isProjectOwner')")
	public void removeSampleFromProject(Project project, Sample sample) {
		ProjectSampleJoin readSampleForProject = psjRepository.readSampleForProject(project, sample);
		psjRepository.delete(readSampleForProject);
		
		// if the sample doesn't refer to any other projects, delete it
		if (psjRepository.getProjectForSample(sample).isEmpty()) {
			sampleRepository.delete(sample);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'isProjectOwner')")
	public void removeSamplesFromProject(Project project, Iterable<Sample> samples) {
		for (Sample s : samples) {
			removeSampleFromProject(project, s);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_USER')")
	public List<Join<Project, User>> getProjectsForUser(User user) {
		return pujRepository.getProjectsForUser(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<ProjectUserJoin> searchProjectUsers(Specification<ProjectUserJoin> specification, int page, int size,
			Direction order, String... sortProperties) {
		if (sortProperties.length == 0) {
			sortProperties = new String[] { CREATED_DATE_SORT_PROPERTY };
		}
		return pujRepository.findAll(specification, new PageRequest(page, size, order, sortProperties));
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	public boolean userHasProjectRole(User user, Project project, ProjectRole projectRole) {
		Page<ProjectUserJoin> searchProjectUsers = searchProjectUsers(
				ProjectUserJoinSpecification.getProjectJoinsWithRole(user, projectRole), 0, Integer.MAX_VALUE,
				Direction.ASC);
		return searchProjectUsers.getContent().contains(new ProjectUserJoin(project, user, projectRole));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#subject,'isProjectOwner') and hasPermission(#relatedProject,'canReadProject')")
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
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	@PostFilter("hasPermission(filterObject.object, 'canReadProject')")
	public List<RelatedProjectJoin> getRelatedProjects(Project project) {
		return relatedProjectRepository.getRelatedProjectsForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public List<RelatedProjectJoin> getReverseRelatedProjects(Project project) {
		return relatedProjectRepository.getReverseRelatedProjects(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project.subject, 'isProjectOwner')")
	public void removeRelatedProject(RelatedProjectJoin relatedProject) {
		relatedProjectRepository.delete(relatedProject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#subject,'isProjectOwner')")
	public void removeRelatedProject(Project subject, Project relatedProject) {
		RelatedProjectJoin relatedProjectJoin = relatedProjectRepository.getRelatedProjectJoin(subject, relatedProject);
		removeRelatedProject(relatedProjectJoin);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public List<Join<Project, Sample>> getProjectsForSample(Sample sample) {
		return psjRepository.getProjectForSample(sample);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'isProjectOwner')")
	public Join<Project, ReferenceFile> addReferenceFileToProject(Project project, ReferenceFile referenceFile) {
		// calculate the file length
		Long referenceFileLength = sequenceFileUtilities.countSequenceFileLengthInBases(referenceFile.getFile());
		referenceFile.setFileLength(referenceFileLength);

		referenceFile = referenceFileRepository.save(referenceFile);
		ProjectReferenceFileJoin j = new ProjectReferenceFileJoin(project, referenceFile);
		return prfjRepository.save(j);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'isProjectOwner')")
	public void removeReferenceFileFromProject(Project project, ReferenceFile file) {
		List<Join<Project, ReferenceFile>> referenceFilesForProject = prfjRepository
				.findReferenceFilesForProject(project);
		Join<Project, ReferenceFile> specificJoin = null;
		for (Join<Project, ReferenceFile> join : referenceFilesForProject) {
			if (join.getObject().equals(file)) {
				specificJoin = join;
				break;
			}
		}
		if (specificJoin != null) {
			prfjRepository.delete((ProjectReferenceFileJoin) specificJoin);
		} else {
			throw new EntityNotFoundException("Cannot find a join for project [" + project.getName()
					+ "] and reference file [" + file.getLabel() + "].");
		}
	}
}
