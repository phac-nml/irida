package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

/**
 * Remote service for retrieving {@link Project}s
 * 
 *
 */
@Service
public class ProjectRemoteServiceImpl extends RemoteServiceImpl<Project> implements ProjectRemoteService {
	// TODO: Get this information from the ProjectsController in the REST API
	// project when it is merged into this project. Issue #86
	public static final String PROJECTS_BOOKMARK = "/projects";

	/**
	 * Create a new {@link ProjectRemoteServiceImpl} that communicates with the
	 * given {@link ProjectRemoteRepository}
	 * 
	 * @param repository
	 *            the {@link ProjectRemoteRepository}
	 * @param apiRepository
	 *            Repository storing information about {@link RemoteAPI}s
	 */

	private ProjectRemoteRepository repository;

	@Autowired
	public ProjectRemoteServiceImpl(ProjectRemoteRepository repository, RemoteAPIRepository apiRepository) {
		super(repository, apiRepository);
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Project> listProjectsForAPI(RemoteAPI api) {
		String projectsHref = api.getServiceURI() + PROJECTS_BOOKMARK;

		return list(projectsHref, api);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getProjectHash(Project project) {
		return repository.readProjectHash(project);
	}
}
