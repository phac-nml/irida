package ca.corefacility.bioinformatics.irida.model.enums.analysis.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ToolExecution;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyJobErrorsService;
import ca.corefacility.bioinformatics.irida.processing.impl.GzipFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.JobErrorRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.CleanupAnalysisSubmissionCondition;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;

/**
 * Integration tests for SNVPhyl pipeline.
 * 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/model/enums/analysis/integration/SNVPhyl/SNVPhylAnalysisIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SNVPhylAnalysisIT {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SNVPhylAnalysisIT.class);
	
	private static final String MATRIX_KEY = "matrix";
	private static final String TREE_KEY = "tree";
	private static final String TABLE_KEY = "table";
	private static final String CORE_KEY = "core";
	private static final String QUALITY_KEY = "mapping-quality";
	private static final String STATS_KEY = "filter-stats";
	private static final String ALIGN_KEY = "alignment";
	
	@Autowired
	private DatabaseSetupGalaxyITService databaseSetupGalaxyITService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisExecutionService analysisExecutionService;

	@Autowired
	private IridaWorkflow snvPhylWorkflow;

	@Autowired
	@Qualifier("rootTempDirectory")
	private Path rootTempDirectory;
	
	@Autowired
	private SequencingObjectService sequencingObjectService;

	@Autowired
	private GalaxyJobErrorsService galaxyJobErrorsService;

	@Autowired
	private JobErrorRepository jobErrorRepository;

	@Autowired
	private EmailController emailController;
	
	@Autowired
	private GzipFileProcessor gzipFileProcessor;

	private AnalysisExecutionScheduledTask analysisExecutionScheduledTask;

	private Path sequenceFilePathA1;
	private Path sequenceFilePathA2;
	private Path sequenceFilePathB1;
	private Path sequenceFilePathB2;
	private Path sequenceFilePathC1;
	private Path sequenceFilePathC2;
	private Path referenceFilePath;
	
	private List<Path> sequenceFilePathsA1List;
	private List<Path> sequenceFilePathsA2List;
	private List<Path> sequenceFilePathsB1List;
	private List<Path> sequenceFilePathsB2List;
	private List<Path> sequenceFilePathsC1List;
	private List<Path> sequenceFilePathsC2List;

	private Path outputSnvTable1;
	private Path outputSnvMatrix1;
	private Path vcf2core1;
	private Path filterStats1;
	private Path snvAlign1;
	
	private Path outputSnvTable2;
	private Path outputSnvMatrix2;
	private Path vcf2core2;
	private Path filterStats2;
	private Path snvAlign2;
	
	private Path outputSnvTable3;
	private Path outputSnvMatrix3;
	private Path vcf2core3;
	private Path filterStats3;
	private Path snvAlign3;

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
				analysisExecutionService, CleanupAnalysisSubmissionCondition.NEVER_CLEANUP, galaxyJobErrorsService, jobErrorRepository, emailController);

		Path tempDir = Files.createTempDirectory(rootTempDirectory, "snvphylTest");

		Path sequenceFilePathRealA1 = Paths.get(SNVPhylAnalysisIT.class.getResource(
				"SNVPhyl/test1/input/fastq/a_1.fastq").toURI());
		Path sequenceFilePathRealA2 = Paths.get(SNVPhylAnalysisIT.class.getResource(
				"SNVPhyl/test1/input/fastq/a_2.fastq").toURI());
		Path sequenceFilePathRealB1 = Paths.get(SNVPhylAnalysisIT.class.getResource(
				"SNVPhyl/test1/input/fastq/b_1.fastq.gz").toURI());
		Path sequenceFilePathRealB2 = Paths.get(SNVPhylAnalysisIT.class.getResource(
				"SNVPhyl/test1/input/fastq/b_2.fastq.gz").toURI());
		Path sequenceFilePathRealC1 = Paths.get(SNVPhylAnalysisIT.class.getResource(
				"SNVPhyl/test1/input/fastq/c_1.fastq").toURI());
		Path sequenceFilePathRealC2 = Paths.get(SNVPhylAnalysisIT.class.getResource(
				"SNVPhyl/test1/input/fastq/c_2.fastq").toURI());
		Path referenceFilePathReal = Paths.get(SNVPhylAnalysisIT.class.getResource(
				"SNVPhyl/test1/input/reference.fasta").toURI());

		sequenceFilePathA1 = tempDir.resolve("a_R1_001.fastq");
		Files.copy(sequenceFilePathRealA1, sequenceFilePathA1, StandardCopyOption.REPLACE_EXISTING);
		sequenceFilePathA2 = tempDir.resolve("a_R2_001.fastq");
		Files.copy(sequenceFilePathRealA2, sequenceFilePathA2, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePathB1 = tempDir.resolve("b_R1_001.fastq.gz");
		Files.copy(sequenceFilePathRealB1, sequenceFilePathB1, StandardCopyOption.REPLACE_EXISTING);
		sequenceFilePathB2 = tempDir.resolve("b_R2_001.fastq.gz");
		Files.copy(sequenceFilePathRealB2, sequenceFilePathB2, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePathC1 = tempDir.resolve("c_R1_001.fastq");
		Files.copy(sequenceFilePathRealC1, sequenceFilePathC1, StandardCopyOption.REPLACE_EXISTING);
		sequenceFilePathC2 = tempDir.resolve("c_R2_001.fastq");
		Files.copy(sequenceFilePathRealC2, sequenceFilePathC2, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePathsA1List = new LinkedList<>();
		sequenceFilePathsA1List.add(sequenceFilePathA1);
		sequenceFilePathsA2List = new LinkedList<>();
		sequenceFilePathsA2List.add(sequenceFilePathA2);

		sequenceFilePathsB1List = new LinkedList<>();
		sequenceFilePathsB1List.add(sequenceFilePathB1);
		sequenceFilePathsB2List = new LinkedList<>();
		sequenceFilePathsB2List.add(sequenceFilePathB2);

		sequenceFilePathsC1List = new LinkedList<>();
		sequenceFilePathsC1List.add(sequenceFilePathC1);
		sequenceFilePathsC2List = new LinkedList<>();
		sequenceFilePathsC2List.add(sequenceFilePathC2);

		referenceFilePath = Files.createTempFile("reference", ".fasta");
		Files.copy(referenceFilePathReal, referenceFilePath, StandardCopyOption.REPLACE_EXISTING);

		outputSnvTable1 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output1/snvTable.tsv").toURI());
		outputSnvMatrix1 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output1/snvMatrix.tsv").toURI());
		vcf2core1 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output1/vcf2core.tsv").toURI());
		filterStats1 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output1/filterStats.txt").toURI());
		snvAlign1 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output1/snvAlignment.phy").toURI());
		
		outputSnvTable2 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output2/snvTable.tsv").toURI());
		outputSnvMatrix2 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output2/snvMatrix.tsv").toURI());
		vcf2core2 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output2/vcf2core.tsv").toURI());
		filterStats2 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output2/filterStats.txt").toURI());
		snvAlign2 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output2/snvAlignment.phy").toURI());
		
		outputSnvTable3 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output3/snvTable.tsv").toURI());
		outputSnvMatrix3 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output3/snvMatrix.tsv").toURI());
		vcf2core3 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output3/vcf2core.tsv").toURI());
		filterStats3 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output3/filterStats.txt").toURI());
		snvAlign3 = Paths.get(SNVPhylAnalysisIT.class.getResource("SNVPhyl/test1/output3/snvAlignment.phy").toURI());
		
		gzipFileProcessor.setDisableFileProcessor(false);
	}

	private void waitUntilAnalysisStageComplete(Set<Future<AnalysisSubmission>> submissionsFutureSet)
			throws InterruptedException, ExecutionException {
		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			submissionFuture.get();
		}
	}

	private void completeSubmittedAnalyses(Long submissionId) throws Exception {
		waitUntilAnalysisStageComplete(analysisExecutionScheduledTask.prepareAnalyses());
		waitUntilAnalysisStageComplete(analysisExecutionScheduledTask.executeAnalyses());

		AnalysisSubmission submission = analysisSubmissionRepository.findOne(submissionId);

		databaseSetupGalaxyITService.waitUntilSubmissionComplete(submission);
		waitUntilAnalysisStageComplete(analysisExecutionScheduledTask.monitorRunningAnalyses());
		waitUntilAnalysisStageComplete(analysisExecutionScheduledTask.transferAnalysesResults());
	}
	
	private void waitForFilesToSettle(SequenceFilePair... filePairs) throws InterruptedException {
		// wait for the files to be moved into the right place, then get the
		// records from the database again so that we get the right path.
		Set<SequenceFile> files = null;
		
		do {
			Thread.sleep(500);
			files = Arrays.stream(filePairs).map(p -> sequencingObjectService.read(p.getId()))
					.map(p -> p.getFiles()).flatMap(l -> l.stream()).collect(Collectors.toSet());
		} while(!files.stream().allMatch(f -> f.getFastQCAnalysis() != null));
	}

	/**
	 * Tests out successfully executing the SNVPhyl pipeline.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSNVPhylSuccess() throws Exception {
		gzipFileProcessor.setDisableFileProcessor(true);
		
		SequenceFilePair sequenceFilePairA = databaseSetupGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePathsA1List, sequenceFilePathsA2List).get(0);
		SequenceFilePair sequenceFilePairB = databaseSetupGalaxyITService.setupSampleSequenceFileInDatabase(2L,
				sequenceFilePathsB1List, sequenceFilePathsB2List).get(0);
		SequenceFilePair sequenceFilePairC = databaseSetupGalaxyITService.setupSampleSequenceFileInDatabase(3L,
				sequenceFilePathsC1List, sequenceFilePathsC2List).get(0);
		
		Map<String,String> parameters = ImmutableMap.of("snv-abundance-ratio", "0.75", "minimum-read-coverage", "2",
				"filter-density-threshold", "2", "filter-density-window-size", "3");
		waitForFilesToSettle(sequenceFilePairA, sequenceFilePairB, sequenceFilePairC);

		AnalysisSubmission submission = databaseSetupGalaxyITService.setupPairSubmissionInDatabase(
				Sets.newHashSet(sequenceFilePairA, sequenceFilePairB, sequenceFilePairC), referenceFilePath,
				parameters, snvPhylWorkflow.getWorkflowIdentifier());
		
		completeSubmittedAnalyses(submission.getId());

		submission = analysisSubmissionRepository.findOne(submission.getId());
		assertEquals("analysis state should be completed.", AnalysisState.COMPLETED, submission.getAnalysisState());

		Analysis analysisPhylogenomics = submission.getAnalysis();
		assertEquals("Should have generated a phylogenomics pipeline analysis type.",
				BuiltInAnalysisTypes.PHYLOGENOMICS, analysisPhylogenomics.getAnalysisType());

		assertEquals("the phylogenomics pipeline should have 8 output files.", 8, analysisPhylogenomics
				.getAnalysisOutputFiles().size());
		
		@SuppressWarnings("resource")
		String matrixContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(MATRIX_KEY).getFile().toFile()).useDelimiter("\\Z")
				.next();
		assertTrue(
				"snpMatrix should be the same but is \"" + matrixContent + "\"",
				com.google.common.io.Files.equal(outputSnvMatrix1.toFile(), analysisPhylogenomics.getAnalysisOutputFile(MATRIX_KEY)
						.getFile().toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(MATRIX_KEY)
				.getCreatedByTool());
		
		@SuppressWarnings("resource")
		String snpTableContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(TABLE_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"snpTable should be the same but is \"" + snpTableContent + "\"",
				com.google.common.io.Files.equal(outputSnvTable1.toFile(), analysisPhylogenomics.getAnalysisOutputFile(TABLE_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(TABLE_KEY)
				.getCreatedByTool());
		
		@SuppressWarnings("resource")
		String vcf2coreContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(CORE_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"vcf2core should be the same but is \"" + vcf2coreContent + "\"",
				com.google.common.io.Files.equal(vcf2core1.toFile(), analysisPhylogenomics.getAnalysisOutputFile(CORE_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(CORE_KEY)
				.getCreatedByTool());
		
		// only check size of mapping quality file due to samples output in random order
		assertTrue("the mapping quality file should not be empty.",
				Files.size(analysisPhylogenomics.getAnalysisOutputFile(QUALITY_KEY).getFile()) > 0);
		
		@SuppressWarnings("resource")
		String filterStatsContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(STATS_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"filterStats should be the same but is \"" + filterStatsContent + "\"",
				com.google.common.io.Files.equal(filterStats1.toFile(), analysisPhylogenomics.getAnalysisOutputFile(STATS_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(STATS_KEY)
				.getCreatedByTool());
		
		@SuppressWarnings("resource")
		String snvAlignContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(ALIGN_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"snvAlign should be the same but is \"" + snvAlignContent + "\"",
				com.google.common.io.Files.equal(snvAlign1.toFile(), analysisPhylogenomics.getAnalysisOutputFile(ALIGN_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(ALIGN_KEY)
				.getCreatedByTool());
		
		// only test to make sure the files have a valid size since PhyML uses a
		// random seed to generate the tree (and so changes results)
		assertTrue("the phylogenetic tree file should not be empty.",
				Files.size(analysisPhylogenomics.getAnalysisOutputFile(TREE_KEY).getFile()) > 0);
		assertTrue("the phylogenetic tree stats file should not be empty.",
				Files.size(analysisPhylogenomics.getAnalysisOutputFile(TREE_KEY).getFile()) > 0);

		// try to follow the phylogenomics provenance all the way back to the
		// upload tools
		final List<ToolExecution> toolsToVisit = Lists.newArrayList(analysisPhylogenomics.getAnalysisOutputFile(TREE_KEY)
				.getCreatedByTool());
		assertFalse("file should have tool provenance attached.", toolsToVisit.isEmpty());

		boolean foundReadsInputTool = false;
		boolean foundReferenceInputTool = false;

		// navigate through the tree to make sure that you can find both types
		// of input tools: the one where you upload the reference file, and the
		// one where you upload the reads.
		while (!toolsToVisit.isEmpty()) {
			final ToolExecution ex = toolsToVisit.remove(0);
			toolsToVisit.addAll(ex.getPreviousSteps());

			if (ex.isInputTool()) {
				final Map<String, String> params = ex.getExecutionTimeParameters();
				logger.debug("Input tool has " + params);

				foundReferenceInputTool |= params.containsKey("files.NAME")
						&& params.get("files.NAME").contains("reference")
						&& params.get("file_type").contains("fasta");
				foundReadsInputTool |= params.get("file_type").contains("fastq");
			}
		}

		assertTrue("Should have found both reads and reference input tools.", foundReadsInputTool
				&& foundReferenceInputTool);
		
		// At the end, verify that these sequence files did get automatically uncompressed
		assertEquals("Should have 2 pairs of files", 2, sequenceFilePairA.getFiles().size());
		for (SequenceFile file : sequenceFilePairA.getFiles()) {
			assertFalse("Sequence files were compressed", file.getFile().toString().endsWith(".gz"));
		}
		
		// At the end, verify that the sequence files did not get automatically decompressed.
		assertEquals("Should have 2 pairs of files", 2, sequenceFilePairB.getFiles().size());
		for (SequenceFile file : sequenceFilePairB.getFiles()) {
			assertTrue("Sequence files were uncompressed", file.getFile().toString().endsWith(".gz"));
		}
	}
	
	/**
	 * Tests out successfully executing the SNVPhyl pipeline and passing a higher value for fraction of reads to call a SNP.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSNVPhylSuccessHigherSNVReadProportion() throws Exception {
		SequenceFilePair sequenceFilePairA = databaseSetupGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePathsA1List, sequenceFilePathsA2List).get(0);
		SequenceFilePair sequenceFilePairB = databaseSetupGalaxyITService.setupSampleSequenceFileInDatabase(2L,
				sequenceFilePathsB1List, sequenceFilePathsB2List).get(0);
		SequenceFilePair sequenceFilePairC = databaseSetupGalaxyITService.setupSampleSequenceFileInDatabase(3L,
				sequenceFilePathsC1List, sequenceFilePathsC2List).get(0);
		
		waitForFilesToSettle(sequenceFilePairA, sequenceFilePairB, sequenceFilePairC);

		Map<String, String> parameters = ImmutableMap.<String, String> builder()
				.put("snv-abundance-ratio", "0.90").put("minimum-read-coverage", "2")
				.put("minimum-percent-coverage", "75").put("minimum-mean-mapping-quality", "20")
				.put("filter-density-threshold", "3").put("filter-density-window-size", "30").build();
		
		AnalysisSubmission submission = databaseSetupGalaxyITService.setupPairSubmissionInDatabase(
				Sets.newHashSet(sequenceFilePairA, sequenceFilePairB, sequenceFilePairC), referenceFilePath,
				parameters, snvPhylWorkflow.getWorkflowIdentifier());

		completeSubmittedAnalyses(submission.getId());

		submission = analysisSubmissionRepository.findOne(submission.getId());
		assertEquals("analysis state should be completed.", AnalysisState.COMPLETED, submission.getAnalysisState());

		Analysis analysisPhylogenomics = submission.getAnalysis();
		assertEquals("Should have generated a phylogenomics pipeline analysis type.",
				BuiltInAnalysisTypes.PHYLOGENOMICS, analysisPhylogenomics.getAnalysisType());

		assertEquals("the phylogenomics pipeline should have 8 output files.", 8, analysisPhylogenomics
				.getAnalysisOutputFiles().size());
		
		@SuppressWarnings("resource")
		String matrixContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(MATRIX_KEY).getFile().toFile()).useDelimiter("\\Z")
				.next();
		assertTrue(
				"snpMatrix should be the same but is \"" + matrixContent + "\"",
				com.google.common.io.Files.equal(outputSnvMatrix2.toFile(), analysisPhylogenomics.getAnalysisOutputFile(MATRIX_KEY)
						.getFile().toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(MATRIX_KEY)
				.getCreatedByTool());
		
		@SuppressWarnings("resource")
		String snpTableContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(TABLE_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"snpTable should be the same but is \"" + snpTableContent + "\"",
				com.google.common.io.Files.equal(outputSnvTable2.toFile(), analysisPhylogenomics.getAnalysisOutputFile(TABLE_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(TABLE_KEY)
				.getCreatedByTool());
		
		@SuppressWarnings("resource")
		String vcf2coreContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(CORE_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"vcf2core should be the same but is \"" + vcf2coreContent + "\"",
				com.google.common.io.Files.equal(vcf2core2.toFile(), analysisPhylogenomics.getAnalysisOutputFile(CORE_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(CORE_KEY)
				.getCreatedByTool());
		
		// only check size of mapping quality file due to samples output in random order
		assertTrue("the mapping quality file should not be empty.",
				Files.size(analysisPhylogenomics.getAnalysisOutputFile(QUALITY_KEY).getFile()) > 0);
		
		@SuppressWarnings("resource")
		String filterStatsContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(STATS_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"filterStats should be the same but is \"" + filterStatsContent + "\"",
				com.google.common.io.Files.equal(filterStats2.toFile(), analysisPhylogenomics.getAnalysisOutputFile(STATS_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(STATS_KEY)
				.getCreatedByTool());
		
		@SuppressWarnings("resource")
		String snvAlignContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(ALIGN_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"snvAlign should be the same but is \"" + snvAlignContent + "\"",
				com.google.common.io.Files.equal(snvAlign2.toFile(), analysisPhylogenomics.getAnalysisOutputFile(ALIGN_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(ALIGN_KEY)
				.getCreatedByTool());
		
		// only test to make sure the files have a valid size since PhyML uses a
		// random seed to generate the tree (and so changes results)
		assertTrue("the phylogenetic tree file should not be empty.",
				Files.size(analysisPhylogenomics.getAnalysisOutputFile(TREE_KEY).getFile()) > 0);
		assertTrue("the phylogenetic tree stats file should not be empty.",
				Files.size(analysisPhylogenomics.getAnalysisOutputFile(TREE_KEY).getFile()) > 0);

		// try to follow the phylogenomics provenance all the way back to the
		// upload tools
		List<ToolExecution> toolsToVisit = Lists.newArrayList(analysisPhylogenomics.getAnalysisOutputFile(TREE_KEY)
				.getCreatedByTool());
		assertFalse("file should have tool provenance attached.", toolsToVisit.isEmpty());

		String minVcf2AlignCov = null;
		String altAlleleFraction = null;
		String minimumPercentCoverage = null;
		String minimumDepthVerify = null;
		String filterDensityThreshold = null;
		String filterDensityWindowSize = null;
		
		// navigate through the tree to make sure that you can find both types
		// of input tools: the one where you upload the reference file, and the
		// one where you upload the reads.
		while (!toolsToVisit.isEmpty()) {
			final ToolExecution ex = toolsToVisit.remove(0);
			toolsToVisit.addAll(ex.getPreviousSteps());

			if (ex.getToolName().contains("Consolidate VCFs")) {
				final Map<String, String> params = ex.getExecutionTimeParameters();
				minVcf2AlignCov = params.get("coverage");
				altAlleleFraction = params.get("snv_abundance_ratio");
				filterDensityThreshold = params.get("use_density_filter.threshold");
				filterDensityWindowSize = params.get("use_density_filter.window_size");
				break;
			}
		}
		
		// try to follow the mapping quality provenance all the way back to the
		// upload tools
		toolsToVisit = Lists.newArrayList(analysisPhylogenomics.getAnalysisOutputFile(QUALITY_KEY)
				.getCreatedByTool());
		assertFalse("file should have tool provenance attached.", toolsToVisit.isEmpty());
		
		while (!toolsToVisit.isEmpty()) {
			final ToolExecution ex = toolsToVisit.remove(0);
			toolsToVisit.addAll(ex.getPreviousSteps());
			
			if (ex.getToolName().contains("Verify Mapping Quality")) {
				final Map<String, String> params = ex.getExecutionTimeParameters();
				minimumPercentCoverage = params.get("minmap");
				minimumDepthVerify = params.get("mindepth");
			}
		}
		
		assertEquals("incorrect minimum vcf 2 align coverage", "\"2\"", minVcf2AlignCov);
		assertEquals("incorrect alternative allele fraction", "\"0.90\"", altAlleleFraction);
		assertEquals("incorrect minimum depth for verify map", "\"2\"", minimumDepthVerify);
		assertEquals("incorrect min percent coverage for verify map", "\"75\"", minimumPercentCoverage);
		assertEquals("incorrect filter density threshold", "3", filterDensityThreshold);
		assertEquals("incorrect filter density window size", "30", filterDensityWindowSize);
	}
	
	/**
	 * Tests out successfully executing the SNVPhyl pipeline and passing a lower value for SNV density threshold to filter out SNVs.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSNVPhylSuccessRemoveSNVDensity() throws Exception {
		SequenceFilePair sequenceFilePairA = databaseSetupGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePathsA1List, sequenceFilePathsA2List).get(0);
		SequenceFilePair sequenceFilePairB = databaseSetupGalaxyITService.setupSampleSequenceFileInDatabase(2L,
				sequenceFilePathsB1List, sequenceFilePathsB2List).get(0);
		SequenceFilePair sequenceFilePairC = databaseSetupGalaxyITService.setupSampleSequenceFileInDatabase(3L,
				sequenceFilePathsC1List, sequenceFilePathsC2List).get(0);

		Map<String,String> parameters = ImmutableMap.of("snv-abundance-ratio", "0.75", "minimum-read-coverage", "2",
				"filter-density-threshold", "2", "filter-density-window-size", "4");
		
		AnalysisSubmission submission = databaseSetupGalaxyITService.setupPairSubmissionInDatabase(
				Sets.newHashSet(sequenceFilePairA, sequenceFilePairB, sequenceFilePairC), referenceFilePath,
				parameters, snvPhylWorkflow.getWorkflowIdentifier());

		completeSubmittedAnalyses(submission.getId());

		submission = analysisSubmissionRepository.findOne(submission.getId());
		assertEquals("analysis state should be completed.", AnalysisState.COMPLETED, submission.getAnalysisState());

		Analysis analysisPhylogenomics = submission.getAnalysis();
		assertEquals("Should have generated a phylogenomics pipeline analysis type.",
				BuiltInAnalysisTypes.PHYLOGENOMICS, analysisPhylogenomics.getAnalysisType());

		assertEquals("the phylogenomics pipeline should have 8 output files.", 8, analysisPhylogenomics
				.getAnalysisOutputFiles().size());
		
		@SuppressWarnings("resource")
		String matrixContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(MATRIX_KEY).getFile().toFile()).useDelimiter("\\Z")
				.next();
		assertTrue(
				"snpMatrix should be the same but is \"" + matrixContent + "\"",
				com.google.common.io.Files.equal(outputSnvMatrix3.toFile(), analysisPhylogenomics.getAnalysisOutputFile(MATRIX_KEY)
						.getFile().toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(MATRIX_KEY)
				.getCreatedByTool());
		
		@SuppressWarnings("resource")
		String snpTableContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(TABLE_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"snpTable should be the same but is \"" + snpTableContent + "\"",
				com.google.common.io.Files.equal(outputSnvTable3.toFile(), analysisPhylogenomics.getAnalysisOutputFile(TABLE_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(TABLE_KEY)
				.getCreatedByTool());
		
		@SuppressWarnings("resource")
		String vcf2coreContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(CORE_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"vcf2core should be the same but is \"" + vcf2coreContent + "\"",
				com.google.common.io.Files.equal(vcf2core3.toFile(), analysisPhylogenomics.getAnalysisOutputFile(CORE_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(CORE_KEY)
				.getCreatedByTool());
		
		// only check size of mapping quality file due to samples output in random order
		assertTrue("the mapping quality file should not be empty.",
				Files.size(analysisPhylogenomics.getAnalysisOutputFile(QUALITY_KEY).getFile()) > 0);
		
		@SuppressWarnings("resource")
		String filterStatsContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(STATS_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"filterStats should be the same but is \"" + filterStatsContent + "\"",
				com.google.common.io.Files.equal(filterStats3.toFile(), analysisPhylogenomics.getAnalysisOutputFile(STATS_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(STATS_KEY)
				.getCreatedByTool());
		
		@SuppressWarnings("resource")
		String snvAlignContent = new Scanner(analysisPhylogenomics.getAnalysisOutputFile(ALIGN_KEY).getFile().toFile()).useDelimiter(
				"\\Z").next();
		assertTrue(
				"snvAlign should be the same but is \"" + snvAlignContent + "\"",
				com.google.common.io.Files.equal(snvAlign3.toFile(), analysisPhylogenomics.getAnalysisOutputFile(ALIGN_KEY).getFile()
						.toFile()));
		assertNotNull("file should have tool provenance attached.", analysisPhylogenomics.getAnalysisOutputFile(ALIGN_KEY)
				.getCreatedByTool());
		
		// only test to make sure the files have a valid size since PhyML uses a
		// random seed to generate the tree (and so changes results)
		assertTrue("the phylogenetic tree file should not be empty.",
				Files.size(analysisPhylogenomics.getAnalysisOutputFile(TREE_KEY).getFile()) > 0);
		assertTrue("the phylogenetic tree stats file should not be empty.",
				Files.size(analysisPhylogenomics.getAnalysisOutputFile(TREE_KEY).getFile()) > 0);

		// try to follow the phylogenomics provenance all the way back to the
		// upload tools
		List<ToolExecution> toolsToVisit = Lists.newArrayList(analysisPhylogenomics.getAnalysisOutputFile(TREE_KEY)
				.getCreatedByTool());
		assertFalse("file should have tool provenance attached.", toolsToVisit.isEmpty());

		String minVcf2AlignCov = null;
		String altAlleleFraction = null;
		String minimumPercentCoverage = null;
		String minimumDepthVerify = null;
		String filterDensityThreshold = null;
		String filterDensityWindowSize = null;
		
		// navigate through the tree to make sure that you can find both types
		// of input tools: the one where you upload the reference file, and the
		// one where you upload the reads.
		while (!toolsToVisit.isEmpty()) {
			final ToolExecution ex = toolsToVisit.remove(0);
			toolsToVisit.addAll(ex.getPreviousSteps());

			if (ex.getToolName().contains("Consolidate VCFs")) {
				final Map<String, String> params = ex.getExecutionTimeParameters();
				minVcf2AlignCov = params.get("coverage");
				altAlleleFraction = params.get("snv_abundance_ratio");
				filterDensityThreshold = params.get("use_density_filter.threshold");
				filterDensityWindowSize = params.get("use_density_filter.window_size");
				break;
			}
		}
		
		// try to follow the mapping quality provenance all the way back to the
		// upload tools
		toolsToVisit = Lists.newArrayList(analysisPhylogenomics.getAnalysisOutputFile(QUALITY_KEY)
				.getCreatedByTool());
		assertFalse("file should have tool provenance attached.", toolsToVisit.isEmpty());
		
		while (!toolsToVisit.isEmpty()) {
			final ToolExecution ex = toolsToVisit.remove(0);
			toolsToVisit.addAll(ex.getPreviousSteps());
			
			if (ex.getToolName().contains("Verify Mapping Quality")) {
				final Map<String, String> params = ex.getExecutionTimeParameters();
				minimumPercentCoverage = params.get("minmap");
				minimumDepthVerify = params.get("mindepth");
			}
		}
		
		assertEquals("incorrect minimum vcf 2 align coverage", "\"2\"", minVcf2AlignCov);
		assertEquals("incorrect alternative allele fraction", "\"0.75\"", altAlleleFraction);
		assertEquals("incorrect minimum depth for verify map", "\"2\"", minimumDepthVerify);
		assertEquals("incorrect min percent coverage for verify map", "\"80\"", minimumPercentCoverage);
		assertEquals("incorrect filter density threshold", "2", filterDensityThreshold);
		assertEquals("incorrect filter density window size", "4", filterDensityWindowSize);
	}
}
