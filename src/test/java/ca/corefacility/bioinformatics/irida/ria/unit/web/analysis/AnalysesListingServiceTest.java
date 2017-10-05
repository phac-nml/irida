package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTAnalysis;
import ca.corefacility.bioinformatics.irida.ria.web.services.AnalysesListingService;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.google.common.collect.ImmutableMap;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
				updateAnalysisPermission, messageSource);
	}

	@Test
	public void testGetPagedSubmissions() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		DataTablesParams params = new DataTablesParams(1, 10, 1, "", new Sort(Sort.Direction.ASC, "id"),
				ImmutableMap.of());

		when(analysisSubmissionService.search(Matchers.<Specification<AnalysisSubmission>>any(),
				Matchers.<PageRequest>any())).thenReturn(AnalysesDataFactory.getPagedAnalysisSubmissions());

		DataTablesResponse response = analysesListingService.getPagedSubmissions(params, Locale.US);

		assertEquals("DataTables response should have a draw value of 1",1, response.getDraw());
		assertEquals("DataTables response should have a records filtered value of 150",150, response.getRecordsFiltered());
		assertEquals("DataTables response should have a records total value of 150",150, response.getRecordsTotal());
		assertTrue("Should have data value", response.getData() != null);
	}
}
