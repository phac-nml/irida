package ca.corefacility.bioinformatics.irida.pipeline.results.impl.integration;

import static org.junit.Assert.assertEquals;

import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.impl.AnalysisSubmissionSampleProcessorImpl;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;

/**
 * Tests updating samples with assemblies.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/pipeline/results/impl/AnalysisSubmissionSampleProcessorImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisSubmissionSampleProcessorImplIT {

	@Autowired
	private AnalysisSubmissionSampleProcessorImpl analysisSubmissionSampleProcessorImpl;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateSamplesSuccess() throws PostProcessingException {
		AnalysisSubmission a = analysisSubmissionRepository.findOne(1L);
		assertEquals("Should be no join between sample and assembly", 0, sampleGenomeAssemblyJoinRepository.count());

		analysisSubmissionSampleProcessorImpl.updateSamples(a);

		assertEquals("Should exist a join between sample and assembly", 1, sampleGenomeAssemblyJoinRepository.count());
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateFailPermissionNonSampleOwner() throws PostProcessingException {
		AnalysisSubmission a = analysisSubmissionRepository.findOne(2L);

		analysisSubmissionSampleProcessorImpl.updateSamples(a);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testUpdateFailPermissionNonProjectOwner() throws PostProcessingException {
		AnalysisSubmission a = analysisSubmissionRepository.findOne(2L);

		analysisSubmissionSampleProcessorImpl.updateSamples(a);
	}

	/**
	 * Verifies that even if "fbristow" (the project owner) is set to run this
	 * code, the RunAsUserAspect will switch the user to the owner of the
	 * analysis submission, a non-project owner who should not have the ability
	 * to write to the samples (and so should throw an AccessDeniedException
	 * for this test).
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateSamplesFailAnalysisSubmittedNonProjectOwner() throws PostProcessingException {
		AnalysisSubmission a = analysisSubmissionRepository.findOne(3L);

		analysisSubmissionSampleProcessorImpl.updateSamples(a);
	}
}
