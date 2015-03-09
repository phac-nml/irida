package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.AnalysisController;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.ImmutableList;

/**
 */
public class AnalysisControllerTest {
	/*
	 * CONTROLLER
	 */
	private AnalysisController analysisController;

	/*
	 * SERVICES
	 */
	private AnalysisSubmissionService analysisSubmissionServiceMock;
	private IridaWorkflowsService iridaWorkflowsServiceMock;

	@Before
	public void init() {
		analysisSubmissionServiceMock = mock(AnalysisSubmissionService.class);
		iridaWorkflowsServiceMock = mock(IridaWorkflowsService.class);
		MessageSource messageSourceMock = mock(MessageSource.class);
		analysisController = new AnalysisController(analysisSubmissionServiceMock, iridaWorkflowsServiceMock, messageSourceMock);
	}

	// ************************************************************************************************
	// AJAX TESTS
	// ************************************************************************************************

	@Test
	public void TestGetAjaxDownloadAnalysisSubmission() throws IOException {
		Long analysisSubmissionId = 1L;
		MockHttpServletResponse response = new MockHttpServletResponse();

		when(analysisSubmissionServiceMock.read(analysisSubmissionId)).thenReturn(TestDataFactory.constructAnalysisSubmission());
		analysisController.getAjaxDownloadAnalysisSubmission(analysisSubmissionId, response);
		assertEquals("Has the correct content type", "application/zip", response.getContentType());
		assertEquals("Has the correct 'Content-Disposition' headers", "attachment;filename=submission-5.zip", response.getHeader("Content-Disposition"));
	}
}
