package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import ca.corefacility.bioinformatics.irida.model.user.Role;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.ria.web.components.AnalysisOutputFileDownloadManager;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAnalysesOutputsService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class UIAnalysesOutputsServiceTest {

	private final User USER_1 = new User(1L, "user1", "user1@nowhere.com", "SDF123", "USER", "ONE", "7777");

	private final Long PROJECT_ID = 1L;
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;
	private UserService userService;
	private AnalysisOutputFileDownloadManager analysisOutputFileDownloadManager;
	private Principal principal;
	private UIAnalysesOutputsService uiProjectAnalysesService;

	@BeforeEach
	public void setUp() {
		analysisSubmissionService = mock(AnalysisSubmissionService.class);
		workflowsService = mock(IridaWorkflowsService.class);
		userService = mock(UserService.class);
		analysisOutputFileDownloadManager = mock(AnalysisOutputFileDownloadManager.class);
		principal = mock(Principal.class);

		uiProjectAnalysesService = new UIAnalysesOutputsService(analysisSubmissionService, workflowsService,
				userService, analysisOutputFileDownloadManager);

		/*
		Mock the principal user
		 */
		USER_1.setSystemRole(Role.ROLE_ADMIN);
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal()).thenReturn(USER_1);
	}

	@Test
	public void getSharedSingleAnalysisOutputs() {
		List<ProjectSampleAnalysisOutputInfo> projectSampleAnalysisOutputInfos = uiProjectAnalysesService.getSharedSingleSampleOutputs(
				PROJECT_ID);
		verify(analysisSubmissionService, times(1)).getAllAnalysisOutputInfoSharedWithProject(PROJECT_ID);
		assertTrue(projectSampleAnalysisOutputInfos.getClass().equals(ArrayList.class),
				"Returns a list of ProjectSampleAnalysisOutputInfo objects");
	}

	@Test
	public void getAutomatedSingleAnalysisOutputs() {
		List<ProjectSampleAnalysisOutputInfo> projectSampleAnalysisOutputInfos = uiProjectAnalysesService.getAutomatedSingleSampleOutputs(
				PROJECT_ID);
		verify(analysisSubmissionService, times(1)).getAllAutomatedAnalysisOutputInfoForAProject(PROJECT_ID);
		assertTrue(projectSampleAnalysisOutputInfos.getClass().equals(ArrayList.class),
				"Returns a list of ProjectSampleAnalysisOutputInfo objects");
	}

	@Test
	public void getUserSingleAnalysisOutputs() {
		List<ProjectSampleAnalysisOutputInfo> userProjectSampleAnalysisOutputInfos = uiProjectAnalysesService.getUserSingleSampleOutputs();

		verify(userService, times(1)).getUserByUsername(principal.getName());

		verify(analysisSubmissionService, times(1)).getAllUserAnalysisOutputInfo(
				userService.getUserByUsername(principal.getName()));
		assertTrue(userProjectSampleAnalysisOutputInfos.getClass().equals(ArrayList.class),
				"Returns a list of ProjectSampleAnalysisOutputInfo objects");
	}

}
