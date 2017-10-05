package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.AnalysisController;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.AnalysesListingService;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AnalysisControllerDataTablesTest {
	@Mock
	private AnalysisSubmissionService analysisSubmissionService;

	@Mock
	private IridaWorkflowsService iridaWorkflowsService;

	@Mock
	private MessageSource messageSource;

	@Mock
	private UserService userService;

	@Mock
	private UpdateAnalysisSubmissionPermission updateAnalysisSubmissionPermission;

	@Mock
	private SampleService sampleService;

	@Mock
	private ProjectService projectService;

	@Mock
	private MetadataTemplateService metadataTemplateService;

	@Mock
	private SequencingObjectService sequencingObjectService;

	@Mock()
	private AnalysesListingService analysesListingService;

	@InjectMocks
	private AnalysisController analysisController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this); // initializes controller and mocks

		mockMvc = MockMvcBuilders.standaloneSetup(analysisController)
				.build();
	}

	@Test
	public void testListAll() throws Exception {
		DataTablesParams params = new DataTablesParams(1, 10, 1, "", new Sort(Sort.Direction.ASC, "id"),
				ImmutableMap.of());

		Page<AnalysisSubmission> page = AnalysesDataFactory.getPagedAnalysisSubmissions();

		when(analysesListingService.getPagedSubmissions(Matchers.<DataTablesParams>any(),
				Matchers.<Locale>any())).thenReturn(new DataTablesResponse(params, page, ImmutableList.of()));

		mockMvc.perform(get("/analysis/ajax/list/all").param("draw", "1")
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
