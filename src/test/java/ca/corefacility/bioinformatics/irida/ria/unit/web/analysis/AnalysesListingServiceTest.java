package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.AnalysesListingService;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisTypesServiceImpl;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link AnalysesListingService} which handles DataTables call for listing Analyses.
 */
public class AnalysesListingServiceTest {
	private AnalysesListingService analysesListingService;

	private AnalysisSubmissionService analysisSubmissionService;

	@Before
	public void init() {
		analysisSubmissionService = mock(AnalysisSubmissionService.class);
		IridaWorkflowsService iridaWorkflowsService = mock(IridaWorkflowsService.class);
		UpdateAnalysisSubmissionPermission updateAnalysisPermission = mock(UpdateAnalysisSubmissionPermission.class);
		MessageSource messageSource = mock(MessageSource.class);
		analysesListingService = new AnalysesListingService(analysisSubmissionService, iridaWorkflowsService,
				updateAnalysisPermission, messageSource, new AnalysisTypesServiceImpl());
	}

	@Test
	public void testGetPagedSubmissionsForAdmin() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		String searchValue = "";
		DataTablesParams params = new DataTablesParams(1, 10, 1, searchValue, Sort.by(Sort.Direction.ASC, "id"),
				ImmutableMap.of());

		when(analysisSubmissionService
				.listAllSubmissions(eq(searchValue), any(String.class), eq(null), Matchers.any(),
						Matchers.any())).thenReturn(AnalysesDataFactory.getPagedAnalysisSubmissions());

		DataTablesResponse response = analysesListingService.getPagedSubmissions(params, Locale.US, null, null);

		assertEquals("DataTables response should have a draw value of 1", 1, response.getDraw());
		assertEquals("DataTables response should have a records filtered value of 150", 150,
				response.getRecordsFiltered());
		assertEquals("DataTables response should have a records total value of 150", 150, response.getRecordsTotal());
		assertTrue("Should have data value", response.getData() != null);

		verify(analysisSubmissionService)
				.listAllSubmissions(eq(searchValue), any(String.class), eq(null), Matchers.any(),
						Matchers.any());
	}

	@Test
	public void testGetPagedSubmissionsForUser() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		String searchValue = "";
		User user = new User();
		DataTablesParams params = new DataTablesParams(1, 10, 1, searchValue, Sort.by(Sort.Direction.ASC, "id"),
				ImmutableMap.of());

		when(analysisSubmissionService.listSubmissionsForUser(eq(searchValue), any(String.class), eq(null), eq(user),
				Matchers.any(), Matchers.any()))
				.thenReturn(AnalysesDataFactory.getPagedAnalysisSubmissions());

		DataTablesResponse response = analysesListingService.getPagedSubmissions(params, Locale.US, user, null);

		assertEquals("DataTables response should have a draw value of 1", 1, response.getDraw());
		assertEquals("DataTables response should have a records filtered value of 150", 150,
				response.getRecordsFiltered());
		assertEquals("DataTables response should have a records total value of 150", 150, response.getRecordsTotal());
		assertTrue("Should have data value", response.getData() != null);

		verify(analysisSubmissionService).listSubmissionsForUser(eq(searchValue), any(String.class), eq(null), eq(user),
				Matchers.any(), Matchers.any());
	}

	@Test
	public void testGetPagedSubmissionsForProject() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		String searchValue = "";
		Project project = new Project();
		DataTablesParams params = new DataTablesParams(1, 10, 1, searchValue, Sort.by(Sort.Direction.ASC, "id"),
				ImmutableMap.of());

		when(analysisSubmissionService
				.listSubmissionsForProject(eq(searchValue), any(String.class), eq(null), Matchers.any(),
						eq(project), Matchers.any()))
				.thenReturn(AnalysesDataFactory.getPagedAnalysisSubmissions());

		DataTablesResponse response = analysesListingService.getPagedSubmissions(params, Locale.US, null, project);

		assertEquals("DataTables response should have a draw value of 1", 1, response.getDraw());
		assertEquals("DataTables response should have a records filtered value of 150", 150,
				response.getRecordsFiltered());
		assertEquals("DataTables response should have a records total value of 150", 150, response.getRecordsTotal());
		assertTrue("Should have data value", response.getData() != null);

		verify(analysisSubmissionService)
				.listSubmissionsForProject(eq(searchValue), any(String.class), eq(null), Matchers.any(),
						eq(project), Matchers.any());
	}
}
