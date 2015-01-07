package ca.corefacility.bioinformatics.irida.model.enums.analysis.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.workflow.IridaWorkflowsGalaxyIntegrationTestConfig;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

/**
 * Integration tests for SNVPhyl pipeline.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class,
		IridaWorkflowsGalaxyIntegrationTestConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/model/enums/analysis/integration/SNVPhyl/SNVPhylAnalysisIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SNVPhylAnalysisIT {

	@Autowired
	private DatabaseSetupGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisExecutionService analysisExecutionService;

	@Autowired
	private IridaWorkflow snvPhylWorkflow;

	private AnalysisExecutionScheduledTask analysisExecutionScheduledTask;

	private Path sequenceFilePathA;
	private Path sequenceFilePathB;
	private Path sequenceFilePathC;
	private Path referenceFilePath;

	private Path outputSnpTable;
	private Path outputSnpMatrix;

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());

		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository,
				analysisExecutionService);

		Path sequenceFilePathRealA = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/input/fastq/a.fastq")
				.toURI());
		Path sequenceFilePathRealB = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/input/fastq/b.fastq")
				.toURI());
		Path sequenceFilePathRealC = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/input/fastq/c.fastq")
				.toURI());
		Path referenceFilePathReal = Paths.get(SNVPhylAnalysisIT.class.getResource(
				"SNVPhyl/test1/input/reference.fasta").toURI());

		sequenceFilePathA = Files.createTempFile("a", ".fastq");
		Files.delete(sequenceFilePathA);
		Files.copy(sequenceFilePathRealA, sequenceFilePathA);

		sequenceFilePathB = Files.createTempFile("b", ".fastq");
		Files.delete(sequenceFilePathB);
		Files.copy(sequenceFilePathRealB, sequenceFilePathB);

		sequenceFilePathC = Files.createTempFile("c", ".fastq");
		Files.delete(sequenceFilePathC);
		Files.copy(sequenceFilePathRealC, sequenceFilePathC);

		referenceFilePath = Files.createTempFile("reference", ".fasta");
		Files.delete(referenceFilePath);
		Files.copy(referenceFilePathReal, referenceFilePath);

		outputSnpTable = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output/snpTable.tsv").toURI());
		outputSnpMatrix = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output/snpMatrix.tsv").toURI());
	}

	private void waitUntilAnalysisStageComplete(Set<Future<AnalysisSubmission>> submissionsFutureSet)
			throws InterruptedException, ExecutionException {
		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			submissionFuture.get();
		}
	}

	private void completeSubmittedAnalyses() throws InterruptedException, ExecutionException {
		waitUntilAnalysisStageComplete(analysisExecutionScheduledTask.prepareAnalyses());
		waitUntilAnalysisStageComplete(analysisExecutionScheduledTask.executeAnalyses());
		waitUntilAnalysisStageComplete(analysisExecutionScheduledTask.monitorRunningAnalyses());
		waitUntilAnalysisStageComplete(analysisExecutionScheduledTask.transferAnalysesResults());
	}

	/**
	 * Tests out successfully executing the SNVPhyl pipeline.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSNVPhylSuccess() throws Exception {
		SequenceFile sequenceFileA = analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePathA).get(0);
		SequenceFile sequenceFileB = analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(2L,
				sequenceFilePathB).get(0);
		SequenceFile sequenceFileC = analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(3L,
				sequenceFilePathC).get(0);

		AnalysisSubmission submission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				Sets.newHashSet(sequenceFileA, sequenceFileB, sequenceFileC), referenceFilePath,
				snvPhylWorkflow.getWorkflowIdentifier());

		completeSubmittedAnalyses();

		submission = analysisSubmissionRepository.findOne(submission.getId());
		assertEquals(AnalysisState.COMPLETED, submission.getAnalysisState());

		Analysis analysis = submission.getAnalysis();
		assertEquals(AnalysisPhylogenomicsPipeline.class, analysis.getClass());
		AnalysisPhylogenomicsPipeline analysisPhylogenomics = (AnalysisPhylogenomicsPipeline) analysis;

		assertEquals(3, analysisPhylogenomics.getAnalysisOutputFiles().size());
		assertTrue(
				"snpMatrix should be the same",
				com.google.common.io.Files.equal(outputSnpMatrix.toFile(), analysisPhylogenomics.getSnpMatrix()
						.getFile().toFile()));
		assertTrue(
				"snpTable should be the same",
				com.google.common.io.Files.equal(outputSnpTable.toFile(), analysisPhylogenomics.getSnpTable().getFile()
						.toFile()));
		assertNotNull(analysisPhylogenomics.getPhylogeneticTree().getFile());
	}
}
