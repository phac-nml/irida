package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Collection;
import java.util.List;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectSubscriptionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectSubscriptionService;

/**
 * A specialized service layer for project subscriptions.
 */
@Service
public class ProjectSubscriptionServiceImpl extends CRUDServiceImpl<Long, ProjectSubscription>
		implements ProjectSubscriptionService {

	private final ProjectSubscriptionRepository projectSubscriptionRepository;
	private final ProjectUserJoinRepository pujRepository;
	private final UserGroupProjectJoinRepository ugpjRepository;

	@Autowired
	public ProjectSubscriptionServiceImpl(ProjectSubscriptionRepository projectSubscriptionRepository,
			ProjectUserJoinRepository pujRepository, UserGroupProjectJoinRepository ugpjRepository,
			Validator validator) {
		super(projectSubscriptionRepository, validator, ProjectSubscription.class);
		this.projectSubscriptionRepository = projectSubscriptionRepository;
		this.pujRepository = pujRepository;
		this.ugpjRepository = ugpjRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public ProjectSubscription read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<ProjectSubscription> list(int page, int size, Sort sort) throws IllegalArgumentException {
		return super.list(page, size, sort);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<ProjectSubscription> getProjectSubscriptionsForUser(User user, int page, int size, Sort sort) {
		PageRequest pr = PageRequest.of(page, size, sort);
		return projectSubscriptionRepository.findAllProjectSubscriptionsByUser(user, pr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public ProjectSubscription update(ProjectSubscription projectSubscription) {
		return super.update(projectSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<User> getUsersWithEmailSubscriptions() {
		return projectSubscriptionRepository.getUsersWithSubscriptions();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<Project> getProjectsForUserWithEmailSubscriptions(User user) {
		return projectSubscriptionRepository.getProjectsForUserWithSubscriptions(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize(
			"hasRole('ROLE_ADMIN') or hasPermission(#project, 'canManageLocalProjectSettings')")
	public ProjectSubscription addProjectSubscriptionForProjectAndUser(Project project, User user) {
		return addProjectSubscription(project, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize(
			"hasRole('ROLE_ADMIN') or hasPermission(#project, 'canManageLocalProjectSettings')")
	public void removeProjectSubscriptionForProjectAndUser(Project project, User user) {
		removeProjectSubscription(project, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize(
			"hasRole('ROLE_ADMIN') or hasPermission(#project, 'canManageLocalProjectSettings') or hasPermission(#userGroup, 'canUpdateUserGroup')")
	public ProjectSubscription addProjectSubscriptionsForUserInUserGroup(Project project, User user,
			UserGroup userGroup) {
		return addProjectSubscription(project, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize(
			"hasRole('ROLE_ADMIN') or hasPermission(#project, 'canManageLocalProjectSettings') or hasPermission(#userGroup, 'canUpdateUserGroup')")
	public void removeProjectSubscriptionsForUserInUserGroup(Project project, User user, UserGroup userGroup) {
		removeProjectSubscription(project, user);
	}

	/**
	 * Add a project subscription for the user
	 *
	 * @param project The {@link Project}
	 * @param user    The {@link User}
	 * @return the {@link ProjectSubscription} created for the user
	 */
	private ProjectSubscription addProjectSubscription(Project project, User user) {
		ProjectSubscription newProjectSubscription = null;
		ProjectSubscription projectSubscription = projectSubscriptionRepository.findProjectSubscriptionByUserAndProject(
				user, project);
		if (projectSubscription == null) {
			newProjectSubscription = new ProjectSubscription(user, project, false);
			projectSubscriptionRepository.save(newProjectSubscription);
		}
		return newProjectSubscription;
	}

	/**
	 * Remove project subscription for the user
	 *
	 * @param project The {@link Project}
	 * @param user    The {@link User}
	 */
	private void removeProjectSubscription(Project project, User user) {
		ProjectUserJoin projectUserjoin = pujRepository.getProjectJoinForUser(project, user);
		Collection<UserGroupProjectJoin> userGroupProjects = ugpjRepository.findByProjectAndUser(project, user);

		if ((projectUserjoin != null && userGroupProjects.isEmpty()) || (projectUserjoin == null
				&& userGroupProjects.size() == 1)) {
			ProjectSubscription projectSubscription = projectSubscriptionRepository.findProjectSubscriptionByUserAndProject(
					user, project);
			if (projectSubscription != null) {
				projectSubscriptionRepository.delete(projectSubscription);
			}
		}
	}
}
