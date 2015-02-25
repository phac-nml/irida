package ca.corefacility.bioinformatics.irida.ria.unit.web.pipelines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.CartController;
import ca.corefacility.bioinformatics.irida.ria.web.pipelines.PipelineController;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Unit tests for {@link PipelineController}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class PipelineControllerTest {
	// Constants
	public static final Locale LOCALE = Locale.US;
	// Dependencies to mock
	private ReferenceFileService referenceFileService;
	private SequenceFileService sequenceFileService;
	private SequenceFilePairService sequenceFilePairService;
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;
	private ProjectService projectService;
	private UserService userService;
	private MessageSource messageSource;
	private CartController cartController;
	private SampleSequenceFileJoinRepository ssfRepositoryMock;
	// Controller to test
	private PipelineController controller;

	@Before
	public void setUp() {
		referenceFileService = mock(ReferenceFileService.class);
		sequenceFileService = mock(SequenceFileService.class);
		sequenceFilePairService = mock(SequenceFilePairService.class);
		analysisSubmissionService = mock(AnalysisSubmissionService.class);
		workflowsService = mock(IridaWorkflowsService.class);
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		messageSource = mock(MessageSource.class);
		cartController = mock(CartController.class);
		ssfRepositoryMock = mock(SampleSequenceFileJoinRepository.class);

		controller = new PipelineController(sequenceFileService, sequenceFilePairService, referenceFileService,
				analysisSubmissionService, workflowsService, projectService, userService, cartController, messageSource);
	}

	@Test
	public void testGetPipelineLaunchPage() {
		ExtendedModelMap model = new ExtendedModelMap();
		String response = controller.getPipelineLaunchPage(model, LOCALE);
		assertEquals(PipelineController.URL_LAUNCH, response);
		assertTrue(model.containsKey("counts"));
		assertTrue(model.containsKey("workflows"));
	}

	@Test
	public void testGetPhylogenomicsPageWithEmptyCart() {
		ExtendedModelMap model = new ExtendedModelMap();
		Principal principal = () -> "FRED";
		UUID id = UUID.randomUUID();
		String response = controller.getPhylogenomicsPage(model, principal, Locale.US, id);
		assertEquals("If cart is empty user should be redirected.", PipelineController.URL_EMPTY_CART_REDIRECT, response);
	}

	@Test
	public void testGetPhylogenomicsPageWithCart() throws IridaWorkflowNotFoundException {
		ExtendedModelMap model = new ExtendedModelMap();
		String username = "FRED";
		Principal principal = () -> username;
		User user = TestDataFactory.constructUser();
		UUID id = UUID.randomUUID();
		when(userService.getUserByUsername(username)).thenReturn(user);
		when(projectService.userHasProjectRole(any(User.class), any(Project.class), any(ProjectRole.class))).thenReturn(true);
		when(cartController.getSelected()).thenReturn(TestDataFactory.constructCart());
		when(sequenceFileService.getSequenceFilesForSample(any(Sample.class)))
				.thenReturn(TestDataFactory.generateSequenceFilesForSample(TestDataFactory.constructSample()));
		when(workflowsService.getIridaWorkflow(id)).thenReturn(TestDataFactory.getIridaWorkflow(id));
		String response = controller.getPhylogenomicsPage(model, principal, Locale.US, id);
		assertEquals("Response should be the path to the phylogenomics template", PipelineController.URL_GENERIC_PIPELINE, response);
		assertTrue("Model should contain the reference files.", model.containsKey("referenceFiles"));
		assertTrue("Model should contain a list of files.", model.containsKey("projects"));
	}

	@Test
	public void testAjaxStartPipeline() throws IridaWorkflowNotFoundException {
		UUID pipelineId = UUID.randomUUID();
		List<Long> pairedEndFileIds = ImmutableList.of(1L, 2L, 3L);
		String name = "TEST_02042016";

		when(workflowsService.getIridaWorkflow(pipelineId)).thenReturn(TestDataFactory.getIridaWorkflow(pipelineId));
		when(ssfRepositoryMock.getUnpairedSequenceFilesForSample(any(SequenceFile.class)))
				.thenReturn(new SampleSequenceFileJoin());
		when(sequenceFileService.getUniqueSamplesForSequenceFiles(Sets.newHashSet(pairedEndFileIds))).

		Map<String, Object> result = controller
				.ajaxStartPipeline(LOCALE, pipelineId, null, pairedEndFileIds, null, null, name);
	}
}
