package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.AnalysisController;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysisOutputFileInfo;
import ca.corefacility.bioinformatics.irida.ria.web.components.AnalysisOutputFileDownloadManager;
import ca.corefacility.bioinformatics.irida.ria.web.services.AnalysesListingService;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class AnalysisControllerTest {
	/*
	 * CONTROLLER
	 */
	private AnalysisController analysisController;

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
		analysesListingService = mock(AnalysesListingService.class);
		analysisSubmissionSampleProcessor = mock(AnalysisSubmissionSampleProcessor.class);
		analysisOutputFileDownloadManager = mock(AnalysisOutputFileDownloadManager.class);
		MessageSource messageSourceMock = mock(MessageSource.class);
		analysisController = new AnalysisController(analysisSubmissionServiceMock, iridaWorkflowsServiceMock,
				userServiceMock, sampleService, projectServiceMock, updatePermission, metadataTemplateService,
				sequencingObjectService, analysesListingService, analysisSubmissionSampleProcessor,
				analysisOutputFileDownloadManager, messageSourceMock);
	}

	@Test
	public void testGetAnalysisDetailsTree() throws IOException, IridaWorkflowNotFoundException {
		Long submissionId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		Locale locale = Locale.ENGLISH;

		final IridaWorkflowInput input = new IridaWorkflowInput("single", "paired", "reference", true);
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		IridaWorkflowDescription description = new IridaWorkflowDescription(submission.getWorkflowId(), "My Workflow",
				"V1", BuiltInAnalysisTypes.PHYLOGENOMICS, input, Lists.newArrayList(), Lists.newArrayList(),
				Lists.newArrayList());
		IridaWorkflow iridaWorkflow = new IridaWorkflow(description, null);
		submission.setAnalysisState(AnalysisState.COMPLETED);

		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getIridaWorkflowOrUnknown(submission)).thenReturn(iridaWorkflow);

		String detailsPage = analysisController.getDetailsPage(submissionId, model, locale);
		assertEquals("should be details page", AnalysisController.PAGE_DETAILS_DIRECTORY + "tree", detailsPage);

		assertEquals("Tree preview should be set", "tree", model.get("preview"));

		assertEquals("submission should be in model", submission, model.get("analysisSubmission"));
		
		assertEquals("submission reference file should be in model.", submission.getReferenceFile().get(), model.get("referenceFile"));

		assertEquals("analysisType should be PHYLOGENOMICS", BuiltInAnalysisTypes.PHYLOGENOMICS,
				model.get("analysisType"));
	}

	@Test
	public void testGetAnalysisDetailsNotCompleted() throws IOException, IridaWorkflowNotFoundException {
		Long submissionId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		Locale locale = Locale.ENGLISH;

		final IridaWorkflowInput input = new IridaWorkflowInput("single", "paired", "reference", true);
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		IridaWorkflowDescription description = new IridaWorkflowDescription(submission.getWorkflowId(), "My Workflow",
				"V1", BuiltInAnalysisTypes.PHYLOGENOMICS, input, Lists.newArrayList(), Lists.newArrayList(),
				Lists.newArrayList());
		IridaWorkflow iridaWorkflow = new IridaWorkflow(description, null);
		submission.setAnalysisState(AnalysisState.RUNNING);

		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getIridaWorkflowOrUnknown(submission)).thenReturn(iridaWorkflow);

		String detailsPage = analysisController.getDetailsPage(submissionId, model, locale);
		assertEquals("should be details page", AnalysisController.PAGE_DETAILS_DIRECTORY + "tree", detailsPage);

		assertFalse("No preview should be available", model.containsAttribute("preview"));

		assertEquals("submission should be in model", submission, model.get("analysisSubmission"));
	}

	@Test
	public void testGetAnalysisDetailsMissingPipeline() throws IOException, IridaWorkflowNotFoundException {
		Long submissionId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		Locale locale = Locale.ENGLISH;
		UUID workflowId = UUID.randomUUID();

		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission(workflowId);
		submission.setAnalysisState(AnalysisState.COMPLETED);

		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getIridaWorkflowOrUnknown(submission))
				.thenReturn(createUnknownWorkflow(workflowId));

		String detailsPage = analysisController.getDetailsPage(submissionId, model, locale);
		assertEquals("should be details page", AnalysisController.PAGE_DETAILS_DIRECTORY + "unavailable", detailsPage);

		assertFalse("No preview should be set", model.containsAttribute("preview"));

		assertEquals("submission should be in model", submission, model.get("analysisSubmission"));

		assertEquals("version should be unknown", "unknown", model.get("version"));
		assertEquals("analysisType should be UNKNOWN", BuiltInAnalysisTypes.UNKNOWN, model.get("analysisType"));
	}

	private IridaWorkflow createUnknownWorkflow(UUID workflowId) {
		return new IridaWorkflow(
				new IridaWorkflowDescription(workflowId, "unknown", "unknown", BuiltInAnalysisTypes.UNKNOWN,
						new IridaWorkflowInput(), Lists.newLinkedList(), Lists.newLinkedList(), Lists.newLinkedList()),
				new IridaWorkflowStructure(null));
	}

	@Test
	public void getOutputFilesInfoSuccess() throws IridaWorkflowNotFoundException {
		Long submissionId = 1L;

		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		submission.setAnalysisState(AnalysisState.COMPLETED);

		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getOutputNames(submission.getWorkflowId()))
				.thenReturn(Lists.newArrayList("tree"));

		List<AnalysisOutputFileInfo> outputInfos = analysisController.getOutputFilesInfo(submissionId);

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

		List<AnalysisOutputFileInfo> outputInfos = analysisController.getOutputFilesInfo(submissionId);

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
			analysisController.getAjaxDownloadAnalysisSubmission(analysisSubmissionId, response);
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
		final List<AnalysisOutputFileInfo> infos = analysisController.getOutputFilesInfo(submissionId);
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
		final AnalysisOutputFileInfo lineInfo = analysisController.getOutputFile(submissionId, info.getId(), limit, 0L,
				null, 0L, null, response);
		assertEquals(limit.intValue(), lineInfo.getLines()
				.size());
		String expLine = "SRR1203042\tSalmonella enterica subsp. enterica serovar Abony str. 0014\t0.00650877\t0.0\t328/400\tBacteria; Proteobacteria; Gammaproteobacteria; Enterobacterales; Enterobacteriaceae; Salmonella; enterica; subsp. enterica; serovar Abony; str. 0014\tSalmonella enterica subsp. enterica\tSalmonella enterica\tSalmonella\tEnterobacteriaceae\tEnterobacterales\tGammaproteobacteria\tProteobacteria\tBacteria\tenterica\tAbony\t\tPRJNA224116\tSAMN01823751\t1029983\tGCF_000487615.2\t./rcn/refseq-NZ-1029983-PRJNA224116-SAMN01823751-GCF_000487615.2-.-Salmonella_enterica_subsp._enterica_serovar_Abony_str._0014.fna";
		assertEquals(expLine, lineInfo.getLines()
				.get(0));
		// begin reading lines after first line file pointer position
		final AnalysisOutputFileInfo lineInfoRandomAccess = analysisController.getOutputFile(submissionId, info.getId(),
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
		final List<AnalysisOutputFileInfo> infos = analysisController.getOutputFilesInfo(submissionId);
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
		final AnalysisOutputFileInfo chunkInfo = analysisController.getOutputFile(submissionId, info.getId(), null,
				null, null, seekTo, chunkSize, response);
		assertEquals("Should get the first 10 characters of the 2nd line starting at file pointer position 290",
				"SRR1203042", chunkInfo.getText());
		final long expFilePointer = seekTo + chunkSize;
		assertEquals("After reading byte chunk of size x starting at position y, filePointer should be x+y",
				expFilePointer, chunkInfo.getFilePointer()
						.longValue());
		String nextTextChunk = "\tSalmonella enterica subsp. enterica serovar Abony str. 0014";
		final AnalysisOutputFileInfo nextChunkInfo = analysisController.getOutputFile(submissionId, info.getId(), null,
				null, null, chunkInfo.getFilePointer(), (long) nextTextChunk.length(), response);
		assertEquals("Should be able to continue reading from last file pointer position", nextTextChunk,
				nextChunkInfo.getText());
		final AnalysisOutputFileInfo lastChunkOfFile = analysisController.getOutputFile(submissionId, info.getId(), null,
				null, null, expFileSize - chunkSize, chunkSize, response);
		final String lastChunkText = "_str..fna\n";
		assertEquals("Should have successfully read the last chunk of the file",
				lastChunkText, lastChunkOfFile.getText());
		final AnalysisOutputFileInfo chunkOutsideRangeOfFile = analysisController.getOutputFile(submissionId, info.getId(), null,
				null, null, expFileSize + chunkSize, chunkSize, response);
		assertEquals("Should return empty string since nothing can be read outside of file range",
				"", chunkOutsideRangeOfFile.getText());
		assertEquals("Should have seeked to an position of file size + chunkSize",
				expFileSize + chunkSize, (long) chunkOutsideRangeOfFile.getStartSeek());
		assertEquals("FilePointer shouldn't have changed from startSeek",
				expFileSize + chunkSize, (long) chunkOutsideRangeOfFile.getFilePointer());
	}
}
