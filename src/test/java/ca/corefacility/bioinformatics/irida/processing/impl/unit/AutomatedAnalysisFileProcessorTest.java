package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.processing.impl.AutomatedAnalysisFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionTemplateRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.ProjectAnalysisSubmissionJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AutomatedAnalysisFileProcessorTest {
	@Mock
	private SequencingObjectRepository objectRepository;
	@Mock
	private SampleSequencingObjectJoinRepository ssoRepository;
	@Mock
	private ProjectSampleJoinRepository psjRepository;
	@Mock
	private AnalysisSubmissionRepository submissionRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ProjectAnalysisSubmissionJoinRepository pasRepository;
	@Mock
	private IridaWorkflowsService workflowsService;
	@Mock
	private AnalysisSubmissionTemplateRepository templateRepository;

	@Mock
	private MessageSource messageSource;

	private AutomatedAnalysisFileProcessor processor;

	IridaWorkflow assemblyWorkflow;
	IridaWorkflow otherWorkflow;
	IridaWorkflow sistrWorkflow;
	User submitter;

	@Before
	public void setUp() throws IridaWorkflowNotFoundException {
		MockitoAnnotations.initMocks(this);

		processor = new AutomatedAnalysisFileProcessor(ssoRepository, psjRepository, submissionRepository,
				templateRepository, pasRepository, workflowsService, objectRepository, messageSource);

		//assembly
		UUID assemblyID = UUID.randomUUID();
		IridaWorkflowDescription assemblyWFD = new IridaWorkflowDescription(assemblyID, null, null, null, null,
				ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
		assemblyWorkflow = new IridaWorkflow(assemblyWFD, null);

		//sistr, needed for the legacy file processor stuff
		UUID sistrID = UUID.randomUUID();
		IridaWorkflowDescription sistrWFD = new IridaWorkflowDescription(sistrID, null, null, null, null,
				ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
		sistrWorkflow = new IridaWorkflow(sistrWFD, null);

		//a different random workflow
		UUID otherWorkflowId = UUID.randomUUID();
		IridaWorkflowDescription otherWFD = new IridaWorkflowDescription(otherWorkflowId, null, null, null, null,
				ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
		otherWorkflow = new IridaWorkflow(otherWFD, null);

		submitter = new User();

		when(workflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION)).thenReturn(
				assemblyWorkflow);
		when(workflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.SISTR_TYPING)).thenReturn(sistrWorkflow);
		when(workflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.DEFAULT)).thenReturn(otherWorkflow);

		when(userRepository.loadUserByUsername("admin")).thenReturn(new User());

	}

	@Test
	public void testAssembleFile() {
		Long sequenceFileId = 1L;
		SequenceFilePair pair = new SequenceFilePair(new SequenceFile(Paths.get("file_R1_1.fastq.gz")),
				new SequenceFile(Paths.get("file_R2_1.fastq.gz")));
		Sample sample = new Sample();
		Project project = new Project();

		AnalysisSubmissionTemplate assemblyTemplate = new AnalysisSubmissionTemplate("assemble",
				assemblyWorkflow.getWorkflowIdentifier(), ImmutableMap.of("param1", "myparam"), null, false, "desc",
				false, project);
		assemblyTemplate.setSubmitter(submitter);

		AnalysisSubmission built = new AnalysisSubmission.Builder(assemblyTemplate).inputFiles(Sets.newHashSet(pair))
				.updateSamples(true)
				.build();

		when(templateRepository.getEnabledAnalysisSubmissionTemplatesForProject(project)).thenReturn(
				Lists.newArrayList(assemblyTemplate));
		when(objectRepository.findOne(sequenceFileId)).thenReturn(pair);
		when(ssoRepository.getSampleForSequencingObject(pair)).thenReturn(new SampleSequencingObjectJoin(sample, pair));
		when(psjRepository.getProjectForSample(sample)).thenReturn(
				ImmutableList.of(new ProjectSampleJoin(project, sample, true)));
		when(submissionRepository.save(any(AnalysisSubmission.class))).thenReturn(built);

		assertTrue("should want to assemble file", processor.shouldProcessFile(sequenceFileId));
		processor.process(pair);

		verify(submissionRepository).save(any(AnalysisSubmission.class));
		verify(pasRepository).save(any(ProjectAnalysisSubmissionJoin.class));

		//ensure the assembly is associated with the sample
		ArgumentCaptor<SequencingObject> captor = ArgumentCaptor.forClass(SequencingObject.class);
		verify(objectRepository).save(captor.capture());

		AnalysisSubmission automatedAssembly = captor.getValue()
				.getAutomatedAssembly();

		assertEquals("Seq object should have an assembly associated", built, automatedAssembly);
	}

	@Test
	public void testOtherWorkflow() {
		Long sequenceFileId = 1L;
		SequenceFilePair pair = new SequenceFilePair(new SequenceFile(Paths.get("file_R1_1.fastq.gz")),
				new SequenceFile(Paths.get("file_R2_1.fastq.gz")));
		Sample sample = new Sample();
		Project project = new Project();

		AnalysisSubmissionTemplate assemblyTemplate = new AnalysisSubmissionTemplate("assemble",
				otherWorkflow.getWorkflowIdentifier(), ImmutableMap.of("param1", "myparam"), null, false, "desc", false,
				project);
		assemblyTemplate.setSubmitter(submitter);

		AnalysisSubmission built = new AnalysisSubmission.Builder(assemblyTemplate).inputFiles(Sets.newHashSet(pair))
				.updateSamples(true)
				.build();

		when(templateRepository.getEnabledAnalysisSubmissionTemplatesForProject(project)).thenReturn(
				Lists.newArrayList(assemblyTemplate));
		when(objectRepository.findOne(sequenceFileId)).thenReturn(pair);
		when(ssoRepository.getSampleForSequencingObject(pair)).thenReturn(new SampleSequencingObjectJoin(sample, pair));
		when(psjRepository.getProjectForSample(sample)).thenReturn(
				ImmutableList.of(new ProjectSampleJoin(project, sample, true)));
		when(submissionRepository.save(any(AnalysisSubmission.class))).thenReturn(built);

		assertTrue("should want to assemble file", processor.shouldProcessFile(sequenceFileId));
		processor.process(pair);

		verify(submissionRepository).save(any(AnalysisSubmission.class));
		verify(pasRepository).save(any(ProjectAnalysisSubmissionJoin.class));

		//ensure the assembly is associated with the sample
		verify(objectRepository, times(0)).save(any(SequencingObject.class));
	}

	@Test
	public void testNoSubmissions() {
		Long sequenceFileId = 1L;
		SequenceFilePair pair = new SequenceFilePair();
		Sample sample = new Sample();
		Project project = new Project();

		when(templateRepository.getEnabledAnalysisSubmissionTemplatesForProject(project)).thenReturn(
				Lists.newArrayList());
		when(objectRepository.findOne(sequenceFileId)).thenReturn(pair);
		when(ssoRepository.getSampleForSequencingObject(pair)).thenReturn(new SampleSequencingObjectJoin(sample, pair));
		when(psjRepository.getProjectForSample(sample)).thenReturn(
				ImmutableList.of(new ProjectSampleJoin(project, sample, true)));

		verifyZeroInteractions(submissionRepository);
	}

	@Test
	public void testOneProjectEnabled() {
		SequenceFilePair pair = new SequenceFilePair(new SequenceFile(Paths.get("file_R1_1.fastq.gz")),
				new SequenceFile(Paths.get("file_R2_1.fastq.gz")));
		Sample sample = new Sample();
		Project project = new Project("assemble me");

		Project disabledProject = new Project("no assembly");

		AnalysisSubmissionTemplate assemblyTemplate = new AnalysisSubmissionTemplate("assemble",
				assemblyWorkflow.getWorkflowIdentifier(), ImmutableMap.of("param1", "myparam"), null, false, "desc",
				false, project);
		assemblyTemplate.setSubmitter(submitter);

		AnalysisSubmission built = new AnalysisSubmission.Builder(assemblyTemplate).inputFiles(Sets.newHashSet(pair))
				.build();

		when(templateRepository.getEnabledAnalysisSubmissionTemplatesForProject(project)).thenReturn(
				Lists.newArrayList(assemblyTemplate));
		when(ssoRepository.getSampleForSequencingObject(pair)).thenReturn(new SampleSequencingObjectJoin(sample, pair));
		when(psjRepository.getProjectForSample(sample)).thenReturn(
				ImmutableList.of(new ProjectSampleJoin(disabledProject, sample, true),
						new ProjectSampleJoin(project, sample, true)));
		when(submissionRepository.save(any(AnalysisSubmission.class))).thenReturn(built);

		processor.process(pair);

		verify(submissionRepository).save(any(AnalysisSubmission.class));

		ArgumentCaptor<ProjectAnalysisSubmissionJoin> captor = ArgumentCaptor.forClass(
				ProjectAnalysisSubmissionJoin.class);

		verify(pasRepository).save(captor.capture());

		ProjectAnalysisSubmissionJoin captorValue = captor.getValue();
		assertEquals("should have run file processor for one project", project, captorValue.getSubject());

	}
}
