package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.ria.web.components.AnalysisOutputFileDownloadManager;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAnalysesOutputsService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class UIAnalysesOutputsServiceTest {

	private final Long PROJECT_ID = 1L;
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;
	private UserService userService;
	private AnalysisOutputFileDownloadManager analysisOutputFileDownloadManager;
	private Principal principal;
	private UIAnalysesOutputsService uiProjectAnalysesService;

	@Before
	public void setUp() {
		analysisSubmissionService = mock(AnalysisSubmissionService.class);
		workflowsService = mock(IridaWorkflowsService.class);
		userService = mock(UserService.class);
		analysisOutputFileDownloadManager = mock(AnalysisOutputFileDownloadManager.class);
		principal = mock(Principal.class);

		uiProjectAnalysesService = new UIAnalysesOutputsService(analysisSubmissionService, workflowsService,
				userService, analysisOutputFileDownloadManager);
	}

	@Test
	public void getSharedSingleAnalysisOutputs() {
		List<ProjectSampleAnalysisOutputInfo> projectSampleAnalysisOutputInfos = uiProjectAnalysesService.getSharedSingleSampleOutputs(
				PROJECT_ID);
		verify(analysisSubmissionService, times(1)).getAllAnalysisOutputInfoSharedWithProject(PROJECT_ID);
		assertTrue("Returns a list of ProjectSampleAnalysisOutputInfo objects",
				projectSampleAnalysisOutputInfos.getClass()
						.equals(ArrayList.class));
	}

	@Test
	public void getAutomatedSingleAnalysisOutputs() {
		List<ProjectSampleAnalysisOutputInfo> projectSampleAnalysisOutputInfos = uiProjectAnalysesService.getAutomatedSingleSampleOutputs(
				PROJECT_ID);
		verify(analysisSubmissionService, times(1)).getAllAutomatedAnalysisOutputInfoForAProject(PROJECT_ID);
		assertTrue("Returns a list of ProjectSampleAnalysisOutputInfo objects",
				projectSampleAnalysisOutputInfos.getClass()
						.equals(ArrayList.class));
	}

	@Test
	public void getUserSingleAnalysisOutputs() {
		User user = userService.getUserByUsername(principal.getName());
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		List<ProjectSampleAnalysisOutputInfo> userProjectSampleAnalysisOutputInfos = uiProjectAnalysesService.getUserSingleSampleOutputs();

		verify(userService, times(1)).getUserByUsername(principal.getName());

		verify(analysisSubmissionService, times(1)).getAllUserAnalysisOutputInfo(
				userService.getUserByUsername(principal.getName()));
		assertTrue("Returns a list of ProjectSampleAnalysisOutputInfo objects",
				userProjectSampleAnalysisOutputInfos.getClass()
						.equals(ArrayList.class));
	}

}
