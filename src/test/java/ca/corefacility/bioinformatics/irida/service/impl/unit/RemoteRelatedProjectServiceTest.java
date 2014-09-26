package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.util.List;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.repositories.RemoteRelatedProjectRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.impl.RemoteRelatedProjectServiceImpl;

public class RemoteRelatedProjectServiceTest {
	private Validator validator;
	private RemoteRelatedProjectRepository repository;
	private RemoteRelatedProjectService service;

	@Before
	public void setUp() {
		repository = mock(RemoteRelatedProjectRepository.class);
		service = new RemoteRelatedProjectServiceImpl(repository, validator);
	}

	@Test
	public void testGetRemoteProjectsForProject() {
		Project p = new Project();
		List<RemoteRelatedProject> projects = Lists
				.newArrayList(new RemoteRelatedProject(), new RemoteRelatedProject());
		when(repository.getRemoteRelatedProjectsForProject(p)).thenReturn(projects);

		List<RemoteRelatedProject> remoteProjectsForProject = service.getRemoteProjectsForProject(p);

		assertEquals(projects, remoteProjectsForProject);
		verify(repository).getRemoteRelatedProjectsForProject(p);
	}
}
