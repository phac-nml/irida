package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisSubmissionRepositoryIT {

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private ReferenceFileRepository referenceFileRepository;

	@Autowired
	private SequenceFileRepository sequenceFileRepository;

	@Autowired
	private SequenceFilePairRepository sequenceFilePairRepository;

	@Autowired
	private UserRepository userRepository;

	private UUID workflowId = UUID.randomUUID();

	private AnalysisSubmission analysisSubmission;
	private AnalysisSubmission analysisSubmission2;
	private SequenceFilePair sequenceFilePair;
	private SequenceFile sequenceFile;
	private ReferenceFile referenceFile;

	private static final String analysisId = "10";
	private static final String analysisId2 = "11";
	private final String analysisName = "analysis 1";
	private final String analysisName2 = "analysis 2";

	private User submitter1;
	private User submitter2;

	/**
	 * Sets up objects for test.
	 * 
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
		sequenceFile = sequenceFileRepository.findOne(1L);
		assertNotNull(sequenceFile);
		Set<SequenceFile> sequenceFiles = Sets.newHashSet(sequenceFile);

		SequenceFile sequenceFile2 = sequenceFileRepository.findOne(2L);
		assertNotNull(sequenceFile2);
		Set<SequenceFile> sequenceFiles2 = Sets.newHashSet(sequenceFile2);

		sequenceFilePair = sequenceFilePairRepository.findOne(1L);
		assertNotNull(sequenceFilePair);

		referenceFile = referenceFileRepository.findOne(1L);
		assertNotNull(referenceFile);

		submitter1 = userRepository.findOne(1L);
		submitter2 = userRepository.findOne(2L);

		analysisSubmission = AnalysisSubmission.builder()
				.name(analysisName)
				.inputFilesSingle(sequenceFiles)
				.referenceFile(referenceFile)
				.workflowId(workflowId)
				.build();
		analysisSubmission.setRemoteAnalysisId(analysisId);
		analysisSubmission.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmission.setSubmitter(submitter1);

		analysisSubmission2 = AnalysisSubmission.builder()
				.name(analysisName2)
				.inputFilesSingle(sequenceFiles2)
				.referenceFile(referenceFile)
				.workflowId(workflowId)
				.build();
		analysisSubmission2.setRemoteAnalysisId(analysisId2);
		analysisSubmission2.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmission2.setSubmitter(submitter2);
	}

	/**
	 * Tests getting a single analysis by its state and succeeding.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByAnalysisStateSuccess() {
		analysisSubmissionRepository.save(analysisSubmission);
		List<AnalysisSubmission> submittedAnalyses = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.SUBMITTING);
		assertEquals(1, submittedAnalyses.size());
		assertEquals(analysisId, submittedAnalyses.get(0).getRemoteAnalysisId());
	}

	/**
	 * Tests getting multiple analyses by a state and succeeding.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByAnalysisStateMultiple() {
		analysisSubmissionRepository.save(analysisSubmission);
		analysisSubmissionRepository.save(analysisSubmission2);
		List<AnalysisSubmission> submittedAnalyses = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.SUBMITTING);
		assertEquals(2, submittedAnalyses.size());
		assertEquals(analysisId, submittedAnalyses.get(0).getRemoteAnalysisId());
	}

	/**
	 * Tests finding no analyses by the given state.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByAnalysisStateNone() {
		analysisSubmissionRepository.save(analysisSubmission);
		List<AnalysisSubmission> submittedAnalyses = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.RUNNING);
		assertEquals(Collections.EMPTY_LIST, submittedAnalyses);
	}

	/**
	 * Tests creating an analysis with only paired files, no reference
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateAnalysisPaired() {
		AnalysisSubmission analysisSubmissionPaired = AnalysisSubmission.builder()
				.name("submission paired 1")
				.inputFilesPaired(Sets.newHashSet(sequenceFilePair))
				.workflowId(workflowId)
				.build(); 
		analysisSubmissionPaired.setSubmitter(submitter1);
		AnalysisSubmission savedSubmission = analysisSubmissionRepository.save(analysisSubmissionPaired);
		assertEquals(0, savedSubmission.getSingleInputFiles().size());
		assertEquals(Sets.newHashSet(sequenceFilePair), savedSubmission.getPairedInputFiles());
		assertFalse(savedSubmission.getReferenceFile().isPresent());
	}

	/**
	 * Tests creating an analysis with only paired files, with reference
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateAnalysisPairedReference() {
		AnalysisSubmission analysisSubmissionPaired = AnalysisSubmission.builder()
				.name("submission paired 1")
				.inputFilesPaired(Sets.newHashSet(sequenceFilePair))
				.referenceFile(referenceFile)
				.workflowId(workflowId)
				.build();
		analysisSubmissionPaired.setSubmitter(submitter1);
		AnalysisSubmission savedSubmission = analysisSubmissionRepository.save(analysisSubmissionPaired);

		assertEquals(0, savedSubmission.getSingleInputFiles().size());
		assertEquals(Sets.newHashSet(sequenceFilePair), savedSubmission.getPairedInputFiles());
		assertEquals(referenceFile, savedSubmission.getReferenceFile().get());
	}

	/**
	 * Tests creating an analysis with both single and paired files and a
	 * reference genome.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateAnalysisSingleAndPairedAndReference() {
		AnalysisSubmission analysisSubmissionPaired = AnalysisSubmission.builder()
				.name("submission paired 1")
				.inputFilesPaired(Sets.newHashSet(sequenceFilePair))
				.inputFilesSingle(Sets.newHashSet(sequenceFile))
				.referenceFile(referenceFile)
				.workflowId(workflowId)
				.build();
		analysisSubmissionPaired.setSubmitter(submitter1);
		AnalysisSubmission savedSubmission = analysisSubmissionRepository.save(analysisSubmissionPaired);

		assertEquals(Sets.newHashSet(sequenceFile), savedSubmission.getSingleInputFiles());
		assertEquals(Sets.newHashSet(sequenceFilePair), savedSubmission.getPairedInputFiles());
		assertEquals(referenceFile, savedSubmission.getReferenceFile().get());
	}

	/**
	 * Tests creating an analysis with both single and paired files and no
	 * reference genome.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateAnalysisSingleAndPaired() {
		AnalysisSubmission analysisSubmissionPaired = AnalysisSubmission.builder()
				.name("submission paired 1")
				.inputFilesPaired(Sets.newHashSet(sequenceFilePair))
				.inputFilesSingle(Sets.newHashSet(sequenceFile))
				.workflowId(workflowId)
				.build();
		analysisSubmissionPaired.setSubmitter(submitter1);
		AnalysisSubmission savedSubmission = analysisSubmissionRepository.save(analysisSubmissionPaired);

		assertEquals(Sets.newHashSet(sequenceFile), savedSubmission.getSingleInputFiles());
		assertEquals(Sets.newHashSet(sequenceFilePair), savedSubmission.getPairedInputFiles());
		assertFalse(savedSubmission.getReferenceFile().isPresent());
	}

	/**
	 * Tests creating an analysis with no set submitter and failing.
	 */
	@Test(expected = DataIntegrityViolationException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateAnalysisNoSubmitterFail() {
		AnalysisSubmission analysisSubmissionPaired = AnalysisSubmission.builder()
				.name("submission paired 1")
				.inputFilesPaired(Sets.newHashSet(sequenceFilePair))
				.workflowId(workflowId)
				.build(); 
		analysisSubmissionRepository.save(analysisSubmissionPaired);
	}

	/**
	 * Tests successfully finding an analysis submission by the submitter.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindBySubmitterSuccess() {
		AnalysisSubmission savedSubmission = analysisSubmissionRepository.save(analysisSubmission);

		Set<AnalysisSubmission> submissions = analysisSubmissionRepository.findBySubmitter(submitter1);
		assertNotNull("submissions should not be null", submissions);
		assertEquals("there are an invalid number of submissions found", 1, submissions.size());
		AnalysisSubmission returnedSubmission = submissions.iterator().next();
		assertEquals("the id of the submission returned is incorrect", savedSubmission.getId(),
				returnedSubmission.getId());
		assertEquals("the submitter of the submission returned is incorrect", savedSubmission.getSubmitter().getId(),
				submitter1.getId());
	}

	/**
	 * Tests successfully finding multiple analyses submissions by the
	 * submitter.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindBySubmitterMultipleSuccess() {
		analysisSubmission2.setSubmitter(submitter1);
		analysisSubmissionRepository.save(analysisSubmission);
		analysisSubmissionRepository.save(analysisSubmission2);

		Set<AnalysisSubmission> submissions = analysisSubmissionRepository.findBySubmitter(submitter1);
		assertEquals("there are an invalid number of submissions found", 2, submissions.size());
	}

	/**
	 * Tests failing to find an analysis submission by the submitter.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindBySubmitterFail() {
		analysisSubmissionRepository.save(analysisSubmission);

		Set<AnalysisSubmission> submissions = analysisSubmissionRepository.findBySubmitter(submitter2);
		assertEquals("there should be no submissions found", 0, submissions.size());
	}
}
