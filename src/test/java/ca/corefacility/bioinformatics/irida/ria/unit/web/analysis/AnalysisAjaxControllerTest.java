package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletResponse;

import ca.corefacility.bioinformatics.irida.config.analysis.ExecutionManagerConfig;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;

import ca.corefacility.bioinformatics.irida.model.project.Project;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.AnalysisAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.*;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.auditing.AnalysisAudit;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.*;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
	private AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;
	private ExecutionManagerConfig configFileMock;
	private AnalysisAudit analysisAuditMock;
	private HttpServletResponse httpServletResponseMock;
	private AnalysisTypesService analysisTypesServiceMock;
	private EmailController emailControllerMock;

	/**
	 * Analysis Output File key names from
	 * {@link TestDataFactory#constructAnalysis()}
	 */
	private final List<String> outputNames = Lists.newArrayList("tree", "matrix", "table", "contigs-with-repeats",
			"refseq-masher-matches");

	@BeforeEach
	public void init() {
		analysisSubmissionServiceMock = mock(AnalysisSubmissionService.class);
		iridaWorkflowsServiceMock = mock(IridaWorkflowsService.class);
		projectServiceMock = mock(ProjectService.class);
		updatePermission = mock(UpdateAnalysisSubmissionPermission.class);
		sampleService = mock(SampleService.class);
		sequencingObjectService = mock(SequencingObjectService.class);
		analysisSubmissionSampleProcessor = mock(AnalysisSubmissionSampleProcessor.class);
		userServiceMock = mock(UserService.class);
		configFileMock = mock(ExecutionManagerConfig.class);
		MessageSource messageSourceMock = mock(MessageSource.class);
		analysisAuditMock = mock(AnalysisAudit.class);
		httpServletResponseMock = mock(HttpServletResponse.class);
		analysisTypesServiceMock = mock(AnalysisTypesService.class);
		emailControllerMock = mock(EmailController.class);

		analysisAjaxController = new AnalysisAjaxController(analysisSubmissionServiceMock, iridaWorkflowsServiceMock,
				userServiceMock, sampleService, projectServiceMock, updatePermission, metadataTemplateService,
				sequencingObjectService, analysisSubmissionSampleProcessor, messageSourceMock, configFileMock,
				analysisAuditMock, analysisTypesServiceMock, emailControllerMock);

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

		assertEquals(1, outputInfos.size(), "Should only be one output");

		AnalysisOutputFileInfo info = outputInfos.get(0);

		assertEquals("snp_tree.tree", info.getFilename(), "Should have proper filename");
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

		assertEquals(5, outputInfos.size(), "Should be 5 outputs");
	}

	// ************************************************************************************************
	// AJAX TESTS
	// ************************************************************************************************

	@Test
	public void TestGetAjaxDownloadAnalysisSubmission() {
		Long analysisSubmissionId = 1L;
		MockHttpServletResponse response = new MockHttpServletResponse();

		when(analysisSubmissionServiceMock.read(analysisSubmissionId))
				.thenReturn(TestDataFactory.constructAnalysisSubmission());
		try {
			analysisAjaxController.getAjaxDownloadAnalysisSubmission(analysisSubmissionId, response);
			assertEquals("application/zip", response.getContentType(), "Has the correct content type");
			assertEquals("attachment;filename=submission-5.zip", response.getHeader("Content-Disposition"),
					"Has the correct 'Content-Disposition' headers");
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
		when(iridaWorkflowsServiceMock.getOutputNames(workflowId)).thenReturn(outputNames);
		// get analysis output file summary info
		final List<AnalysisOutputFileInfo> infos = analysisAjaxController.getOutputFilesInfo(submissionId);
		assertEquals(5, infos.size(), "Expecting 5 analysis output file info items");
		final Optional<AnalysisOutputFileInfo> optInfo = infos.stream()
				.filter(x -> Objects.equals(x.getOutputName(), "refseq-masher-matches")).findFirst();
		assertTrue(optInfo.isPresent(), "Should be a refseq-masher-matches.tsv output file");
		final AnalysisOutputFileInfo info = optInfo.get();
		final String firstLine = "sample\ttop_taxonomy_name\tdistance\tpvalue\tmatching\tfull_taxonomy\ttaxonomic_subspecies\ttaxonomic_species\ttaxonomic_genus\ttaxonomic_family\ttaxonomic_order\ttaxonomic_class\ttaxonomic_phylum\ttaxonomic_superkingdom\tsubspecies\tserovar\tplasmid\tbioproject\tbiosample\ttaxid\tassembly_accession\tmatch_id";
		assertEquals(firstLine, info.getFirstLine(),
				"First line of file should be read since it has a tabular file extension");
		final Long seekTo = 290L;
		assertEquals(seekTo, info.getFilePointer(), "FilePointer should be first character of second line of file");
		assertEquals(Long.valueOf(61875), info.getFileSizeBytes(), "File size in bytes should be returned");
		final Long limit = 3L;
		final AnalysisOutputFileInfo lineInfo = analysisAjaxController.getOutputFile(submissionId, info.getId(), limit,
				0L, null, 0L, null, response);
		assertEquals(limit.intValue(), lineInfo.getLines().size());
		String expLine = "SRR1203042\tSalmonella enterica subsp. enterica serovar Abony str. 0014\t0.00650877\t0.0\t328/400\tBacteria; Proteobacteria; Gammaproteobacteria; Enterobacterales; Enterobacteriaceae; Salmonella; enterica; subsp. enterica; serovar Abony; str. 0014\tSalmonella enterica subsp. enterica\tSalmonella enterica\tSalmonella\tEnterobacteriaceae\tEnterobacterales\tGammaproteobacteria\tProteobacteria\tBacteria\tenterica\tAbony\t\tPRJNA224116\tSAMN01823751\t1029983\tGCF_000487615.2\t./rcn/refseq-NZ-1029983-PRJNA224116-SAMN01823751-GCF_000487615.2-.-Salmonella_enterica_subsp._enterica_serovar_Abony_str._0014.fna";
		assertEquals(expLine, lineInfo.getLines().get(0));
		// begin reading lines after first line file pointer position
		final AnalysisOutputFileInfo lineInfoRandomAccess = analysisAjaxController.getOutputFile(submissionId,
				info.getId(), limit, 0L, null, info.getFilePointer(), null, response);
		assertEquals(limit.intValue(), lineInfoRandomAccess.getLines().size(),
				"Using the RandomAccessFile reading method with seek>0, should give the same results as using a BufferedReader if both start reading at the same position");
		assertEquals(expLine, lineInfoRandomAccess.getLines().get(0),
				"Using the RandomAccessFile reading method with seek>0, should give the same results as using a BufferedReader if both start reading at the same position");
	}

	@Test
	public void testGetOutputFileByteSizedChunks() throws IridaWorkflowNotFoundException {
		final Long submissionId = 1L;
		final MockHttpServletResponse response = new MockHttpServletResponse();
		final AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		final UUID workflowId = submission.getWorkflowId();
		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getOutputNames(workflowId)).thenReturn(outputNames);
		// get analysis output file summary info
		final List<AnalysisOutputFileInfo> infos = analysisAjaxController.getOutputFilesInfo(submissionId);
		assertEquals(5, infos.size(), "Expecting 5 analysis output file info items");
		final Optional<AnalysisOutputFileInfo> optInfo = infos.stream()
				.filter(x -> Objects.equals(x.getOutputName(), "refseq-masher-matches")).findFirst();
		assertTrue(optInfo.isPresent(), "Should be a refseq-masher-matches.tsv output file");
		final AnalysisOutputFileInfo info = optInfo.get();
		final String firstLine = "sample\ttop_taxonomy_name\tdistance\tpvalue\tmatching\tfull_taxonomy\ttaxonomic_subspecies\ttaxonomic_species\ttaxonomic_genus\ttaxonomic_family\ttaxonomic_order\ttaxonomic_class\ttaxonomic_phylum\ttaxonomic_superkingdom\tsubspecies\tserovar\tplasmid\tbioproject\tbiosample\ttaxid\tassembly_accession\tmatch_id";
		assertEquals(firstLine, info.getFirstLine(),
				"First line of file should be read since it has a tabular file extension");
		final Long seekTo = 290L;
		final Long expFileSize = 61875L;
		assertEquals(seekTo, info.getFilePointer(), "FilePointer should be first character of second line of file");
		assertEquals(expFileSize, info.getFileSizeBytes(), "File size in bytes should be returned");
		final Long chunkSize = 10L;
		final AnalysisOutputFileInfo chunkInfo = analysisAjaxController.getOutputFile(submissionId, info.getId(), null,
				null, null, seekTo, chunkSize, response);
		assertEquals("SRR1203042", chunkInfo.getText(),
				"Should get the first 10 characters of the 2nd line starting at file pointer position 290");
		final long expFilePointer = seekTo + chunkSize;
		assertEquals(expFilePointer, chunkInfo.getFilePointer().longValue(),
				"After reading byte chunk of size x starting at position y, filePointer should be x+y");
		String nextTextChunk = "\tSalmonella enterica subsp. enterica serovar Abony str. 0014";
		final AnalysisOutputFileInfo nextChunkInfo = analysisAjaxController.getOutputFile(submissionId, info.getId(),
				null, null, null, chunkInfo.getFilePointer(), (long) nextTextChunk.length(), response);
		assertEquals(nextTextChunk, nextChunkInfo.getText(),
				"Should be able to continue reading from last file pointer position");
		final AnalysisOutputFileInfo lastChunkOfFile = analysisAjaxController.getOutputFile(submissionId, info.getId(),
				null, null, null, expFileSize - chunkSize, chunkSize, response);
		final String lastChunkText = "_str..fna\n";
		assertEquals(lastChunkText, lastChunkOfFile.getText(),
				"Should have successfully read the last chunk of the file");
		final AnalysisOutputFileInfo chunkOutsideRangeOfFile = analysisAjaxController.getOutputFile(submissionId,
				info.getId(), null, null, null, expFileSize + chunkSize, chunkSize, response);
		assertEquals("", chunkOutsideRangeOfFile.getText(),
				"Should return empty string since nothing can be read outside of file range");
		assertEquals(expFileSize + chunkSize, (long) chunkOutsideRangeOfFile.getStartSeek(),
				"Should have seeked to an position of file size + chunkSize");
		assertEquals(expFileSize + chunkSize, (long) chunkOutsideRangeOfFile.getFilePointer(),
				"FilePointer shouldn't have changed from startSeek");
	}

	@Test
	public void testUpdateAnalysisEmailPipelineResult() throws IridaWorkflowNotFoundException {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		AnalysisEmailPipelineResult res = new AnalysisEmailPipelineResult(submission.getId(), true, true);
		submission.setAnalysisState(AnalysisState.RUNNING);
		when(analysisSubmissionServiceMock.read(submission.getId())).thenReturn(submission);
		assertFalse(submission.getEmailPipelineResultCompleted(), "Email result on pipeline completion");
		assertFalse(submission.getEmailPipelineResultError(), "Email result on pipeline error");
		analysisAjaxController.ajaxUpdateEmailPipelineResult(res, Locale.getDefault(), httpServletResponseMock);
		submission.setEmailPipelineResultCompleted(res.getEmailPipelineResultCompleted());
		submission.setEmailPipelineResultError(res.getEmailPipelineResultError());
		verify(analysisSubmissionServiceMock, times(1)).update(submission);
	}

	@Test
	public void testUpdateAnalysisPriority() throws IridaWorkflowNotFoundException {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		AnalysisSubmissionInfo info = new AnalysisSubmissionInfo(submission.getId(), null,
				AnalysisSubmission.Priority.HIGH);
		submission.setAnalysisState(AnalysisState.NEW);
		when(analysisSubmissionServiceMock.read(submission.getId())).thenReturn(submission);
		assertEquals(submission.getPriority(), submission.getPriority(), "Priority should be medium");
		analysisAjaxController.ajaxUpdateSubmission(info, Locale.getDefault(), httpServletResponseMock);
		verify(analysisSubmissionServiceMock, times(1)).updatePriority(submission, info.getPriority());
	}

	@Test
	public void testUpdateAnalysisName() throws IridaWorkflowNotFoundException {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		AnalysisSubmissionInfo info = new AnalysisSubmissionInfo(submission.getId(), "NEW SUBMISSION NAME", null);
		submission.setAnalysisState(AnalysisState.NEW);
		when(analysisSubmissionServiceMock.read(submission.getId())).thenReturn(submission);
		assertEquals(submission.getName(), "submission-" + submission.getId(), "Submission name should be");
		analysisAjaxController.ajaxUpdateSubmission(info, Locale.getDefault(), httpServletResponseMock);
		submission.setName(info.getAnalysisName());
		verify(analysisSubmissionServiceMock, times(1)).update(submission);
	}

	@Test
	public void testGetAnalysisDetails() {
		final IridaWorkflowInput input = new IridaWorkflowInput("single", "paired", "reference", true);
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		IridaWorkflowDescription description = new IridaWorkflowDescription(submission.getWorkflowId(), "My Workflow",
				"V1", BuiltInAnalysisTypes.PHYLOGENOMICS, input, Lists.newArrayList(), Lists.newArrayList(),
				Lists.newArrayList());
		IridaWorkflow iridaWorkflow = new IridaWorkflow(description, null);
		submission.setAnalysisState(AnalysisState.COMPLETED);

		when(analysisSubmissionServiceMock.read(submission.getId())).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getIridaWorkflowOrUnknown(submission)).thenReturn(iridaWorkflow);

		analysisAjaxController.ajaxGetDataForDetailsTab(submission.getId(), Locale.getDefault(),
				httpServletResponseMock);
		assertNotNull(analysisSubmissionServiceMock.read(submission.getId()), "Submission exists");
		verify(iridaWorkflowsServiceMock, times(1)).getIridaWorkflowOrUnknown(submission);
		verify(analysisAuditMock, times(1)).getAnalysisRunningTime(submission);
		verify(analysisSubmissionSampleProcessor, times(1))
				.hasRegisteredAnalysisSampleUpdater(submission.getAnalysis().getAnalysisType());
	}

	@Test
	public void testDeleteAnalysisSubmission() {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		when(analysisSubmissionServiceMock.read(submission.getId())).thenReturn(submission);
		assertNotNull(analysisSubmissionServiceMock.read(submission.getId()), "Submission exists");
		analysisSubmissionServiceMock.delete(submission.getId());
		verify(analysisSubmissionServiceMock, times(1)).delete(submission.getId());
	}

	@Test
	public void updateSharedProjects() {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		Project project = TestDataFactory.constructProject();
		ProjectAnalysisSubmissionJoin paj = TestDataFactory.constructProjectAnalysisSubmissionJoin(project, submission);
		AnalysisProjectShare aPS = new AnalysisProjectShare(project.getId(), true);
		when(analysisSubmissionServiceMock.read(submission.getId())).thenReturn(submission);
		when(projectServiceMock.read(project.getId())).thenReturn(project);
		analysisAjaxController.updateProjectShare(submission.getId(), aPS, Locale.getDefault());
		assertNotNull(submission, "Submission exists");
		assertNotNull(project, "Project exists");
		when(analysisSubmissionServiceMock.shareAnalysisSubmissionWithProject(submission, project)).thenReturn(paj);
		verify(analysisSubmissionServiceMock, times(1)).read(submission.getId());
		verify(projectServiceMock, times(1)).read(project.getId());
		verify(analysisSubmissionServiceMock, times(1)).shareAnalysisSubmissionWithProject(submission, project);
	}

	@Test
	public void saveResultsToSamples() {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		when(analysisSubmissionServiceMock.read(submission.getId())).thenReturn(submission);
		analysisAjaxController.saveResultsToSamples(submission.getId(), Locale.getDefault(), httpServletResponseMock);
		try {
			submission.setUpdateSamples(true);
			verify(analysisSubmissionSampleProcessor, times(1)).updateSamples(submission);
			verify(analysisSubmissionServiceMock, times(1)).update(submission);
			assertTrue(submission.getUpdateSamples(), "Samples have been updated");
		} catch (PostProcessingException e) {
			assertNotNull(e.toString());
		}
	}

	@Test
	public void testGetAnalysisInputFiles() {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		when(analysisSubmissionServiceMock.read(submission.getId())).thenReturn(submission);
		analysisAjaxController.ajaxGetAnalysisInputFiles(submission.getId(), Locale.getDefault());
		assertNotNull(submission, "Submission exists");
		verify(sequencingObjectService, times(1)).getSequencingObjectsOfTypeForAnalysisSubmission(submission,
				SequenceFilePair.class);
		verify(iridaWorkflowsServiceMock, times(1)).getIridaWorkflowOrUnknown(submission);
	}

	@Test
	public void testProvenance() {
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		when(analysisSubmissionServiceMock.read(submission.getId())).thenReturn(submission);
		AnalysisProvenanceResponse provenance = analysisAjaxController.getProvenanceByFile(submission.getId(),
				"snp_tree.tree");
		assertTrue(provenance != null, "Provenance response is not null");
		assertTrue(provenance.getCreatedByTool().getToolName().equals("testTool"),
				"Provenance created by tool is 'testTool'");
		assertTrue(provenance.getCreatedByTool().getPreviousExecutionTools().size() == 0,
				"Provenance created by tool -> previousExecutionTools is null");
	}

}
