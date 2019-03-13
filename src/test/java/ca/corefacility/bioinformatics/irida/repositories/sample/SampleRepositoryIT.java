package ca.corefacility.bioinformatics.irida.repositories.sample;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
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
		AnalysisSubmission a = analysisSubmissionRepository.findOne(10L);
		Set<Sample> samples = sampleRepository.findSamplesForAnalysisSubmission(a);

		assertEquals("Sample ids are equal", Sets.newHashSet(11L),
				samples.stream().map(Sample::getId).collect(Collectors.toSet()));
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testFindSamplesForAnalysisSubmissionMultipleSample() {
		AnalysisSubmission a = analysisSubmissionRepository.findOne(21L);
		Set<Sample> samples = sampleRepository.findSamplesForAnalysisSubmission(a);

		assertEquals("Sample ids are equal", Sets.newHashSet(22L, 33L),
				samples.stream().map(Sample::getId).collect(Collectors.toSet()));
	}
}
