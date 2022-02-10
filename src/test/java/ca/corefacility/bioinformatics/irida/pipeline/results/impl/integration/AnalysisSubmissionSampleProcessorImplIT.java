package ca.corefacility.bioinformatics.irida.pipeline.results.impl.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.impl.AnalysisSubmissionSampleProcessorImpl;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;

/**
 * Tests updating samples with assemblies.
 */
@ServiceIntegrationTest
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
		AnalysisSubmission a = analysisSubmissionRepository.findById(1L).orElse(null);
		assertEquals(0, sampleGenomeAssemblyJoinRepository.count(), "Should be no join between sample and assembly");

		analysisSubmissionSampleProcessorImpl.updateSamples(a);

		assertEquals(1, sampleGenomeAssemblyJoinRepository.count(), "Should exist a join between sample and assembly");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateFailPermissionNonSampleOwner() throws PostProcessingException {
		AnalysisSubmission a = analysisSubmissionRepository.findById(2L).orElse(null);

		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionSampleProcessorImpl.updateSamples(a);
		});
	}

	@Test
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testUpdateFailPermissionNonProjectOwner() throws PostProcessingException {
		AnalysisSubmission a = analysisSubmissionRepository.findById(2L).orElse(null);

		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionSampleProcessorImpl.updateSamples(a);
		});
	}

	/**
	 * Verifies that even if "fbristow" (the project owner) is set to run this
	 * code, the RunAsUserAspect will switch the user to the owner of the
	 * analysis submission, a non-project owner who should not have the ability
	 * to write to the samples (and so should throw an AccessDeniedException for
	 * this test).
	 */
	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateSamplesFailAnalysisSubmittedNonProjectOwner() throws PostProcessingException {
		AnalysisSubmission a = analysisSubmissionRepository.findById(3L).orElse(null);

		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionSampleProcessorImpl.updateSamples(a);
		});
	}
}
