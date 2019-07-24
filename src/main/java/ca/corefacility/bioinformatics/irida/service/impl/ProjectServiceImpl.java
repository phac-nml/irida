package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.criteria.*;
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
import org.springframework.data.domain.Sort;
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
import org.springframework.util.StringUtils;

import ca.corefacility.bioinformatics.irida.events.annotations.LaunchesProjectEvent;
import ca.corefacility.bioinformatics.irida.exceptions.*;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.enums.UserGroupRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.*;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectReferenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.ProjectAnalysisSubmissionJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.*;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A specialized service layer for projects.
 */
@Service
public class ProjectServiceImpl extends CRUDServiceImpl<Long, Project> implements ProjectService {

	// settings that can be updated locally for a remote project
	public List<String> VALID_LOCAL_SETTINGS = Lists.newArrayList("assembleUploads", "syncFrequency", "remoteStatus",
			"genomeSize", "minimumCoverage", "maximumCoverage", "sistrTypingUploads");

	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

	private final ProjectUserJoinRepository pujRepository;
	private final ProjectSampleJoinRepository psjRepository;
	private final SampleRepository sampleRepository;
	private final UserRepository userRepository;
	private final RelatedProjectRepository relatedProjectRepository;
	private final ReferenceFileRepository referenceFileRepository;
	private final ProjectReferenceFileJoinRepository prfjRepository;
	private final UserGroupProjectJoinRepository ugpjRepository;
	private final SampleSequencingObjectJoinRepository ssoRepository;
	private final ProjectAnalysisSubmissionJoinRepository pasRepository;
	private final SequencingObjectRepository sequencingObjectRepository;
	private final ProjectRepository projectRepository;

	@Autowired
	public ProjectServiceImpl(ProjectRepository projectRepository, SampleRepository sampleRepository,
			UserRepository userRepository, ProjectUserJoinRepository pujRepository,
			ProjectSampleJoinRepository psjRepository, RelatedProjectRepository relatedProjectRepository,
			ReferenceFileRepository referenceFileRepository, ProjectReferenceFileJoinRepository prfjRepository,
			final UserGroupProjectJoinRepository ugpjRepository, SampleSequencingObjectJoinRepository ssoRepository,
			ProjectAnalysisSubmissionJoinRepository pasRepository,
			SequencingObjectRepository sequencingObjectRepository, Validator validator) {
		super(projectRepository, validator, Project.class);
		this.projectRepository = projectRepository;
		this.sampleRepository = sampleRepository;
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.psjRepository = psjRepository;
		this.relatedProjectRepository = relatedProjectRepository;
		this.referenceFileRepository = referenceFileRepository;
		this.prfjRepository = prfjRepository;
		this.ugpjRepository = ugpjRepository;
		this.ssoRepository = ssoRepository;
		this.pasRepository = pasRepository;
		this.sequencingObjectRepository = sequencingObjectRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#idents, 'canReadProject')")
	public Iterable<Project> readMultiple(Iterable<Long> idents) {
		return super.readMultiple(idents);
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
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadProject')")
	public Page<Revision<Integer, Project>> findRevisions(Long id, Pageable pageable)
			throws EntityRevisionDeletedException {
		return super.findRevisions(id, pageable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_SEQUENCER')")
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
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		User user = userRepository.loadUserByUsername(userDetails.getUsername());
		addUserToProject(project, user, ProjectRole.PROJECT_OWNER);
		return project;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#object, 'isProjectOwner')")
	public Project update(Project object) {
		return super.update(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#id, 'isProjectOwner')")
	public Project updateFields(Long id, Map<String, Object> updatedFields)
			throws ConstraintViolationException, EntityExistsException, InvalidPropertyException {
		return super.updateFields(id, updatedFields);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#project, 'canManageLocalProjectSettings')")
	public Project updateProjectSettings(Project project, Map<String, Object> updates) {
		// ensure only accepted fields are updated
		Set<String> keys = Sets.newHashSet(updates.keySet());
		keys.removeAll(VALID_LOCAL_SETTINGS);
		if (!keys.isEmpty()) {
			throw new IllegalArgumentException("Invalid update fields for project settings: " + updates.keySet());
		}

		return updateFields(project.getId(), updates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or hasPermission(#id, 'canManageLocalProjectSettings')")
	public void delete(final Long id) {
		super.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@LaunchesProjectEvent(UserRoleSetProjectEvent.class)
	@PreAuthorize("hasPermission(#project, 'canManageLocalProjectSettings')")
	public Join<Project, User> addUserToProject(Project project, User user, ProjectRole role) {
		try {
			ProjectUserJoin join = pujRepository.save(new ProjectUserJoin(project, user, role));
			return join;
		} catch (DataIntegrityViolationException e) {
			throw new EntityExistsException(
					"The user [" + user.getId() + "] already belongs to project [" + project.getId() + "]");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@LaunchesProjectEvent(UserRemovedProjectEvent.class)
	@PreAuthorize("hasPermission(#project, 'canManageLocalProjectSettings')")
	public void removeUserFromProject(Project project, User user) throws ProjectWithoutOwnerException {
		ProjectUserJoin projectJoinForUser = pujRepository.getProjectJoinForUser(project, user);
		if (!allowRoleChange(projectJoinForUser.getSubject(), projectJoinForUser.getProjectRole())) {
			throw new ProjectWithoutOwnerException("Removing this user would leave the project without an owner");
		}
		pujRepository.delete(projectJoinForUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@LaunchesProjectEvent(UserGroupRemovedProjectEvent.class)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canManageLocalProjectSettings')")
	public void removeUserGroupFromProject(Project project, UserGroup userGroup) throws ProjectWithoutOwnerException {
		final UserGroupProjectJoin j = ugpjRepository.findByProjectAndUserGroup(project, userGroup);
		if (!allowRoleChange(project, j.getProjectRole())) {
			throw new ProjectWithoutOwnerException("Removing this user group would leave the project without an owner.");
		}
		ugpjRepository.delete(j);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@LaunchesProjectEvent(UserRoleSetProjectEvent.class)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project,'canManageLocalProjectSettings')")
	public Join<Project, User> updateUserProjectRole(Project project, User user, ProjectRole projectRole)
			throws ProjectWithoutOwnerException {
		ProjectUserJoin projectJoinForUser = pujRepository.getProjectJoinForUser(project, user);
		if (projectJoinForUser == null) {
			throw new EntityNotFoundException(
					"Join between this project and user does not exist. User: " + user + " Project: " + project);
		}

		if (!allowRoleChange(projectJoinForUser.getSubject(), projectJoinForUser.getProjectRole())) {
			throw new ProjectWithoutOwnerException("This role change would leave the project without an owner");
		}

		projectJoinForUser.setProjectRole(projectRole);
		return pujRepository.save(projectJoinForUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@LaunchesProjectEvent(UserGroupRoleSetProjectEvent.class)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canManageLocalProjectSettings')")
	public Join<Project, UserGroup> updateUserGroupProjectRole(Project project, UserGroup userGroup,
			ProjectRole projectRole) throws ProjectWithoutOwnerException {
		final UserGroupProjectJoin j = ugpjRepository.findByProjectAndUserGroup(project, userGroup);
		if (j == null) {
			throw new EntityNotFoundException(
					"Join between this project and group does not exist. Group: " + userGroup + " Project: " + project);
		}
		if (!allowRoleChange(project, j.getProjectRole())) {
			throw new ProjectWithoutOwnerException("This role change would leave the project without an owner");
		}
		j.setProjectRole(projectRole);
		return ugpjRepository.save(j);
	}

	private boolean allowRoleChange(final Project project, final ProjectRole projectRoleToChange) {
		// if they're not a project owner, no worries
		if (!projectRoleToChange.equals(ProjectRole.PROJECT_OWNER)) {
			return true;
		}

		final Collection<Join<Project, User>> usersForProjectByRole = pujRepository.getUsersForProjectByRole(project,
				ProjectRole.PROJECT_OWNER);
		final Collection<UserGroupProjectJoin> groups = ugpjRepository.findGroupsByProjectAndProjectRole(project,
				ProjectRole.PROJECT_OWNER);
		if (usersForProjectByRole.size() + groups.size() > 1) {
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
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or (hasPermission(#project, 'isProjectOwner'))")
	public ProjectSampleJoin addSampleToProject(Project project, Sample sample, boolean owner) {
		logger.trace("Adding sample to project.");

		// Check to ensure a sample with this sequencer id doesn't exist in this
		// project already
		if (sampleRepository.getSampleBySampleName(project, sample.getSampleName()) != null) {
			throw new EntityExistsException(
					"Sample with sequencer id '" + sample.getSampleName() + "' already exists in project " + project.getId());
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

		ProjectSampleJoin join = new ProjectSampleJoin(project, sample, owner);

		try {
			return psjRepository.save(join);
		} catch (DataIntegrityViolationException e) {
			throw new EntityExistsException(
					"Sample [" + sample.getId() + "] has already been added to project [" + project.getId() + "]");
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
		//read the existing ProjectSampleJoin to see if we're the owner
		ProjectSampleJoin projectSampleJoin = psjRepository.readSampleForProject(source, sample);

		//set the ownership on the sample given the existing permissions
		ProjectSampleJoin join = addSampleToProject(destination, sample, projectSampleJoin.isOwner());

		//remove the old join
		removeSampleFromProject(source, sample);

		return join;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@LaunchesProjectEvent(SampleAddedProjectEvent.class)
	@PreAuthorize("hasPermission(#source, 'canManageLocalProjectSettings')"
			+ " and hasPermission(#destination, 'isProjectOwner')" + " and hasPermission(#samples, 'canReadSample')"
			+ " and ((not #giveOwner) or hasPermission(#samples, 'canUpdateSample'))")
	public List<ProjectSampleJoin> shareSamples(Project source, Project destination, Collection<Sample> samples,
			boolean giveOwner) {

		List<ProjectSampleJoin> newJoins = new ArrayList<>();

		for (Sample sample : samples) {
			ProjectSampleJoin newJoin = addSampleToProject(destination, sample, giveOwner);

			logger.trace("Shared sample " + sample.getId() + " to project " + destination.getId());

			newJoins.add(newJoin);
		}

		return newJoins;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@LaunchesProjectEvent(SampleAddedProjectEvent.class)
	@PreAuthorize("hasPermission(#source, 'isProjectOwner') and hasPermission(#destination, 'isProjectOwner') "
			+ "and hasPermission(#samples, 'canReadSample') ")
	public List<ProjectSampleJoin> moveSamples(Project source, Project destination, Collection<Sample> samples) {

		List<ProjectSampleJoin> newJoins = new ArrayList<>();

		for (Sample sample : samples) {
			ProjectSampleJoin newJoin = moveSampleBetweenProjects(source, destination, sample);

			logger.trace("Moved sample " + sample.getId() + " to project " + destination.getId());

			newJoins.add(newJoin);
		}

		return newJoins;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canManageLocalProjectSettings')")
	@LaunchesProjectEvent(SampleRemovedProjectEvent.class)
	public void removeSampleFromProject(Project project, Sample sample) {
		ProjectSampleJoin readSampleForProject = psjRepository.readSampleForProject(project, sample);
		psjRepository.delete(readSampleForProject);

		// if the sample doesn't refer to any other projects, delete it
		if (psjRepository.getProjectForSample(sample)
				.isEmpty()) {
			sampleRepository.delete(sample);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canManageLocalProjectSettings')")
	@LaunchesProjectEvent(SampleRemovedProjectEvent.class)
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
		final List<Join<Project, User>> userJoinProjects = pujRepository.getProjectsForUser(user);
		final List<Join<Project, User>> groupJoinProjects = ugpjRepository.findProjectsByUser(user)
				.stream()
				.map(j -> new ProjectUserJoin(j.getSubject(), user, j.getProjectRole()))
				.collect(Collectors.toList());

		return new ImmutableList.Builder<Join<Project, User>>().addAll(userJoinProjects)
				.addAll(groupJoinProjects)
				.build();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	public boolean userHasProjectRole(User user, Project project, ProjectRole projectRole) {
		Page<ProjectUserJoin> searchProjectUsers = pujRepository.findAll(getProjectJoinsWithRole(user, projectRole),
				new PageRequest(0, Integer.MAX_VALUE, Sort.Direction.ASC, CREATED_DATE_SORT_PROPERTY));
		return searchProjectUsers.getContent()
				.contains(new ProjectUserJoin(project, user, projectRole));
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
			RelatedProjectJoin relation = relatedProjectRepository.save(new RelatedProjectJoin(subject, relatedProject));
			return relation;
		} catch (DataIntegrityViolationException e) {
			throw new EntityExistsException("Project " + subject.getLabel() + " is already related to " + relatedProject.getLabel(), e);
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
	@PostFilter("hasPermission(filterObject.subject, 'canReadProject')")
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
		List<Join<Project, ReferenceFile>> referenceFilesForProject = prfjRepository.findReferenceFilesForProject(
				project);
		Join<Project, ReferenceFile> specificJoin = null;
		for (Join<Project, ReferenceFile> join : referenceFilesForProject) {
			if (join.getObject()
					.equals(file)) {
				specificJoin = join;
				break;
			}
		}
		if (specificJoin != null) {
			prfjRepository.delete((ProjectReferenceFileJoin) specificJoin);
		} else {
			throw new EntityNotFoundException(
					"Cannot find a join for project [" + project.getName() + "] and reference file [" + file.getLabel()
							+ "].");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#p, 'canManageLocalProjectSettings')")
	public Page<Project> getUnassociatedProjects(final Project p, final String searchName, final Integer page, final Integer count,
			final Direction sortDirection, final String... sortedBy) {

		final UserDetails loggedInDetails = (UserDetails) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		final User loggedIn = userRepository.loadUserByUsername(loggedInDetails.getUsername());
		final PageRequest pr = new PageRequest(page, count, sortDirection, getOrDefaultSortProperties(sortedBy));
		if (loggedIn.getSystemRole()
				.equals(Role.ROLE_ADMIN)) {
			return projectRepository.findAllProjectsByNameExcludingProject(searchName, p, pr);
		} else {
			return projectRepository.findProjectsByNameExcludingProjectForUser(searchName, p, loggedIn, pr);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<Project> findProjectsForUser(final String search, final Integer page, final Integer count, final Sort sort) {
		final UserDetails loggedInDetails = (UserDetails) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		final User loggedIn = userRepository.loadUserByUsername(loggedInDetails.getUsername());
		final PageRequest pr = new PageRequest(page, count, getOrDefaultSort(sort));
		return projectRepository.findAll(searchForProjects(search, null, null, loggedIn), pr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Page<Project> findAllProjects(String searchValue, int currentPage, int length, Sort sort) {
		final PageRequest pr = new PageRequest(currentPage, length, sort);
		return projectRepository.findAll(searchForProjects(searchValue, null, null, null), pr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@LaunchesProjectEvent(UserGroupRoleSetProjectEvent.class)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canManageLocalProjectSettings')")
	public Join<Project, UserGroup> addUserGroupToProject(final Project project, final UserGroup userGroup, final ProjectRole role) {
		return ugpjRepository.save(new UserGroupProjectJoin(project, userGroup, role));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<Project> getProjectsWithRemoteSyncStatus(SyncStatus syncStatus) {
		return projectRepository.getProjectsWithRemoteSyncStatus(syncStatus);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<Project> getRemoteProjects() {
		return projectRepository.getRemoteProjects();
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	@PostFilter("hasPermission(filterObject, 'canReadProject')")
	public Set<Project> getProjectsForSequencingObjects(Collection<? extends SequencingObject> sequences) {
		Set<Project> projects = new HashSet<>();

		if (sequences != null && !sequences.isEmpty()) {
			// get the samples for a seq object
			Set<SampleSequencingObjectJoin> samples = sequences.stream()
					.map(s -> ssoRepository.getSampleForSequencingObject(s))
					.collect(Collectors.toSet());

			// get the projects for the samples
			for (SampleSequencingObjectJoin s : samples) {
				psjRepository.getProjectForSample(s.getSubject())
						.forEach(p -> {
							// p may be null if sample was removed from all projects
							if (p != null) {
								projects.add(p.getSubject());
							}
						});
			}
		}

		return projects;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#submission, 'canReadAnalysisSubmission')")
	@PostFilter("hasPermission(filterObject.subject, 'canReadProject')")
	public List<ProjectAnalysisSubmissionJoin> getProjectsForAnalysisSubmission(AnalysisSubmission submission) {
		return pasRepository.getProjectsForSubmission(submission);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@PreAuthorize("hasPermission(#sampleIds, 'canUpdateSample')")
	@Override
	public Project createProjectWithSamples(Project project, List<Long> sampleIds, boolean owner) {

		Project created = create(project);

		sampleIds.forEach(sid -> {
			Sample s = sampleRepository.findOne(sid);
			addSampleToProject(project, s, owner);
		});

		return created;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#submission, 'canReadAnalysisSubmission')")
	@PostFilter("hasPermission(filterObject, 'canReadProject')")
	@Override
	public List<Project> getProjectsUsedInAnalysisSubmission(AnalysisSubmission submission) {
		Set<SequencingObject> findSequencingObjectsForAnalysisSubmission = sequencingObjectRepository.findSequencingObjectsForAnalysisSubmission(
				submission);

		// get available projects
		Set<Project> projectsInAnalysis = getProjectsForSequencingObjects(findSequencingObjectsForAnalysisSubmission);

		return Lists.newArrayList(projectsInAnalysis);
	}

	/**
	 * If the sort properties are empty, sort by default on the CREATED_DATE
	 * property.
	 *
	 * @param sortProperties the sort properties to check
	 * @return the created date property if no sort properties specified,
	 * otherwise just return the sort properties.
	 */
	private static final String[] getOrDefaultSortProperties(final String... sortProperties) {
		if (sortProperties == null || sortProperties.length == 0) {
			return new String[] { CREATED_DATE_SORT_PROPERTY };
		} else {
			return sortProperties;
		}
	}

	/**
	 * If the {@link Sort} is null create a default {@link Sort} for the data.
	 *
	 * @param sort {@link Sort} for the data
	 * @return the create {@link Sort} if none was defined, otherwise just return the original {@link Sort}
	 */
	private static final Sort getOrDefaultSort(Sort sort) {
		if (sort == null) {
			sort = new Sort(Direction.ASC, CREATED_DATE_SORT_PROPERTY);
		}
		return sort;
	}

	/**
	 * Get a {@link ProjectUserJoin} where the user has a given role
	 *
	 * @param projectRole The {@link ProjectRole} to search for.
	 * @param user        The user to search
	 * @return a {@link Specification} to search for {@link Project} where the
	 * specified {@link User} has a certain {@link ProjectRole}.
	 */
	private static final Specification<ProjectUserJoin> getProjectJoinsWithRole(User user, ProjectRole projectRole) {
		return new Specification<ProjectUserJoin>() {
			@Override
			public Predicate toPredicate(Root<ProjectUserJoin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("projectRole"), projectRole), cb.equal(root.get("user"), user));
			}
		};
	}

	/**
	 * Search for projects using a few different types of search fields.
	 *
	 * @param allFields          the search criteria to apply to all fields
	 * @param projectNameFilter  the filter to apply specifically to project name
	 * @param organismNameFilter the filter to apply specifically to organism name
	 * @param user               the filter to apply for user filtering
	 * @return the specification
	 */
	private static final Specification<Project> searchForProjects(final String allFields,
			final String projectNameFilter, final String organismNameFilter, final User user) {
		return new Specification<Project>() {

			/**
			 * This {@link Predicate} considers *all* fields on a
			 * {@link Project} with an OR filter.
			 *
			 * @param root
			 *            the root of the query
			 * @param query
			 *            the query
			 * @param cb
			 *            the builder
			 * @return a {@link Predicate} that covers all fields with OR.
			 */
			private Predicate allFieldsPredicate(final Root<Project> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
				final List<Predicate> allFieldsPredicates = new ArrayList<>();
				allFieldsPredicates.add(cb.like(root.get("name"), "%" + allFields + "%"));
				allFieldsPredicates.add(cb.like(root.get("organism"), "%" + allFields + "%"));
				allFieldsPredicates.add(cb.like(root.get("id")
						.as(String.class), "%" + allFields + "%"));
				return cb.or(allFieldsPredicates.toArray(new Predicate[0]));
			}

			/**
			 * This {@link Predicate} considers each specific field on
			 * {@link Project} separately and joins them with an AND filter.
			 *
			 * @param root
			 *            the root of the query
			 * @param query
			 *            the query
			 * @param cb
			 *            the builder
			 * @return a {@link Predicate} that covers individual fields with an
			 *         AND.
			 */
			private Predicate specificFiltersPredicate(final Root<Project> root, final CriteriaQuery<?> query,
					final CriteriaBuilder cb) {
				final List<Predicate> filterPredicates = new ArrayList<>();
				if (!StringUtils.isEmpty(projectNameFilter)) {
					filterPredicates.add(cb.like(root.get("name"), "%" + projectNameFilter + "%"));
				}
				if (!StringUtils.isEmpty(organismNameFilter)) {
					filterPredicates.add(cb.like(root.get("organism"), "%" + organismNameFilter + "%"));
				}

				return cb.and(filterPredicates.toArray(new Predicate[0]));
			}

			/**
			 * This {@link Predicate} filters out {@link Project}s for the
			 * specific user where they are assigned individually as a member.
			 *
			 * @param root
			 *            the root of the query
			 * @param query
			 *            the query
			 * @param cb
			 *            the builder
			 * @return a {@link Predicate} that filters {@link Project}s where
			 *         users are individually assigned.
			 */
			private Predicate individualProjectMembership(final Root<Project> root, final CriteriaQuery<?> query,
					final CriteriaBuilder cb) {
				final Subquery<Long> userMemberSelect = query.subquery(Long.class);
				final Root<ProjectUserJoin> userMemberJoin = userMemberSelect.from(ProjectUserJoin.class);
				userMemberSelect.select(userMemberJoin.get("project")
						.get("id"))
						.where(cb.equal(userMemberJoin.get("user"), user));
				return cb.in(root.get("id"))
						.value(userMemberSelect);
			}

			/**
			 * This {@link Predicate} filters out {@link Project}s for the
			 * specific user where they are assigned transitively through a
			 * {@link UserGroup}.
			 *
			 * @param root
			 *            the root of the query
			 * @param query
			 *            the query
			 * @param cb
			 *            the builder
			 * @return a {@link Predicate} that filters {@link Project}s where
			 *         users are assigned transitively through {@link UserGroup}
			 *         .
			 */
			private Predicate groupProjectMembership(final Root<Project> root, final CriteriaQuery<?> query,
					final CriteriaBuilder cb) {
				final Subquery<Long> groupMemberSelect = query.subquery(Long.class);
				final Root<UserGroupProjectJoin> groupMemberJoin = groupMemberSelect.from(UserGroupProjectJoin.class);
				groupMemberSelect.select(groupMemberJoin.get("project")
						.get("id"))
						.where(cb.equal(groupMemberJoin.join("userGroup")
								.join("users")
								.get("user"), user));
				return cb.in(root.get("id"))
						.value(groupMemberSelect);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Predicate toPredicate(final Root<Project> root, final CriteriaQuery<?> query,
					final CriteriaBuilder cb) {
				final Predicate allFieldsPredicate = allFieldsPredicate(root, query, cb);
				final Predicate specificFiltersPredicate = specificFiltersPredicate(root, query, cb);

				final Predicate projectMember = cb.or(individualProjectMembership(root, query, cb),
						groupProjectMembership(root, query, cb));
				if (user != null) {
					return cb.and(allFieldsPredicate, specificFiltersPredicate, projectMember);
				} else {
					return cb.and(allFieldsPredicate, specificFiltersPredicate);
				}
			}
		};
	}
}