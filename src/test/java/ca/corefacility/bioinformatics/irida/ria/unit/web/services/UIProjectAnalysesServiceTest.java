package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectAnalysesService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class UIProjectAnalysesServiceTest {

	private final Long PROJECT_ID = 1L;
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;
	private UIProjectAnalysesService uiProjectAnalysesService;

	@Before
	public void setUp() {
		analysisSubmissionService = mock(AnalysisSubmissionService.class);
		workflowsService = mock(IridaWorkflowsService.class);

		uiProjectAnalysesService = new UIProjectAnalysesService(analysisSubmissionService, workflowsService);
	}

	@Test
	public void getSharedSingleAnalysisOutputs() {
		List<ProjectSampleAnalysisOutputInfo> projectSampleAnalysisOutputInfos = uiProjectAnalysesService.getSharedSingleSampleOutputs(PROJECT_ID);
		verify(analysisSubmissionService, times(1)).getAllAnalysisOutputInfoSharedWithProject(PROJECT_ID);
		assertTrue("Returns a list of ProjectSampleAnalysisOutputInfo objects", projectSampleAnalysisOutputInfos.getClass().equals(ArrayList.class));
	}

	@Test
	public void getAutomatedSingleAnalysisOutputs() {
		List<ProjectSampleAnalysisOutputInfo> projectSampleAnalysisOutputInfos = uiProjectAnalysesService.getAutomatedSingleSampleOutputs(PROJECT_ID);
		verify(analysisSubmissionService, times(1)).getAllAutomatedAnalysisOutputInfoForAProject(PROJECT_ID);
		String s = projectSampleAnalysisOutputInfos.getClass().toString();
		assertTrue("Returns a list of ProjectSampleAnalysisOutputInfo objects", projectSampleAnalysisOutputInfos.getClass().equals(
				ArrayList.class));
	}

}
