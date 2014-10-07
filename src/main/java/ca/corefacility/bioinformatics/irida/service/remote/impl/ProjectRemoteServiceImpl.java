package ca.corefacility.bioinformatics.irida.service.remote.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

/**
 * Remote service for retrieving {@link RemoteProject}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Service
public class ProjectRemoteServiceImpl extends RemoteServiceImpl<RemoteProject> implements ProjectRemoteService {

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
	public RemoteProject read(RemoteRelatedProject project) {
		String remoteProjectURI = project.getRemoteProjectURI();
		RemoteAPI remoteAPI = project.getRemoteAPI();

		return super.read(remoteProjectURI, remoteAPI);
	}
}
