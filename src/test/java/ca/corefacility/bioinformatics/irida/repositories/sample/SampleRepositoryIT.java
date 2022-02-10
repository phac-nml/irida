package ca.corefacility.bioinformatics.irida.repositories.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;

@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/sample/SampleRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SampleRepositoryIT {

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testFindSamplesForAnalysisSubmissionSingleSample() {
		AnalysisSubmission a = analysisSubmissionRepository.findById(10L).orElse(null);
		Set<Sample> samples = sampleRepository.findSamplesForAnalysisSubmission(a);

		assertEquals(Sets.newHashSet(11L), samples.stream().map(Sample::getId).collect(Collectors.toSet()),
				"Sample ids are equal");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testFindSamplesForAnalysisSubmissionMultipleSample() {
		AnalysisSubmission a = analysisSubmissionRepository.findById(21L).orElse(null);
		Set<Sample> samples = sampleRepository.findSamplesForAnalysisSubmission(a);

		assertEquals(Sets.newHashSet(22L, 33L), samples.stream().map(Sample::getId).collect(Collectors.toSet()),
				"Sample ids are equal");
	}
}
