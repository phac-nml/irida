package ca.corefacility.bioinformatics.irida.service.remote.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteIRIDARootRepository;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

public class ProjectRemoteServiceImplTest {
	ProjectRemoteService service;
	ProjectRemoteRepository repository;
	RemoteIRIDARootRepository rootRepository;

	@Before
	public void setUp() {
		repository = mock(ProjectRemoteRepository.class);
		service = new ProjectRemoteServiceImpl(repository,rootRepository);
	}

	@Test
	public void testRead() {
		Project localProject = new Project("local project");
		RemoteAPI remoteAPI = new RemoteAPI();
		String remoteProjectURI = "http://somewhere";
		RemoteRelatedProject project = new RemoteRelatedProject(localProject, remoteAPI, remoteProjectURI);

		service.read(project);

		verify(repository).read(remoteProjectURI, remoteAPI);
	}
}
