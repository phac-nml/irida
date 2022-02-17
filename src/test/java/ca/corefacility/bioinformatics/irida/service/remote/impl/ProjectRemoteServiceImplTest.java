package ca.corefacility.bioinformatics.irida.service.remote.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

public class ProjectRemoteServiceImplTest {
	ProjectRemoteService service;
	ProjectRemoteRepository repository;
	RemoteAPIRepository apiRepo;

	@BeforeEach
	public void setUp() {
		repository = mock(ProjectRemoteRepository.class);
		apiRepo = mock(RemoteAPIRepository.class);
		service = new ProjectRemoteServiceImpl(repository, apiRepo);
	}

	@Test
	public void testListProjectsForAPI() {
		RemoteAPI api = new RemoteAPI();
		api.setServiceURI("http://somewhere/");
		String serviceURI = "http://somewhere/";
		String projecsRel = serviceURI + ProjectRemoteServiceImpl.PROJECTS_BOOKMARK;

		service.listProjectsForAPI(api);

		verify(repository).list(projecsRel, api);
	}
}
