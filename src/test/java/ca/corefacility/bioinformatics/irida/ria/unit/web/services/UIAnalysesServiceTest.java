package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.security.Principal;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleAnalyses;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAnalysesService;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIAnalysesServiceTest {

	private UIAnalysesService service;
	private ProjectService projectService;
	private SequencingObjectService sequencingObjectService;
	private SampleService sampleService;
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	private UserService userService;
	private IridaWorkflowsService iridaWorkflowsService;
	private Principal principal;

	private final User USER_1 = new User("test", "test@nowhere.com", "PW1@3456", "Test", "Tester", "1234567890");
	private final User USER_2 = new User("test2", "test2@nowhere.com", "PW2@3456", "Test2", "Tester2", "1234567892");
	private final Sample SAMPLE_1 = new Sample("SAMPLE_01");

	private final Long SAMPLE_ID = 313L;
	private final String SAMPLE_ORGANISM = "Salmonella";
	private final String SAMPLE_DESCRIPTION = "This is a project about interesting stuff";

	SampleSequencingObjectJoin sampleSequencingObjectJoin;
	SampleGenomeAssemblyJoin sampleGenomeAssemblyJoin;

	@BeforeEach
	public void setUp() {
		sampleService = mock(SampleService.class);

		projectService = mock(ProjectService.class);
		UpdateSamplePermission updateSamplePermission = mock(UpdateSamplePermission.class);

		sequencingObjectService = mock(SequencingObjectService.class);
		sampleSequencingObjectJoin = mock(SampleSequencingObjectJoin.class);
		sampleGenomeAssemblyJoin = mock(SampleGenomeAssemblyJoin.class);

		MessageSource messageSource = mock(MessageSource.class);

		analysisSubmissionRepository = mock(AnalysisSubmissionRepository.class);
		userService = mock(UserService.class);
		iridaWorkflowsService = mock(IridaWorkflowsService.class);
		principal = mock(Principal.class);

		service = new UIAnalysesService(sampleService, sequencingObjectService, userService, analysisSubmissionRepository, iridaWorkflowsService, messageSource);

		// DATA
		SAMPLE_1.setId(SAMPLE_ID);
		SAMPLE_1.setDescription(SAMPLE_DESCRIPTION);
		SAMPLE_1.setOrganism(SAMPLE_ORGANISM);
		USER_1.setSystemRole(Role.ROLE_ADMIN);
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		when(sampleService.read(1L)).thenReturn(SAMPLE_1);
		when(updateSamplePermission.isAllowed(authentication, SAMPLE_1)).thenReturn(true);

	}

	@Test
	public void testGetSampleAnalyses() {

		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		submission.setId(1L);
		submission.setSubmitter(USER_1);

		AnalysisSubmission submission2 = TestDataFactory.constructAnalysisSubmission();
		submission2.setId(2L);
		submission2.setSubmitter(USER_2);

		AnalysisSubmission submission3 = TestDataFactory.constructAnalysisSubmission();
		submission3.setId(3L);
		submission3.setSubmitter(USER_2);

		Set<AnalysisSubmission> allAnalysisSubmissionSet = new HashSet<>();
		allAnalysisSubmissionSet.add(submission);
		allAnalysisSubmissionSet.add(submission2);
		allAnalysisSubmissionSet.add(submission3);

		Set<AnalysisSubmission> user1AnalysisSubmissionSet = new HashSet<>();
		user1AnalysisSubmissionSet.add(submission);

		SequencingObject sequencingObject1 = mock(SequencingObject.class);

		SampleSequencingObjectJoin s = new SampleSequencingObjectJoin(SAMPLE_1, sequencingObject1);

		Collection<SampleSequencingObjectJoin> sampleSequencingObjectJoinCollection = new ArrayList<>();
		sampleSequencingObjectJoinCollection.add(s);

		final IridaWorkflowInput input = new IridaWorkflowInput("single", "paired", "reference", true);
		IridaWorkflowDescription description = new IridaWorkflowDescription(submission.getWorkflowId(), "My Workflow",
				"V1", BuiltInAnalysisTypes.PHYLOGENOMICS, input, Lists.newArrayList(), Lists.newArrayList(),
				Lists.newArrayList());
		IridaWorkflow iridaWorkflow = new IridaWorkflow(description, null);

		when(sampleService.read(SAMPLE_ID)).thenReturn(SAMPLE_1);

		USER_1.setSystemRole(Role.ROLE_USER);
		USER_2.setSystemRole(Role.ROLE_ADMIN);

		USER_1.setId(1L);
		USER_2.setId(2L);

		when(userService.getUserByUsername(principal.getName())).thenReturn(USER_1);


		when(sequencingObjectService.getSequencingObjectsForSample(SAMPLE_1)).thenReturn(
				sampleSequencingObjectJoinCollection);

		when(analysisSubmissionRepository.findAnalysisSubmissionsForSequencingObjectBySubmitter(
				any(SequencingObject.class), eq(USER_1))).thenReturn(user1AnalysisSubmissionSet);

		when(iridaWorkflowsService.getIridaWorkflowOrUnknown(any(AnalysisSubmission.class))).thenReturn(iridaWorkflow);

		// Get sample analyses listing for user with Role.USER
		List<SampleAnalyses> sampleAnalysesList = service.getSampleAnalyses(SAMPLE_ID, principal, Locale.ENGLISH);
		assertEquals(1, sampleAnalysesList.size(), "A user with Role.USER should only see their own analyses ran with sample");


		when(userService.getUserByUsername(principal.getName())).thenReturn(USER_2);

		when(sequencingObjectService.getSequencingObjectsForSample(SAMPLE_1)).thenReturn(
				sampleSequencingObjectJoinCollection);

		when(analysisSubmissionRepository.findAnalysisSubmissionsForSequencingObject(
				any(SequencingObject.class))).thenReturn(allAnalysisSubmissionSet);

		when(iridaWorkflowsService.getIridaWorkflowOrUnknown(any(AnalysisSubmission.class))).thenReturn(iridaWorkflow);

		// Get sample analyses listing for user with Role.ADMIN
		sampleAnalysesList = service.getSampleAnalyses(SAMPLE_ID, principal, Locale.ENGLISH);
		assertEquals(3, sampleAnalysesList.size(), "A user with Role.ADMIN should see all analyses ran with sample");
	}
}
