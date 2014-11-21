package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.workflow.RemoteWorkflowRepository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisSubmissionRepositoryIT {

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private RemoteWorkflowRepository remoteWorkflowRepository;

	@Autowired
	private ReferenceFileRepository referenceFileRepository;

	@Autowired
	private SequenceFileRepository sequenceFileRepository;

	private AnalysisSubmissionPhylogenomics analysisSubmission;
	private AnalysisSubmissionPhylogenomics analysisSubmission2;
	private static final String analysisId = "10";
	private static final String analysisId2 = "11";
	private final String analysisName = "analysis 1";
	private final String analysisName2 = "analysis 2";

	/**
	 * Sets up objects for test.
	 * 
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
		SequenceFile sequenceFile = sequenceFileRepository.findOne(1L);
		assertNotNull(sequenceFile);
		Set<SequenceFile> sequenceFiles = Sets.newHashSet(sequenceFile);
		
		SequenceFile sequenceFile2 = sequenceFileRepository.findOne(2L);
		assertNotNull(sequenceFile2);
		Set<SequenceFile> sequenceFiles2 = Sets.newHashSet(sequenceFile2);
		
		ReferenceFile referenceFile = referenceFileRepository.findOne(1L);
		assertNotNull(referenceFile);
		RemoteWorkflowPhylogenomics remoteWorkflow = remoteWorkflowRepository
				.getByType("1", RemoteWorkflowPhylogenomics.class);
		assertNotNull(remoteWorkflow);

		analysisSubmission = new AnalysisSubmissionPhylogenomics(analysisName, sequenceFiles,
				referenceFile, remoteWorkflow);
		analysisSubmission.setRemoteAnalysisId(analysisId);
		analysisSubmission.setAnalysisState(AnalysisState.SUBMITTING);
		
		analysisSubmission2 = new AnalysisSubmissionPhylogenomics(analysisName2, sequenceFiles2,
				referenceFile, remoteWorkflow);
		analysisSubmission2.setRemoteAnalysisId(analysisId2);
		analysisSubmission2.setAnalysisState(AnalysisState.SUBMITTING);
	}

	/**
	 * Tests saving an analysis submission.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSaveAnalysisSubmission() {
		AnalysisSubmissionPhylogenomics savedSubmission = analysisSubmissionRepository
				.save(analysisSubmission);

		AnalysisSubmissionPhylogenomics loadedSubmission = analysisSubmissionRepository
				.getByType(savedSubmission.getId(),
						AnalysisSubmissionPhylogenomics.class);

		assertEquals(analysisSubmission.getRemoteAnalysisId(),
				loadedSubmission.getRemoteAnalysisId());
		assertEquals(analysisSubmission.getRemoteWorkflow(),
				loadedSubmission.getRemoteWorkflow());
		assertEquals(analysisSubmission.getInputFiles(),
				loadedSubmission.getInputFiles());
		assertEquals(analysisSubmission.getReferenceFile(),
				loadedSubmission.getReferenceFile());
		assertEquals(analysisSubmission.getAnalysisState(),
				loadedSubmission.getAnalysisState());
	}

	/**
	 * Tests failing to get an analysis submission
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisSubmissionFail() {
		AnalysisSubmissionPhylogenomics savedSubmission = analysisSubmissionRepository
				.getByType(999L, AnalysisSubmissionPhylogenomics.class);
		assertNull(savedSubmission);
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
}
