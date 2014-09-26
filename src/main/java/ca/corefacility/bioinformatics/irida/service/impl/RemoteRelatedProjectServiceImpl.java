package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.List;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.repositories.RemoteRelatedProjectRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;

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
	public List<RemoteRelatedProject> getRemoteProjectsForProject(Project project) {
		return repository.getRemoteRelatedProjectsForProject(project);
	}

}
