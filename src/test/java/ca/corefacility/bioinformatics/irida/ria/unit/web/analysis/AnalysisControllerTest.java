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

	@Before
	public void init() {
		analysisSubmissionServiceMock = mock(AnalysisSubmissionService.class);
		userServiceMock = mock(UserService.class);
		MessageSource messageSourceMock = mock(MessageSource.class);
		analysisController = new AnalysisController(analysisSubmissionServiceMock, userServiceMock, messageSourceMock);
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
	@SuppressWarnings("unchecked")
	public void testGetAjaxListAllAnalysis() throws IOException {
		int countParam = 10;
		int pageParam = 0;
		String sortDirParam = "desc";
		String sortedByParam = "createdDate";

		AnalysisSubmission analysisSubmission1 = TestDataFactory.constructAnalysisSubmission();
		AnalysisSubmission analysisSubmission2 = TestDataFactory.constructAnalysisSubmission();
		ImmutableList<AnalysisSubmission> analysisList = ImmutableList.of(analysisSubmission1, analysisSubmission2);
		Page<AnalysisSubmission> analysisSubmissionPage = new PageImpl<>(analysisList);

		when(analysisSubmissionServiceMock
				.search(any(Specification.class), eq(0), eq(10), eq(Sort.Direction.DESC), eq("createdDate"))).thenReturn(analysisSubmissionPage);
		Map<String, Object> map = analysisController.getAjaxListAllAnalysis(pageParam, countParam, sortedByParam, sortDirParam, null, null, null, null);

		assertTrue(map.containsKey("analysis"));
		assertTrue(map.containsKey("totalAnalysis"));
		assertTrue(map.containsKey("totalPages"));

		// Make sure all the analysis were added.
		List<Object> analysis = (List<Object>) map.get("analysis");
		assertTrue(analysisList.size() == analysis.size());
		for (int i = 0; i < analysis.size(); i++) {
			Object o = analysis.get(i);
			if (o instanceof Map) {
				Map<String, String> oMap = (HashMap<String, String>) o;
				AnalysisSubmission a = analysisList.get(i);
				assertEquals(oMap.get("id"), a.getId().toString());
				assertEquals(oMap.get("name"), a.getName());
			}
		}

		// Make sure there are the correct number of pages
		assertEquals("the correct number of pages", 1, map.get("totalPages"));

		// Make sure that the total is correctly set.
		assertEquals("total is correctly set.", (long) analysisList.size(), map.get("totalAnalysis"));
	}

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
