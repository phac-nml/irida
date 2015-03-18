package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
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
	 */
	@Autowired
	public ProjectRemoteServiceImpl(ProjectRemoteRepository repository) {
		super(repository);
	}

	/**
	 * Read a {@link RemoteProject} for a given {@link RemoteRelatedProject}
	 * reference
	 * 
	 * @param project
	 *            The {@link RemoteRelatedProject} to read
	 * @return a {@link RemoteProject}
	 */
	public Project read(RemoteRelatedProject project) {
		String remoteProjectURI = project.getRemoteProjectURI();
		RemoteAPI remoteAPI = project.getRemoteAPI();

		return super.read(remoteProjectURI, remoteAPI);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Project> listProjectsForAPI(RemoteAPI api) {
		String projectsHref = api.getServiceURI() + PROJECTS_BOOKMARK;

		return list(projectsHref, api);
	}
}
