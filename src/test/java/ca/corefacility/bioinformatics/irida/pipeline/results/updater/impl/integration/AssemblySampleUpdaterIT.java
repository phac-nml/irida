package ca.corefacility.bioinformatics.irida.pipeline.results.updater.impl.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.impl.AssemblySampleUpdater;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;

/**
 * Tests updating samples with assemblies.
 */
@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/pipeline/results/impl/AssemblySampleUpdaterIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AssemblySampleUpdaterIT {

	@Autowired
	private AssemblySampleUpdater assemblySampleUpdater;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateSuccess() {
		AnalysisSubmission a = analysisSubmissionRepository.findById(1L).orElse(null);
		Sample s = sampleRepository.findById(2L).orElse(null);
		assertEquals(0, sampleGenomeAssemblyJoinRepository.count(), "Should be no join between sample and assembly");

		assemblySampleUpdater.update(Sets.newHashSet(s), a);

		assertEquals(1, sampleGenomeAssemblyJoinRepository.count(), "Should exist a join between sample and assembly");
		SampleGenomeAssemblyJoin j = sampleGenomeAssemblyJoinRepository.findAll().iterator().next();

		assertEquals((Long) 2L, j.getSubject().getId(), "Should have joined sample 2L");
		assertNotNull(j.getObject().getId(), "Should have joined an assembly");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateFailMultipleSamples() {
		AnalysisSubmission a = analysisSubmissionRepository.findById(1L).orElse(null);
		Sample s1 = sampleRepository.findById(1L).orElse(null);
		Sample s2 = sampleRepository.findById(2L).orElse(null);

		assertThrows(IllegalArgumentException.class, () -> {
			assemblySampleUpdater.update(Sets.newHashSet(s1, s2), a);
		});
	}
}
