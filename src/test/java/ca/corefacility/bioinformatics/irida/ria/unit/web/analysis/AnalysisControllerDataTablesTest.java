package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import java.security.Principal;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.AnalysisController;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.AnalysesListingService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test for {@link AnalysisController} for handling request for DataTables.
 */
public class AnalysisControllerDataTablesTest {

	@Mock
	private UserService userService;

	@Mock
	private ProjectService projectService;

	@Mock()
	private AnalysesListingService analysesListingService;

	@InjectMocks
	private AnalysisController analysisController;

	private MockMvc mockMvc;

	@Before
	public void setup() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		MockitoAnnotations.initMocks(this); // initializes controller and mocks

		/*
		Need to set up a mock for the AnalysisSubmissionService so that it return a Page of AnalysisSubmission.
		 */
		DataTablesParams params = new DataTablesParams(1, 10, 1, "", new Sort(Sort.Direction.ASC, "id"),
				ImmutableMap.of());
		Page<AnalysisSubmission> page = AnalysesDataFactory.getPagedAnalysisSubmissions();
		when(analysesListingService.getPagedSubmissions(Matchers.any(),
				Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(new DataTablesResponse(params, page, ImmutableList.of()));

		mockMvc = MockMvcBuilders.standaloneSetup(analysisController)
				.build();
	}

	@Test
	public void testListAll() throws Exception {
		testAnalysesListingByUrl("/analysis/ajax/list/all");
	}

	@Test
	public void testListAllForUsers() throws Exception {
		testAnalysesListingByUrl("/analysis/ajax/list");
	}

	@Test
	public void testListAllForUserInProject() throws Exception {
		testAnalysesListingByUrl("/analysis/ajax/project/{projectId}/list");
	}

	private void testAnalysesListingByUrl(String url) throws Exception {
		mockMvc.perform(get(url, 1)
				.principal(mock(Principal.class))
				.param("draw", "1")
				.param("columns[0][data]", "id")
				.param("columns[0][name]", "")
				.param("columns[0][searchable]", "true")
				.param("columns[0][orderable]", "true")
				.param("order[0][column]", "0")
				.param("order[0][dir]", "desc")
				.param("locale", Locale.US.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.draw", is(1)))
				.andExpect(jsonPath("$.recordsFiltered", is(150)))
				.andExpect(jsonPath("$.recordsTotal", is(150)))
				.andExpect(jsonPath("$.data").isArray());
	}
}
