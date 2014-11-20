package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.List;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;

/**
 * Implementation of {@link ProjectEventService} using a
 * {@link ProjectEventRepository}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Service
public class ProjectEventServiceImpl extends CRUDServiceImpl<Long, ProjectEvent> implements ProjectEventService {

	private ProjectEventRepository repository;

	@Autowired
	public ProjectEventServiceImpl(ProjectEventRepository repository, Validator validator) {
		super(repository, validator, ProjectEvent.class);
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ProjectEvent> getEventsForProject(Project project) {
		return repository.getEventsForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ProjectEvent> getEventsForUser(User user) {
		return repository.getEventsForUser(user);
	}

}
