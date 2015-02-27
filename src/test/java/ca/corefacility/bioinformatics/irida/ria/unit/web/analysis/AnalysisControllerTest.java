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
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
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
	private UserService userServiceMock;
	private IridaWorkflowsService iridaWorkflowsServiceMock;

	@Before
	public void init() {
		analysisSubmissionServiceMock = mock(AnalysisSubmissionService.class);
		userServiceMock = mock(UserService.class);
		iridaWorkflowsServiceMock = mock(IridaWorkflowsService.class);
		MessageSource messageSourceMock = mock(MessageSource.class);
		analysisController = new AnalysisController(analysisSubmissionServiceMock, userServiceMock, iridaWorkflowsServiceMock, messageSourceMock);
	}

	// ************************************************************************************************
	// PAGE TESTS
	// ************************************************************************************************

	@Test
	public void testGetTreeAnalysis() throws IOException {
		Long id = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		AnalysisSubmission analysisSubmission = TestDataFactory.constructAnalysisSubmission();

		when(analysisSubmissionServiceMock.read(id)).thenReturn(analysisSubmission);
		analysisController.getTreeAnalysis(id, model);
		assertTrue(model.containsAttribute("analysis"));
		assertTrue(model.containsAttribute("analysisSubmission"));
		assertTrue(model.containsAttribute("newick"));
		Path path = Paths.get(TestDataFactory.FAKE_FILE_PATH.replace("{name}", "snp_tree.tree"));
		List<String> lines = Files.readAllLines(path);
		assertEquals(lines.get(0), model.get("newick"));
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
