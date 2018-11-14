package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableList;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.processing.impl.AssemblyFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

public class AssemblyFileProcessorTest {
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
	private IridaWorkflowsService workflowsService;

	private AssemblyFileProcessor processor;

	@Before
	public void setUp() throws IridaWorkflowNotFoundException {
		MockitoAnnotations.initMocks(this);

		processor = new AssemblyFileProcessor(objectRepository, submissionRepository, workflowsService, userRepository,
				ssoRepository, psjRepository);

		UUID workflowUUID = UUID.randomUUID();
		IridaWorkflowDescription workflowDescription = new IridaWorkflowDescription(workflowUUID, null, null, null,
				null, ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
		IridaWorkflow workflow = new IridaWorkflow(workflowDescription, null);

		when(workflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION)).thenReturn(workflow);
		when(userRepository.loadUserByUsername("admin")).thenReturn(new User());

	}

	@Test
	public void testAssembleFile() {
		Long sequenceFileId = 1L;
		SequenceFilePair pair = new SequenceFilePair(new SequenceFile(Paths.get("file_R1_1.fastq.gz")),
				new SequenceFile(Paths.get("file_R2_1.fastq.gz")));
		Sample sample = new Sample();
		Project project = new Project();
		project.setAssembleUploads(true);

		when(objectRepository.findOne(sequenceFileId)).thenReturn(pair);
		when(ssoRepository.getSampleForSequencingObject(pair)).thenReturn(new SampleSequencingObjectJoin(sample, pair));
		when(psjRepository.getProjectForSample(sample)).thenReturn(
				ImmutableList.of(new ProjectSampleJoin(project, sample, true)));

		assertTrue("should want to assemble file", processor.shouldProcessFile(sequenceFileId));
		processor.process(pair);

		verify(submissionRepository).save(any(AnalysisSubmission.class));
	}

	@Test
	public void testAssemblyDisabled() {
		Long sequenceFileId = 1L;
		SequenceFilePair pair = new SequenceFilePair();
		Sample sample = new Sample();
		Project project = new Project();
		project.setAssembleUploads(false);

		when(objectRepository.findOne(sequenceFileId)).thenReturn(pair);
		when(ssoRepository.getSampleForSequencingObject(pair)).thenReturn(new SampleSequencingObjectJoin(sample, pair));
		when(psjRepository.getProjectForSample(sample)).thenReturn(
				ImmutableList.of(new ProjectSampleJoin(project, sample, true)));

		assertFalse("processor should not want to assemble file", processor.shouldProcessFile(sequenceFileId));

		verifyZeroInteractions(submissionRepository);
	}

	@Test
	public void testOneProjectEnabled() {
		SequenceFilePair pair = new SequenceFilePair(new SequenceFile(Paths.get("file_R1_1.fastq.gz")),
				new SequenceFile(Paths.get("file_R2_1.fastq.gz")));
		Sample sample = new Sample();
		Project project = new Project();
		project.setAssembleUploads(true);

		Project disabledProject = new Project();
		disabledProject.setAssembleUploads(false);

		when(ssoRepository.getSampleForSequencingObject(pair)).thenReturn(new SampleSequencingObjectJoin(sample, pair));
		when(psjRepository.getProjectForSample(sample)).thenReturn(
				ImmutableList
						.of(new ProjectSampleJoin(disabledProject, sample, true), new ProjectSampleJoin(project, sample, true)));

		processor.process(pair);

		verify(submissionRepository).save(any(AnalysisSubmission.class));
	}
}
