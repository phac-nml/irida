package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletResponse;

import ca.corefacility.bioinformatics.irida.config.analysis.ExecutionManagerConfig;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;


import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.AnalysisAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysisInputFiles;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.auditing.AnalysisAudit;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysisOutputFileInfo;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysisSamples;
import ca.corefacility.bioinformatics.irida.ria.web.components.AnalysisOutputFileDownloadManager;
import ca.corefacility.bioinformatics.irida.ria.web.services.AnalysesListingService;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnalysisAjaxControllerTest {
	/*
	 * CONTROLLER
	 */
	private AnalysisAjaxController analysisAjaxController;

	/*
	 * SERVICES
	 */
	private AnalysisSubmissionService analysisSubmissionServiceMock;
	private IridaWorkflowsService iridaWorkflowsServiceMock;
	private UserService userServiceMock;
	private ProjectService projectServiceMock;
	private SampleService sampleService;
	private UpdateAnalysisSubmissionPermission updatePermission;
	private MetadataTemplateService metadataTemplateService;
	private SequencingObjectService sequencingObjectService;
	private AnalysesListingService analysesListingService;
	private AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;
	private AnalysisOutputFileDownloadManager analysisOutputFileDownloadManager;
	private ExecutionManagerConfig configFileMock;
	private EmailController emailControllerMock;
	private AnalysisAudit analysisAuditMock;
	/**
	 * Analysis Output File key names from {@link TestDataFactory#constructAnalysis()}
	 */
	private final List<String> outputNames = Lists.newArrayList("tree", "matrix", "table", "contigs-with-repeats",
			"refseq-masher-matches");

	@Before
	public void init() {
		analysisSubmissionServiceMock = mock(AnalysisSubmissionService.class);
		iridaWorkflowsServiceMock = mock(IridaWorkflowsService.class);
		projectServiceMock = mock(ProjectService.class);
		updatePermission = mock(UpdateAnalysisSubmissionPermission.class);
		sampleService = mock(SampleService.class);
		sequencingObjectService = mock(SequencingObjectService.class);
		analysisSubmissionSampleProcessor = mock(AnalysisSubmissionSampleProcessor.class);
		analysisOutputFileDownloadManager = mock(AnalysisOutputFileDownloadManager.class);
		userServiceMock = mock(UserService.class);
		configFileMock = mock(ExecutionManagerConfig.class);
		MessageSource messageSourceMock = mock(MessageSource.class);
		AnalysisAudit analysisAuditingMock = mock(AnalysisAudit.class);

		analysisAjaxController = new AnalysisAjaxController(analysisSubmissionServiceMock, iridaWorkflowsServiceMock,
				userServiceMock, sampleService, projectServiceMock, updatePermission, metadataTemplateService,
				sequencingObjectService, analysisSubmissionSampleProcessor,
				analysisOutputFileDownloadManager, messageSourceMock, configFileMock, analysisAuditingMock);

	}

	@Test
	public void getOutputFilesInfoSuccess() throws IridaWorkflowNotFoundException {
		Long submissionId = 1L;

		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		submission.setAnalysisState(AnalysisState.COMPLETED);

		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getOutputNames(submission.getWorkflowId()))
				.thenReturn(Lists.newArrayList("tree"));

		List<AnalysisOutputFileInfo> outputInfos = analysisAjaxController.getOutputFilesInfo(submissionId);

		assertEquals("Should only be one output", 1, outputInfos.size());

		AnalysisOutputFileInfo info = outputInfos.get(0);

		assertEquals("Should have proper filename", "snp_tree.tree", info.getFilename());
	}

	@Test
	public void getOutputFilesInfoSuccessNoWorkflow() throws IridaWorkflowNotFoundException {
		Long submissionId = 1L;

		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		submission.setAnalysisState(AnalysisState.COMPLETED);

		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getOutputNames(submission.getWorkflowId()))
				.thenThrow(new IridaWorkflowNotFoundException(""));

		List<AnalysisOutputFileInfo> outputInfos = analysisAjaxController.getOutputFilesInfo(submissionId);

		assertEquals("Should be 5 outputs", 5, outputInfos.size());
	}

	// ************************************************************************************************
	// AJAX TESTS
	// ************************************************************************************************

	@Test
	public void TestGetAjaxDownloadAnalysisSubmission() {
		Long analysisSubmissionId = 1L;
		MockHttpServletResponse response = new MockHttpServletResponse();

		when(analysisSubmissionServiceMock.read(analysisSubmissionId)).thenReturn(
				TestDataFactory.constructAnalysisSubmission());
		try {
			analysisAjaxController.getAjaxDownloadAnalysisSubmission(analysisSubmissionId, response);
			assertEquals("Has the correct content type", "application/zip", response.getContentType());
			assertEquals("Has the correct 'Content-Disposition' headers", "attachment;filename=submission-5.zip",
					response.getHeader("Content-Disposition"));
		} catch (final Exception e) {
			fail();
		}
	}

	@Test
	public void testGetOutputFileLines() throws IridaWorkflowNotFoundException {
		final Long submissionId = 1L;
		final MockHttpServletResponse response = new MockHttpServletResponse();
		final AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		final UUID workflowId = submission.getWorkflowId();
		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getOutputNames(workflowId)).thenReturn(
				outputNames);
		// get analysis output file summary info
		final List<AnalysisOutputFileInfo> infos = analysisAjaxController.getOutputFilesInfo(submissionId);
		assertEquals("Expecting 5 analysis output file info items", 5, infos.size());
		final Optional<AnalysisOutputFileInfo> optInfo = infos.stream()
				.filter(x -> Objects.equals(x.getOutputName(), "refseq-masher-matches"))
				.findFirst();
		assertTrue("Should be a refseq-masher-matches.tsv output file", optInfo.isPresent());
		final AnalysisOutputFileInfo info = optInfo.get();
		final String firstLine = "sample\ttop_taxonomy_name\tdistance\tpvalue\tmatching\tfull_taxonomy\ttaxonomic_subspecies\ttaxonomic_species\ttaxonomic_genus\ttaxonomic_family\ttaxonomic_order\ttaxonomic_class\ttaxonomic_phylum\ttaxonomic_superkingdom\tsubspecies\tserovar\tplasmid\tbioproject\tbiosample\ttaxid\tassembly_accession\tmatch_id";
		assertEquals("First line of file should be read since it has a tabular file extension", firstLine,
				info.getFirstLine());
		final Long seekTo = 290L;
		assertEquals("FilePointer should be first character of second line of file", seekTo, info.getFilePointer());
		assertEquals("File size in bytes should be returned", Long.valueOf(61875), info.getFileSizeBytes());
		final Long limit = 3L;
		final AnalysisOutputFileInfo lineInfo = analysisAjaxController.getOutputFile(submissionId, info.getId(), limit, 0L,
				null, 0L, null, response);
		assertEquals(limit.intValue(), lineInfo.getLines()
				.size());
		String expLine = "SRR1203042\tSalmonella enterica subsp. enterica serovar Abony str. 0014\t0.00650877\t0.0\t328/400\tBacteria; Proteobacteria; Gammaproteobacteria; Enterobacterales; Enterobacteriaceae; Salmonella; enterica; subsp. enterica; serovar Abony; str. 0014\tSalmonella enterica subsp. enterica\tSalmonella enterica\tSalmonella\tEnterobacteriaceae\tEnterobacterales\tGammaproteobacteria\tProteobacteria\tBacteria\tenterica\tAbony\t\tPRJNA224116\tSAMN01823751\t1029983\tGCF_000487615.2\t./rcn/refseq-NZ-1029983-PRJNA224116-SAMN01823751-GCF_000487615.2-.-Salmonella_enterica_subsp._enterica_serovar_Abony_str._0014.fna";
		assertEquals(expLine, lineInfo.getLines()
				.get(0));
		// begin reading lines after first line file pointer position
		final AnalysisOutputFileInfo lineInfoRandomAccess = analysisAjaxController.getOutputFile(submissionId, info.getId(),
				limit, 0L, null, info.getFilePointer(), null, response);
		assertEquals(
				"Using the RandomAccessFile reading method with seek>0, should give the same results as using a BufferedReader if both start reading at the same position",
				limit.intValue(), lineInfoRandomAccess.getLines()
						.size());
		assertEquals(
				"Using the RandomAccessFile reading method with seek>0, should give the same results as using a BufferedReader if both start reading at the same position",
				expLine, lineInfoRandomAccess.getLines()
						.get(0));
	}

	@Test
	public void testGetOutputFileByteSizedChunks() throws IridaWorkflowNotFoundException {
		final Long submissionId = 1L;
		final MockHttpServletResponse response = new MockHttpServletResponse();
		final AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		final UUID workflowId = submission.getWorkflowId();
		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getOutputNames(workflowId)).thenReturn(
				outputNames);
		// get analysis output file summary info
		final List<AnalysisOutputFileInfo> infos = analysisAjaxController.getOutputFilesInfo(submissionId);
		assertEquals("Expecting 5 analysis output file info items", 5, infos.size());
		final Optional<AnalysisOutputFileInfo> optInfo = infos.stream()
				.filter(x -> Objects.equals(x.getOutputName(), "refseq-masher-matches"))
				.findFirst();
		assertTrue("Should be a refseq-masher-matches.tsv output file", optInfo.isPresent());
		final AnalysisOutputFileInfo info = optInfo.get();
		final String firstLine = "sample\ttop_taxonomy_name\tdistance\tpvalue\tmatching\tfull_taxonomy\ttaxonomic_subspecies\ttaxonomic_species\ttaxonomic_genus\ttaxonomic_family\ttaxonomic_order\ttaxonomic_class\ttaxonomic_phylum\ttaxonomic_superkingdom\tsubspecies\tserovar\tplasmid\tbioproject\tbiosample\ttaxid\tassembly_accession\tmatch_id";
		assertEquals("First line of file should be read since it has a tabular file extension", firstLine,
				info.getFirstLine());
		final Long seekTo = 290L;
		final Long expFileSize = 61875L;
		assertEquals("FilePointer should be first character of second line of file", seekTo, info.getFilePointer());
		assertEquals("File size in bytes should be returned", expFileSize, info.getFileSizeBytes());
		final Long chunkSize = 10L;
		final AnalysisOutputFileInfo chunkInfo = analysisAjaxController.getOutputFile(submissionId, info.getId(), null,
				null, null, seekTo, chunkSize, response);
		assertEquals("Should get the first 10 characters of the 2nd line starting at file pointer position 290",
				"SRR1203042", chunkInfo.getText());
		final long expFilePointer = seekTo + chunkSize;
		assertEquals("After reading byte chunk of size x starting at position y, filePointer should be x+y",
				expFilePointer, chunkInfo.getFilePointer()
						.longValue());
		String nextTextChunk = "\tSalmonella enterica subsp. enterica serovar Abony str. 0014";
		final AnalysisOutputFileInfo nextChunkInfo = analysisAjaxController.getOutputFile(submissionId, info.getId(), null,
				null, null, chunkInfo.getFilePointer(), (long) nextTextChunk.length(), response);
		assertEquals("Should be able to continue reading from last file pointer position", nextTextChunk,
				nextChunkInfo.getText());
		final AnalysisOutputFileInfo lastChunkOfFile = analysisAjaxController.getOutputFile(submissionId, info.getId(), null,
				null, null, expFileSize - chunkSize, chunkSize, response);
		final String lastChunkText = "_str..fna\n";
		assertEquals("Should have successfully read the last chunk of the file",
				lastChunkText, lastChunkOfFile.getText());
		final AnalysisOutputFileInfo chunkOutsideRangeOfFile = analysisAjaxController.getOutputFile(submissionId, info.getId(), null,
				null, null, expFileSize + chunkSize, chunkSize, response);
		assertEquals("Should return empty string since nothing can be read outside of file range",
				"", chunkOutsideRangeOfFile.getText());
		assertEquals("Should have seeked to an position of file size + chunkSize",
				expFileSize + chunkSize, (long) chunkOutsideRangeOfFile.getStartSeek());
		assertEquals("FilePointer shouldn't have changed from startSeek",
				expFileSize + chunkSize, (long) chunkOutsideRangeOfFile.getFilePointer());
	}

	@Test
	public void testUpdateAnalysisEmailPipelineResult() throws IridaWorkflowNotFoundException {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();

		submission.setAnalysisState(AnalysisState.RUNNING);
		assertFalse("Email result on pipeline completion", submission.getEmailPipelineResult());
		submission.setEmailPipelineResult(true);
		assertTrue("Email result on pipeline completion", submission.getEmailPipelineResult());
	}

	@Test
	public void testUpdateAnalysisPriority() throws IridaWorkflowNotFoundException {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();

		submission.setAnalysisState(AnalysisState.NEW);
		assertEquals("Priority should be medium", submission.getPriority(), AnalysisSubmission.Priority.MEDIUM);
		submission.setPriority(AnalysisSubmission.Priority.HIGH);
		assertEquals("Priority should be high", submission.getPriority(), AnalysisSubmission.Priority.HIGH);
	}

	@Test
	public void testUpdateAnalysisName() throws IridaWorkflowNotFoundException {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();

		assertEquals("Submission name should be", submission.getName(), "submission-"+submission.getId());
		submission.setName("NEW SUBMISSION");
		assertEquals("Submission name should be", submission.getName(), "NEW SUBMISSION");
	}

	@Test
	public void testGetAnalysisDetails() {
		final IridaWorkflowInput input = new IridaWorkflowInput("single", "paired", "reference", true);
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();

		String submissionName = submission.getName();
		assertEquals("Submission name should be", submissionName, "submission-"+submission.getId());

		String analysisDescription = submission.getAnalysisDescription();
		assertEquals("Submission description should be empty", analysisDescription, null);

		assertEquals("Submission id is 5", submission.getId().longValue(), 5L);

		AnalysisSubmission.Priority submissionPriority = submission.getPriority();
		assertEquals("Priority should be medium", submissionPriority, AnalysisSubmission.Priority.MEDIUM);

		IridaWorkflowDescription description = new IridaWorkflowDescription(submission.getWorkflowId(), "My Workflow",
				"V1", BuiltInAnalysisTypes.PHYLOGENOMICS, input, Lists.newArrayList(), Lists.newArrayList(),
				Lists.newArrayList());

		assertEquals("Workflow name", description.getName(), "My Workflow");
		assertEquals("Pipeline type", description.getAnalysisType(), BuiltInAnalysisTypes.PHYLOGENOMICS);
		assertEquals("Pipeline version should be", description.getVersion(), "V1");
	}

	@Test
	public void testDeleteAnalysisSubmission() {
		Long submissionId = 1L;
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		analysisSubmissionServiceMock.delete(submission.getId());
		assertNull("Submission has been deleted", analysisSubmissionServiceMock.read(submission.getId()));
	}

	@Test
	public void updateSharedProjects(){
		Long submissionId = 1L;
		Long projectId = 1L;
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		Project project = TestDataFactory.constructProject();
		ProjectAnalysisSubmissionJoin paj = TestDataFactory.constructProjectAnalysisSubmissionJoin(project, submission);
		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(projectServiceMock.read(projectId)).thenReturn(project);
		when(analysisSubmissionServiceMock.shareAnalysisSubmissionWithProject(submission, project)).thenReturn(paj);
		assertNotNull("Analysis results shared with project is not null", analysisSubmissionServiceMock.shareAnalysisSubmissionWithProject(submission, project));
	}

	@Test
	public void removeSharedProjects(){
		Long submissionId = 1L;
		Long projectId = 1L;
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		Project project = TestDataFactory.constructProject();
		ProjectAnalysisSubmissionJoin paj = TestDataFactory.constructProjectAnalysisSubmissionJoin(project, submission);
		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(projectServiceMock.read(projectId)).thenReturn(project);
		when(analysisSubmissionServiceMock.shareAnalysisSubmissionWithProject(submission, project)).thenReturn(paj);
		assertNotNull("Analysis results shared with project is not null", analysisSubmissionServiceMock.shareAnalysisSubmissionWithProject(submission, project));
	}

	@Test
	public void saveResultsToSamples() {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		try {
			analysisSubmissionSampleProcessor.updateSamples(submission);
			assertFalse ("Samples have not been updated", submission.getUpdateSamples());

			submission.setUpdateSamples(true);

			analysisSubmissionServiceMock.update(submission);
			assertTrue("Samples have been updated", submission.getUpdateSamples());
		} catch (PostProcessingException e)
		{
			assertNotNull(e.toString());
		}
	}

	@Test
	public void getInputs() {
		Long submissionId = 1L;
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);

		AnalysisInputFiles inputFiles = analysisAjaxController.ajaxGetAnalysisInputFiles(submissionId);
		inputFiles.setReferenceFile(TestDataFactory.constructReferenceFile());
		Sample sample1 = TestDataFactory.constructSample();
		sample1.setId(1L);
		Sample sample2 = TestDataFactory.constructSample();
		sample1.setId(2L);

		AnalysisSamples newSample  = new AnalysisSamples(sample1.getSampleName(), sample1.getId(), null,null,null);
		AnalysisSamples anotherNewSample = new AnalysisSamples(sample2.getSampleName(), sample2.getId(), null,null,null);

		List<AnalysisSamples> samples = new ArrayList<>();
		samples.add(newSample);
		samples.add(anotherNewSample);

		inputFiles.setSamples(samples);

		assertEquals("There should be 2 samples for this submission", inputFiles.getSamples().size() , 2);
	}

}