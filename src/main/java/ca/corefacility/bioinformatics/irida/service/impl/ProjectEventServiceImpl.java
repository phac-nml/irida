package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;

/**
 * Implementation of {@link ProjectEventService} using a {@link ProjectEventRepository}
 */
@Service
public class ProjectEventServiceImpl extends CRUDServiceImpl<Long, ProjectEvent> implements ProjectEventService {

	private ProjectEventRepository repository;
	private ProjectUserJoinRepository pujRepository;
	private UserGroupProjectJoinRepository ugpjRepository;

	@Autowired
	public ProjectEventServiceImpl(ProjectEventRepository repository, ProjectUserJoinRepository pujRepository,
			UserGroupProjectJoinRepository ugpjRepository, Validator validator) {
		super(repository, validator, ProjectEvent.class);
		this.repository = repository;
		this.pujRepository = pujRepository;
		this.ugpjRepository = ugpjRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Page<ProjectEvent> getEventsForProject(Project project, Pageable pageable) {
		return repository.getEventsForProject(project, pageable);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or principal.username == #user.username")
	public Page<ProjectEvent> getEventsForUser(User user, Pageable pageable) {
		List<Project> projects = getProjectsForUser(user);
		return repository.getEventsForProjects(projects, pageable);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Page<ProjectEvent> getAllProjectsEvents(Pageable pageable) {
		return repository.getAllProjectsEvents(pageable);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public List<ProjectEvent> getEventsForUserAfterDate(User user, Date beginning) {
		List<Project> projects = getProjectsForUser(user);
		List<ProjectEvent> eventsForUserAfterDate = repository.getEventsForProjectsAfterDate(projects, beginning);

		return eventsForUserAfterDate;
	}

	private List<Project> getProjectsForUser(User user) {
		final List<Project> userJoinProjects = pujRepository.getProjectsForUser(user)
				.stream()
				.map(Join::getSubject)
				.collect(Collectors.toList());
		final List<Project> groupJoinProjects = ugpjRepository.findProjectsByUser(user)
				.stream()
				.map(Join::getSubject)
				.collect(Collectors.toList());

		List<Project> projects = new ArrayList<Project>();
		projects.addAll(userJoinProjects);
		projects.addAll(groupJoinProjects);

		return projects;
	}
}
