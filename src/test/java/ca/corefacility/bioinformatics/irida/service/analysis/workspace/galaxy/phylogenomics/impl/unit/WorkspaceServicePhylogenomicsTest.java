package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.phylogenomics.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.phylogenomics.impl.WorkspaceServicePhylogenomics;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;

/**
 * Tests out preparing a Galaxy Phylogenomics Pipeline workflow for execution.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkspaceServicePhylogenomicsTest {

	@Mock private GalaxyHistoriesService galaxyHistoriesService;
	@Mock private GalaxyWorkflowService galaxyWorkflowService;
	@Mock private List<Dataset> sequenceDatasets;
	@Mock private Dataset refDataset;
	
	private WorkspaceServicePhylogenomics
		workflowPreparation;
	
	private Set<SequenceFile> inputFiles;
	private ReferenceFile referenceFile;
	private Path refFile;
	private RemoteWorkflowPhylogenomics remoteWorkflow;
	private AnalysisSubmissionPhylogenomics submission;
	private CollectionResponse collectionResponse;
	private WorkflowDetails workflowDetails;
		
	private History workflowHistory;
	
	private static final String HISTORY_ID = "10";
	private static final String WORKFLOW_ID = "11";
	private static final String SEQUENCE_FILE_LABEL = "sequence_files";
	private static final String REFERENCE_FILE_LABEL = "reference_files";
	private static final String SEQUENCE_FILE_ID = "12";
	private static final String REFERENCE_FILE_ID = "13";
	
	private static final String TREE_LABEL = "tree";
	private static final String MATRIX_LABEL = "snp_matrix";
	private static final String TABLE_LABEL = "snp_table";
	
	/**
	 * Sets up variables for testing.
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
		MockitoAnnotations.initMocks(this);

		SequenceFile sFileA = new SequenceFile(createTempFile("fileA" , "fastq"));
		SequenceFile sFileB = new SequenceFile(createTempFile("fileB" , "fastq"));
		SequenceFile sFileC = new SequenceFile(createTempFile("fileC" , "fastq"));
		
		refFile = createTempFile("reference", "fasta");
		referenceFile = new ReferenceFile(refFile);
		
		inputFiles = new HashSet<>();
		inputFiles.addAll(Arrays.asList(sFileA, sFileB, sFileC));
		
		String workflowChecksum = "1";
		
		remoteWorkflow = new RemoteWorkflowPhylogenomics(WORKFLOW_ID,
				workflowChecksum, SEQUENCE_FILE_LABEL, REFERENCE_FILE_LABEL,
				TREE_LABEL, MATRIX_LABEL, TABLE_LABEL);
		
		submission = new AnalysisSubmissionPhylogenomics(
			inputFiles, referenceFile, remoteWorkflow);
		
		workflowHistory = new History();
		workflowHistory.setId(HISTORY_ID);
		
		collectionResponse = new CollectionResponse();
		collectionResponse.setId("1");
		
		workflowDetails = new WorkflowDetails();
		workflowDetails.setId(WORKFLOW_ID);
		
		workflowPreparation = 
				new WorkspaceServicePhylogenomics(
						galaxyHistoriesService, galaxyWorkflowService);
	}
	
	private Path createTempFile(String prefix, String suffix) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		file.deleteOnExit();
		
		return file.toPath();
	}
	
	/**
	 * Tests out successfully to preparing an analysis
	 * @throws ExecutionManagerException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPrepareAnalysisWorkspaceSuccess() throws ExecutionManagerException {
		when(galaxyHistoriesService.newHistoryForWorkflow()).thenReturn(workflowHistory);
		when(galaxyHistoriesService.uploadFilesListToHistory(
				any(List.class), eq(InputFileType.FASTQ_SANGER), eq(workflowHistory))).
				thenReturn(sequenceDatasets);
		when(galaxyHistoriesService.fileToHistory(
				refFile, InputFileType.FASTA, workflowHistory)).thenReturn(refDataset);
		when(galaxyHistoriesService.constructCollectionList(
				any(List.class), eq(workflowHistory))).thenReturn(collectionResponse);
		when(galaxyWorkflowService.getWorkflowDetails(WORKFLOW_ID)).thenReturn(workflowDetails);
		when(galaxyWorkflowService.getWorkflowInputId(
				workflowDetails, SEQUENCE_FILE_LABEL)).thenReturn(SEQUENCE_FILE_ID);
		when(galaxyWorkflowService.getWorkflowInputId(
				workflowDetails, REFERENCE_FILE_LABEL)).thenReturn(REFERENCE_FILE_ID);
		
		PreparedWorkflowGalaxy preparedWorkflow = workflowPreparation.prepareAnalysisWorkspace(submission);
		assertEquals("preparedWorflow history id not equal to " + HISTORY_ID,
				HISTORY_ID, preparedWorkflow.getRemoteAnalysisId());
		assertNotNull("workflowInputs in preparedWorkflow is null", preparedWorkflow.getWorkflowInputs());
	}
	
	/**
	 * Tests out failing to prepare an analysis.
	 * @throws ExecutionManagerException
	 */
	@SuppressWarnings("unchecked")
	@Test(expected=UploadException.class)
	public void testPrepareAnalysisWorkspaceFail() throws ExecutionManagerException {
		when(galaxyHistoriesService.newHistoryForWorkflow()).thenReturn(workflowHistory);
		when(galaxyHistoriesService.uploadFilesListToHistory(
				any(List.class), eq(InputFileType.FASTQ_SANGER), eq(workflowHistory))).
				thenThrow(new UploadException());
		
		workflowPreparation.prepareAnalysisWorkspace(submission);
	}
}
