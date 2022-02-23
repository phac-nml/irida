package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.exceptions.LinkNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectRemoteRepositoryImplTest {

	private ProjectRemoteRepository projectRemoteRepository;
	private RemoteAPITokenService tokenService;
	private static final String PROJECT_HASH_REL = RESTProjectsController.PROJECT_HASH_REL;

	@BeforeEach
	public void setUp() {
		tokenService = mock(RemoteAPITokenService.class);
		projectRemoteRepository = new ProjectRemoteRepositoryImpl(tokenService);
	}

	@Test
	public void testReadProjectHashWithoutProjectHashRel() {
		Project remoteProject = mock(Project.class);

		when(remoteProject.getLink(PROJECT_HASH_REL)).thenReturn(Optional.empty());

		assertThrows(LinkNotFoundException.class, () -> {
			projectRemoteRepository.readProjectHash(remoteProject);
		});
	}
}
