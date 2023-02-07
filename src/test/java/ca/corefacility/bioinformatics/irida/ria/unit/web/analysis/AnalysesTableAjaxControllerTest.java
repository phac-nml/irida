package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import java.security.Principal;


import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.AnalysesTableAjaxController;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.AnalysisTypesService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.ImmutableSet;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AnalysesTableAjaxControllerTest {

	@Mock
	private AnalysisSubmissionService analysisSubmissionService;

	@Mock
	private AnalysisTypesService analysisTypesService;

	@Mock
	private IridaWorkflowsService iridaWorkflowsService;

	@Mock
	private UpdateAnalysisSubmissionPermission updateAnalysisSubmissionPermission;

	@Mock
	private MessageSource messageSource;

	@InjectMocks
	private AnalysesTableAjaxController controller;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		/*
		Set up all mocks here
		 */
		when(iridaWorkflowsService.getRegisteredWorkflowTypes()).thenReturn(
				ImmutableSet.of(new AnalysisType("Pipeline 1"), new AnalysisType("Pipeline 2")));

		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.build();
	}

	@Test
	public void testGetAnalysisStates() throws Exception {
		mockMvc.perform(get("/ajax/analyses/states").accept(MediaType.APPLICATION_JSON).principal(mock(Principal.class)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].value", is("NEW")))
				.andExpect(jsonPath("$[1].value", is("DOWNLOADING")))
				.andExpect(jsonPath("$[2].value", is("FINISHED_DOWNLOADING")))
				.andExpect(jsonPath("$[3].value", is("PREPARING")))
				.andExpect(jsonPath("$[4].value", is("PREPARED")))
				.andExpect(jsonPath("$[5].value", is("SUBMITTING")))
				.andExpect(jsonPath("$[6].value", is("RUNNING")))
				.andExpect(jsonPath("$[7].value", is("FINISHED_RUNNING")))
				.andExpect(jsonPath("$[8].value", is("COMPLETING")))
				.andExpect(jsonPath("$[9].value", is("COMPLETED")))
				.andExpect(jsonPath("$[10].value", is("TRANSFERRED")))
				.andExpect(jsonPath("$[11].value", is("POST_PROCESSING")));
	}

	@Test
	public void testGetWorkflowTypes() throws Exception {
		mockMvc.perform(get("/ajax/analyses/types").accept(MediaType.APPLICATION_JSON).principal(mock(Principal.class)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].value", is("Pipeline 1")))
				.andExpect(jsonPath("$[1].value", is("Pipeline 2")));
	}
}
