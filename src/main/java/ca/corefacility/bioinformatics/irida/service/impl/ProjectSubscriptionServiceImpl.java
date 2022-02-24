package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectSubscriptionRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectSubscriptionService;

/**
 * A specialized service layer for project subscriptions.
 */
@Service
public class ProjectSubscriptionServiceImpl extends CRUDServiceImpl<Long, ProjectSubscription>
		implements ProjectSubscriptionService {

	private final ProjectSubscriptionRepository projectSubscriptionRepository;

	@Autowired
	public ProjectSubscriptionServiceImpl(ProjectSubscriptionRepository projectSubscriptionRepository,
			Validator validator) {
		super(projectSubscriptionRepository, validator, ProjectSubscription.class);
		this.projectSubscriptionRepository = projectSubscriptionRepository;
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
}
