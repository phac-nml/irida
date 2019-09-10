package ca.corefacility.bioinformatics.irida.ria.unit.web.pipelines;

import java.security.Principal;
import java.util.Locale;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotDisplayableException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyToolDataService;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.cart.CartController;
import ca.corefacility.bioinformatics.irida.ria.web.pipelines.PipelineController;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.impl.TestEmailController;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;

import java.security.Principal;
import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by josh on 15-01-09.
 */
public class PipelineControllerTest {
	// Dependencies to mock
	private ReferenceFileService referenceFileService;
	private SequencingObjectService sequencingObjectService;
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;
	private ProjectService projectService;
	private UserService userService;
	private MessageSource messageSource;
	private CartController cartController;
	// Controller to test
	private PipelineController controller;
	private WorkflowNamedParametersService namedParameterService;
	private UpdateSamplePermission updateSamplePermission;
	private AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;
	private GalaxyToolDataService galaxyToolDataService;
	private TestEmailController emailController;

	@Before
	public void setUp() {
		referenceFileService = mock(ReferenceFileService.class);
		analysisSubmissionService = mock(AnalysisSubmissionService.class);
		workflowsService = mock(IridaWorkflowsService.class);
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		messageSource = mock(MessageSource.class);
		cartController = mock(CartController.class);
		sequencingObjectService = mock(SequencingObjectService.class);
		namedParameterService = mock(WorkflowNamedParametersService.class);
		updateSamplePermission = mock(UpdateSamplePermission.class);
		analysisSubmissionSampleProcessor = mock(AnalysisSubmissionSampleProcessor.class);
		galaxyToolDataService = mock(GalaxyToolDataService.class);
		emailController = mock(TestEmailController.class);

		controller = new PipelineController(sequencingObjectService, referenceFileService, analysisSubmissionService,
				workflowsService, projectService, userService, cartController, messageSource, namedParameterService,
				updateSamplePermission, analysisSubmissionSampleProcessor, galaxyToolDataService, emailController);
		when(messageSource.getMessage(any(), any(), any())).thenReturn("");
	}

	@Test
	public void testGetPhylogenomicsPageWithEmptyCart() {
		ExtendedModelMap model = new ExtendedModelMap();
		Principal principal = () -> "FRED";
		UUID id = UUID.randomUUID();
		String response = controller.getSpecifiedPipelinePage(model, principal, Locale.US, id, null);
		assertEquals("If cart is empty user should be redirected.", PipelineController.URL_EMPTY_CART_REDIRECT,
				response);
	}

	@Test
	public void testGetPhylogenomicsPageWithCart()
			throws IridaWorkflowNotFoundException, IridaWorkflowNotDisplayableException {
		ExtendedModelMap model = new ExtendedModelMap();
		String username = "FRED";
		Principal principal = () -> username;
		User user = TestDataFactory.constructUser();
		UUID id = UUID.randomUUID();
		when(userService.getUserByUsername(username)).thenReturn(user);
		when(projectService.userHasProjectRole(any(User.class), any(Project.class), any(ProjectRole.class))).thenReturn(
				true);
		when(cartController.getSelected()).thenReturn(TestDataFactory.constructCart());

		when(sequencingObjectService.getSequencesForSampleOfType(any(Sample.class),
				eq(SingleEndSequenceFile.class))).thenReturn(
				TestDataFactory.generateSequencingObjectsForSample(TestDataFactory.constructSample()));

		when(workflowsService.getDisplayableIridaWorkflow(id)).thenReturn(TestDataFactory.getIridaWorkflow(id));
		String response = controller.getSpecifiedPipelinePage(model, principal, Locale.US, id, null);
		assertEquals("Response should be the path to the phylogenomics template",
				PipelineController.URL_GENERIC_PIPELINE, response);
		assertTrue("Model should contain the reference files.", model.containsKey("referenceFiles"));
		assertTrue("Model should contain a list of files.", model.containsKey("projects"));
	}

	@Test
	public void getPipelinePageForAutomation()
			throws IridaWorkflowNotFoundException, IridaWorkflowNotDisplayableException {
		ExtendedModelMap model = new ExtendedModelMap();
		Principal principal = () -> "FRED";
		UUID id = UUID.randomUUID();

		Project project = new Project();
		project.setId(5L);

		when(projectService.read(project.getId())).thenReturn(project);
		when(workflowsService.getDisplayableIridaWorkflow(id)).thenReturn(TestDataFactory.getIridaWorkflow(id));

		String response = controller.getSpecifiedPipelinePage(model, principal, Locale.US, id, project.getId());

		assertEquals(project.getId(), model.get("automatedProjectId"));
		assertNotNull(model.get("automatedProject"));
	}
}
