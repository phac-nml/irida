package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.integration;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowGalaxyPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServiceGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.service.analysis.prepration.galaxy.phylogenomics.impl.GalaxyWorkflowPreparationServicePhylogenomicsPipeline;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class, WindowsLocalGalaxyConfig.class  })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class AnalysisExecutionServiceGalaxyPhylogenomicsPipelineIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;
	
	private Path dataFile;
	private Path referenceFile;
	private Set<SequenceFile> sequenceFiles;
	
	private AnalysisExecutionServiceGalaxyPhylogenomicsPipeline workflowManagement;
	
	@Before
	public void setup() throws URISyntaxException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		
		dataFile = Paths.get(AnalysisExecutionServiceGalaxyPhylogenomicsPipelineIT.class.getResource(
				"testData1.fastq").toURI());
		referenceFile = Paths.get(AnalysisExecutionServiceGalaxyPhylogenomicsPipelineIT.class.getResource(
				"testReference.fasta").toURI());
				
		sequenceFiles = new HashSet<>();
		sequenceFiles.add(new SequenceFile(dataFile));
		
		buildWorkflowManagementGalaxy();
		
//		invalidSubmittedAnalysis = new AnalysisSubmissionGalaxyPhylogenomicsPipeline(new GalaxyAnalysisId("invalid"), null);
	}
	
	private GalaxyHistoriesService buildGalaxyHistoriesService() {
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();
		return new GalaxyHistoriesService(historiesClient, toolsClient);
	}
	
	private GalaxyWorkflowService buildGalaxyWorkflowService() {
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		
		return new GalaxyWorkflowService(historiesClient, workflowsClient,
						new StandardPasswordEncoder());
	}
	
	private void buildWorkflowManagementGalaxy() {
		GalaxyHistoriesService galaxyHistoriesService = buildGalaxyHistoriesService();
		GalaxyWorkflowService galaxyWorkflowService = buildGalaxyWorkflowService();
		
		GalaxyWorkflowPreparationServicePhylogenomicsPipeline galaxyWorkflowPreparationServicePhylogenomicsPipeline = 
				new GalaxyWorkflowPreparationServicePhylogenomicsPipeline(galaxyHistoriesService, galaxyWorkflowService);
		workflowManagement = new AnalysisExecutionServiceGalaxyPhylogenomicsPipeline(galaxyWorkflowService,
				galaxyHistoriesService, galaxyWorkflowPreparationServicePhylogenomicsPipeline);
	}
	
	private AnalysisSubmissionGalaxyPhylogenomicsPipeline buildAnalysisSubmission() {
		
		String sequenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestSequenceFilesLabel();
		String referenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestReferenceLabel();
		
		RemoteWorkflowGalaxyPhylogenomics remoteWorkflow =
				new RemoteWorkflowGalaxyPhylogenomics(localGalaxy.getWorkflowCorePipelineTestId(),
				localGalaxy.getWorkflowCorePipelineTestChecksum(),
				sequenceFileInputLabel, referenceFileInputLabel);

		
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission = 
				new AnalysisSubmissionGalaxyPhylogenomicsPipeline(sequenceFiles,
						new ReferenceFile(referenceFile),remoteWorkflow);
		analysisSubmission.setInputFiles(sequenceFiles);
		analysisSubmission.setReferenceFile(new ReferenceFile(referenceFile));
		analysisSubmission.setRemoteWorkflow(remoteWorkflow);
		
		return analysisSubmission;
	}
	
	private void waitForAnalysisFinished(AnalysisSubmissionGalaxyPhylogenomicsPipeline analysis) throws InterruptedException, ExecutionManagerException {
		final int max = 1000;
		final int time = 5000;
		for (int i = 0; i < max; i++) {
			WorkflowStatus status = workflowManagement.getWorkflowStatus(analysis);
			if (WorkflowState.OK.equals(status.getState())) {
				break;
			}
			Thread.sleep(time);
		}
	}
	
	/**
	 * Asserts that the given status is in a valid state for a workflow.
	 * @param status
	 */
	private void assertValidStatus(WorkflowStatus status) {
		assertNotNull(status);
		assertFalse(WorkflowState.UNKNOWN.equals(status.getState()));
		float percentComplete = status.getPercentComplete();
		assertTrue(0.0f <= percentComplete && percentComplete <= 100.0f);
	}
	
	/**
	 * Tests out successfully submitting a workflow for execution.
	 * @throws InterruptedException 
	 * @throws ExecutionManagerException 
	 */
	@Test
	public void testExecuteAnalysisSuccess() throws InterruptedException, ExecutionManagerException {
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission = buildAnalysisSubmission();
		
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmitted = workflowManagement.executeAnalysis(analysisSubmission);
		assertNotNull(analysisSubmitted);
		assertNotNull(analysisSubmitted.getRemoteAnalysisId());
		
		WorkflowOutputs output = analysisSubmitted.getOutputs();
		assertNotNull(output);
		WorkflowStatus status = workflowManagement.getWorkflowStatus(analysisSubmitted);
		assertValidStatus(status);
	}
	
	/**
	 * Tests getting results from an executed analysis.
	 * @throws ExecutionManagerException
	 * @throws InterruptedException
	 */
	@Ignore
	@Test
	public void testGetAnalysisResultsSuccess() throws ExecutionManagerException, InterruptedException {
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission = buildAnalysisSubmission();
		
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmitted = workflowManagement.executeAnalysis(analysisSubmission);
		assertNotNull(analysisSubmitted);
		
		WorkflowStatus status = workflowManagement.getWorkflowStatus(analysisSubmitted);
		assertValidStatus(status);
		
		waitForAnalysisFinished(analysisSubmitted);
		
//		AnalysisPhylogenomicsPipeline analysisResults = (AnalysisPhylogenomicsPipeline)analysis;
		
//		Path outputFile = analysisResults.getOutputFile();
//		assertNotNull(outputFile);
//		assertTrue(outputFile.toFile().exists());
	}

	/**
	 * Tests out attempting to submit an invalid workflow for execution.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=WorkflowException.class)
	public void testExecuteAnalysisFailInvalidWorkflow() throws ExecutionManagerException {
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission = buildAnalysisSubmission();
		analysisSubmission.getRemoteWorkflow().
			setWorkflowId(localGalaxy.getInvalidWorkflowId());
		
		workflowManagement.executeAnalysis(analysisSubmission);
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws URISyntaxException, ExecutionManagerException, InterruptedException, IOException {
		StandardPasswordEncoder passwordCoder = new StandardPasswordEncoder();
		String workflowId = "ebfb8f50c6abde6d";
		
		GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get("http://localhost:8888", "3196a4a894a8bccbefc32ccff25ddf9d");
		WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();
		HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();
		ToolsClient toolsClient = galaxyInstance.getToolsClient();
		
		String checksum = passwordCoder.encode(workflowsClient.exportWorkflow(workflowId));
		
		GalaxyHistoriesService historiesService = new GalaxyHistoriesService(historiesClient, toolsClient);
		GalaxyWorkflowService workflowService = new GalaxyWorkflowService(historiesClient, workflowsClient,
				new StandardPasswordEncoder());
		
		GalaxyWorkflowPreparationServicePhylogenomicsPipeline galaxyWorkflowPreparationServicePhylogenomicsPipeline =
				new GalaxyWorkflowPreparationServicePhylogenomicsPipeline(historiesService, workflowService);
		AnalysisExecutionServiceGalaxyPhylogenomicsPipeline analysisService = 
				new AnalysisExecutionServiceGalaxyPhylogenomicsPipeline(workflowService, historiesService, 
						galaxyWorkflowPreparationServicePhylogenomicsPipeline);
		
		SequenceFile a1 = new SequenceFile(Paths.get(new URI("file:////home/aaron/workspace/irida-api/1/input/fastq/a_1.fastq")));
		SequenceFile a2 = new SequenceFile(Paths.get(new URI("file:////home/aaron/workspace/irida-api/1/input/fastq/a_2.fastq")));
		SequenceFile b1 = new SequenceFile(Paths.get(new URI("file:////home/aaron/workspace/irida-api/1/input/fastq/b_1.fastq")));
		SequenceFile b2 = new SequenceFile(Paths.get(new URI("file:////home/aaron/workspace/irida-api/1/input/fastq/b_2.fastq")));
		SequenceFile c1 = new SequenceFile(Paths.get(new URI("file:////home/aaron/workspace/irida-api/1/input/fastq/c_1.fastq")));
		SequenceFile c2 = new SequenceFile(Paths.get(new URI("file:////home/aaron/workspace/irida-api/1/input/fastq/c_2.fastq")));
		ReferenceFile reference = new ReferenceFile(Paths.get(new URI("file:////home/aaron/workspace/irida-api/1/input/reference.fasta")));
		
		Set<SequenceFile> sequenceFiles = new HashSet<>();
		sequenceFiles.addAll(Lists.newArrayList(a1,b1,c1));
		
		String sequenceFileInputLabel = "sequence_reads";
		String referenceFileInputLabel = "reference";
		
		RemoteWorkflowGalaxyPhylogenomics remoteWorkflow =
				new RemoteWorkflowGalaxyPhylogenomics(workflowId,checksum,
						sequenceFileInputLabel, referenceFileInputLabel);

		
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission = 
				new AnalysisSubmissionGalaxyPhylogenomicsPipeline(sequenceFiles,
						reference, remoteWorkflow);
		
		analysisService.executeAnalysis(analysisSubmission);
		
		final int max = 1000;
		final int time = 5000;
		for (int i = 0; i < max; i++) {
			WorkflowStatus status = analysisService.getWorkflowStatus(analysisSubmission);
			if (WorkflowState.OK.equals(status.getState())) {
				break;
			}
			Thread.sleep(time);
		}
		
		WorkflowOutputs outputs = analysisSubmission.getOutputs();
		for (String outputId : outputs.getOutputIds()) {
			System.out.println("output:"+ outputId);
			
			Dataset dataset = historiesClient.showDataset(analysisSubmission.getRemoteAnalysisId().getValue(), outputId);
			String name = dataset.getName();
			System.out.println("\t"+name);
			
			if ("pseudo-positions.tsv".equals(name)) {
				URL url = new URL(dataset.getFullDownloadUrl());
				
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				InputStream stream = con.getInputStream();

				String galaxyFileContents = readFileContentsFromReader(new BufferedReader(
						new InputStreamReader(stream)));
				
				System.out.println(galaxyFileContents);
			}
		}
	}
	
	private static String readFileContentsFromReader(BufferedReader reader)
			throws IOException {
		String line;
		String contents = "";
		while ((line = reader.readLine()) != null) {
			contents += line + "\n";
		}

		return contents;
	}
}
