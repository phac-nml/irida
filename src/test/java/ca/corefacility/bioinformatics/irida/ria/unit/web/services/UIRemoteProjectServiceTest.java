package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.security.Principal;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote.RemoteProjectSettingsUpdateRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIRemoteProjectService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import static org.mockito.Mockito.*;

public class UIRemoteProjectServiceTest {
	private MessageSource messageSource;
	private ProjectService projectService;
	private ProjectRemoteService projectRemoteService;
	private UserService userService;
	private UIRemoteProjectService uiRemoteProjectService;
	private Principal principal;
	private RemoteStatus remoteStatus;
	Project remoteProject = TestDataFactory.constructProject();

	private final Locale LOCALE = Locale.CANADA;
	private final Long NONEXISTENTPROJECTID = 12L;
	private RemoteProjectSettingsUpdateRequest remoteProjectSettingsUpdateRequest;

	@Before
	public void setUp() {
		messageSource = mock(MessageSource.class);
		projectService = mock(ProjectService.class);
		projectRemoteService = mock(ProjectRemoteService.class);
		userService = mock(UserService.class);
		MessageSource messageSource = mock(MessageSource.class);
		principal = mock(Principal.class);
		remoteStatus = mock(RemoteStatus.class);

		uiRemoteProjectService = new UIRemoteProjectService(messageSource, projectService, projectRemoteService,
				userService);

		remoteProjectSettingsUpdateRequest = new RemoteProjectSettingsUpdateRequest(
				false, false, ProjectSyncFrequency.NEVER);

		remoteProject.setId(2L);
		remoteProject.setRemoteStatus(remoteStatus);

		when(projectService.read(remoteProject.getId())).thenReturn(remoteProject);
	}

	@Test(expected = Exception.class)
	public void testGetRemoteProjectSyncSettingsEntityNotFound() throws Exception {
		uiRemoteProjectService.getProjectRemoteSettings(NONEXISTENTPROJECTID, LOCALE);
		verify(projectService, times(1)).read(NONEXISTENTPROJECTID);

	}

	@Test(expected = Exception.class)
	public void testUpdateRemoteProjectSyncSettingsEntityNotFound() throws Exception {
		uiRemoteProjectService.updateProjectSyncSettings(NONEXISTENTPROJECTID, remoteProjectSettingsUpdateRequest,
				principal, LOCALE);
		verify(projectService, times(1)).read(NONEXISTENTPROJECTID);
	}

	@Test
	public void testGetRemoteProjectSyncSettings() throws Exception {
		uiRemoteProjectService.getProjectRemoteSettings(remoteProject.getId(), LOCALE);
		verify(projectService, times(1)).read(remoteProject.getId());
	}

	@Test
	public void testUpdateRemoteProjectSyncSettingsDefault() throws Exception {
		uiRemoteProjectService.updateProjectSyncSettings(remoteProject.getId(), remoteProjectSettingsUpdateRequest,
				principal, LOCALE);
		verify(projectService, times(1)).read(remoteProject.getId());
		verify(userService, times(0)).getUserByUsername(principal.getName());
		verify(projectRemoteService, times(0)).read(remoteProject.getRemoteStatus()
				.getURL());
	}

	@Test
	public void testUpdateRemoteProjectSyncSettingsChangeUser() throws Exception {
		remoteProjectSettingsUpdateRequest.setChangeUser(true);
		uiRemoteProjectService.updateProjectSyncSettings(remoteProject.getId(), remoteProjectSettingsUpdateRequest,
				principal, LOCALE);
		verify(projectService, times(1)).read(remoteProject.getId());
		verify(userService, times(1)).getUserByUsername(principal.getName());
		verify(projectRemoteService, times(1)).read(remoteProject.getRemoteStatus()
				.getURL());

	}


}
