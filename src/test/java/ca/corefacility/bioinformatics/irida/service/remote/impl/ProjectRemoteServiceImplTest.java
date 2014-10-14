package ca.corefacility.bioinformatics.irida.service.remote.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteIRIDARoot;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLink;
import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteIRIDARootRepository;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

import com.google.common.collect.Lists;

public class ProjectRemoteServiceImplTest {
	ProjectRemoteService service;
	ProjectRemoteRepository repository;
	RemoteIRIDARootRepository rootRepository;

	@Before
	public void setUp() {
		repository = mock(ProjectRemoteRepository.class);
		rootRepository = mock(RemoteIRIDARootRepository.class);
		service = new ProjectRemoteServiceImpl(repository, rootRepository);
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

	@Test
	public void testListProjectsForAPI() {
		RemoteAPI api = new RemoteAPI();
		RemoteIRIDARoot root = new RemoteIRIDARoot();
		String projecsRel = "http://somewhere/projects";
		root.setLinks(Lists.newArrayList(new RESTLink("projects", projecsRel)));
		when(rootRepository.read(api)).thenReturn(root);

		service.listProjectsForAPI(api);

		verify(repository).list(projecsRel, api);
	}
}
