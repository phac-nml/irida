package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import static org.junit.Assert.*;

import java.io.IOException;
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
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
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
	private static final String analysisId = "10";
	
	/**
	 * Sets up objects for test.
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
		SequenceFile sequenceFile = sequenceFileRepository.findOne(1L);
		assertNotNull(sequenceFile);
		Set<SequenceFile> sequenceFiles = Sets.newHashSet(sequenceFile);
		ReferenceFile referenceFile = referenceFileRepository.findOne(1L);
		assertNotNull(referenceFile);
		RemoteWorkflowPhylogenomics remoteWorkflow = remoteWorkflowRepository.getByType("1", RemoteWorkflowPhylogenomics.class);
		assertNotNull(remoteWorkflow);
		
		analysisSubmission =
				new AnalysisSubmissionPhylogenomics(sequenceFiles, referenceFile,
						remoteWorkflow);
		analysisSubmission.setRemoteAnalysisId(analysisId);
		analysisSubmission.setAnalysisState(AnalysisState.SUBMITTED);
	}
	
	/**
	 * Tests saving an analysis submission.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSaveAnalysisSubmission() {
		analysisSubmissionRepository.save(analysisSubmission);
		
		AnalysisSubmissionPhylogenomics savedSubmission = 
				analysisSubmissionRepository.getByType(analysisId, AnalysisSubmissionPhylogenomics.class);
		
		assertEquals(analysisSubmission.getRemoteAnalysisId(), savedSubmission.getRemoteAnalysisId());
		assertEquals(analysisSubmission.getRemoteWorkflow(), savedSubmission.getRemoteWorkflow());
		assertEquals(analysisSubmission.getInputFiles(), savedSubmission.getInputFiles());
		assertEquals(analysisSubmission.getReferenceFile(), savedSubmission.getReferenceFile());
		assertEquals(analysisSubmission.getAnalysisState(), savedSubmission.getAnalysisState());
	}
	
	/**
	 * Tests failing to get an analysis submission
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisSubmissionFail() {		
		AnalysisSubmissionPhylogenomics savedSubmission = 
				analysisSubmissionRepository.getByType("invalid", AnalysisSubmissionPhylogenomics.class);
		assertNull(savedSubmission);
	}
	
	/**
	 * Tests getting an analysis by its state and succeeding.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByAnalysisStateSuccess() {
		analysisSubmissionRepository.save(analysisSubmission);
		List<AnalysisSubmission> submittedAnalyses = analysisSubmissionRepository.findByAnalysisState(AnalysisState.SUBMITTED);
		assertEquals(1,submittedAnalyses.size());
		assertEquals(analysisId, submittedAnalyses.get(0).getRemoteAnalysisId());
	}
	
	/**
	 * Tests getting an analysis by its state and failing.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindByAnalysisStateFail() {
		analysisSubmissionRepository.save(analysisSubmission);
		List<AnalysisSubmission> submittedAnalyses = analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING);
		assertEquals(0,submittedAnalyses.size());
	}
}
