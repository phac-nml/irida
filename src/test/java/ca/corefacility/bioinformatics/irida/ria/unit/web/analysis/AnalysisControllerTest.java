package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
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

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.AnalysisController;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;

import com.google.common.collect.ImmutableList;

/**
 * Created by josh on 14-09-04.
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
	private MessageSource messageSourceMock;

	@Before
	public void init() {
		analysisSubmissionServiceMock = mock(AnalysisSubmissionService.class);
		messageSourceMock = mock(MessageSource.class);
		analysisController = new AnalysisController(analysisSubmissionServiceMock, messageSourceMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetAjaxListAllAnalysis() throws IOException {
		int countParam = 10;
		int pageParam = 1;
		String sortDirParam = "desc";
		String sortedByParam = "createdDate";

		AnalysisSubmission analysisSubmission1 = TestDataFactory.constrctAnalysisSubmission();
		AnalysisSubmission analysisSubmission2 = TestDataFactory.constrctAnalysisSubmission();
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
}
