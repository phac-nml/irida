package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link AnalysesListingService} which handles DataTables call for listing Analyses.
 */
public class AnalysesListingServiceTest {
	private AnalysesListingService analysesListingService;

	private AnalysisSubmissionService analysisSubmissionService;

	@BeforeEach
	public void init() {
		analysisSubmissionService = mock(AnalysisSubmissionService.class);
		IridaWorkflowsService iridaWorkflowsService = mock(IridaWorkflowsService.class);
		UpdateAnalysisSubmissionPermission updateAnalysisPermission = mock(UpdateAnalysisSubmissionPermission.class);
		MessageSource messageSource = mock(MessageSource.class);

		AnalysisTypesServiceImpl analysisTypesService = new AnalysisTypesServiceImpl();
		analysisTypesService.registerDefaultTypes();
		analysesListingService = new AnalysesListingService(analysisSubmissionService, iridaWorkflowsService,
				updateAnalysisPermission, messageSource, analysisTypesService);
	}

	@Test
	public void testGetPagedSubmissionsForAdmin() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		String searchValue = "";
		DataTablesParams params = new DataTablesParams(1, 10, 1, searchValue, Sort.by(Sort.Direction.ASC, "id"),
				ImmutableMap.of());

		when(analysisSubmissionService
				.listAllSubmissions(eq(searchValue), isNull(), isNull(), isNull(),
						any())).thenReturn(AnalysesDataFactory.getPagedAnalysisSubmissions());

		DataTablesResponse response = analysesListingService.getPagedSubmissions(params, Locale.US, null, null);

		assertEquals(1, response.getDraw(), "DataTables response should have a draw value of 1");
		assertEquals(150, response.getRecordsFiltered(),
				"DataTables response should have a records filtered value of 150");
		assertEquals(150, response.getRecordsTotal(), "DataTables response should have a records total value of 150");
		assertTrue(response.getData() != null, "Should have data value");

		verify(analysisSubmissionService)
				.listAllSubmissions(eq(searchValue), isNull(), isNull(), isNull(),
						any());
	}

	@Test
	public void testGetPagedSubmissionsForUser() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		String searchValue = "";
		User user = new User();
		DataTablesParams params = new DataTablesParams(1, 10, 1, searchValue, Sort.by(Sort.Direction.ASC, "id"),
				ImmutableMap.of());

		when(analysisSubmissionService.listSubmissionsForUser(eq(searchValue), isNull(), isNull(), eq(user),
				any(), any()))
				.thenReturn(AnalysesDataFactory.getPagedAnalysisSubmissions());

		DataTablesResponse response = analysesListingService.getPagedSubmissions(params, Locale.US, user, null);

		assertEquals(1, response.getDraw(), "DataTables response should have a draw value of 1");
		assertEquals(150, response.getRecordsFiltered(),
				"DataTables response should have a records filtered value of 150");
		assertEquals(150, response.getRecordsTotal(), "DataTables response should have a records total value of 150");
		assertTrue(response.getData() != null, "Should have data value");

		verify(analysisSubmissionService).listSubmissionsForUser(eq(searchValue), isNull(), isNull(), eq(user),
				isNull(), any());
	}

	@Test
	public void testGetPagedSubmissionsForProject() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		String searchValue = "";
		Project project = new Project();
		DataTablesParams params = new DataTablesParams(1, 10, 1, searchValue, Sort.by(Sort.Direction.ASC, "id"),
				ImmutableMap.of());

		when(analysisSubmissionService
				.listSubmissionsForProject(eq(searchValue), isNull(), isNull(), isNull(),
						eq(project), any()))
				.thenReturn(AnalysesDataFactory.getPagedAnalysisSubmissions());

		DataTablesResponse response = analysesListingService.getPagedSubmissions(params, Locale.US, null, project);

		assertEquals(1, response.getDraw(), "DataTables response should have a draw value of 1");
		assertEquals(150, response.getRecordsFiltered(),
				"DataTables response should have a records filtered value of 150");
		assertEquals(150, response.getRecordsTotal(), "DataTables response should have a records total value of 150");
		assertTrue(response.getData() != null, "Should have data value");

		verify(analysisSubmissionService)
				.listSubmissionsForProject(eq(searchValue), isNull(), isNull(), isNull(),
						eq(project), any());
	}
}
