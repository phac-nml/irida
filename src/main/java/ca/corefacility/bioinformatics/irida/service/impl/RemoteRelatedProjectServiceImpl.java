package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.repositories.RemoteRelatedProjectRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;

/**
 * Implementation of service for managing {@link RemoteRelatedProject}s
 * 
 *
 */
@Service
public class RemoteRelatedProjectServiceImpl extends CRUDServiceImpl<Long, RemoteRelatedProject> implements
		RemoteRelatedProjectService {

	RemoteRelatedProjectRepository repository;

	@Autowired
	public RemoteRelatedProjectServiceImpl(RemoteRelatedProjectRepository repository, Validator validator) {
		super(repository, validator, RemoteRelatedProject.class);
		this.repository = repository;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public RemoteRelatedProject create(@Valid RemoteRelatedProject object) throws EntityExistsException, ConstraintViolationException {
		return super.create(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public List<RemoteRelatedProject> getRemoteProjectsForProject(Project project) {
		return repository.getRemoteRelatedProjectsForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public RemoteRelatedProject getRemoteRelatedProjectForProjectAndURI(Project project, String remoteProjectURI) {
		RemoteRelatedProject remoteRelatedProjectForProjectAndURI = repository.getRemoteRelatedProjectForProjectAndURI(
				project, remoteProjectURI);
		if (remoteRelatedProjectForProjectAndURI == null) {
			throw new EntityNotFoundException("No RemoteRelatedProject exists for this project and URI");
		}
		return remoteRelatedProjectForProjectAndURI;
	}

}
