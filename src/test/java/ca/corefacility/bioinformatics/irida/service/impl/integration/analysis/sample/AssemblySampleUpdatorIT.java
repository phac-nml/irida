package ca.corefacility.bioinformatics.irida.service.impl.integration.analysis.sample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
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
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.sample.AssemblySampleUpdator;

/**
 * Tests updating samples with assemblies.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/analysis/sample/AssemblySampleUpdatorIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AssemblySampleUpdatorIT {

	@Autowired
	private AssemblySampleUpdator assemblySampleUpdatorService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateSuccess() {
		AnalysisSubmission a = analysisSubmissionRepository.findOne(1L);
		Sample s = sampleRepository.findOne(2L);
		assertEquals("Should be no join between sample and assembly", 0, sampleGenomeAssemblyJoinRepository.count());

		assemblySampleUpdatorService.update(Sets.newHashSet(s), a);

		assertEquals("Should exist a join between sample and assembly", 1, sampleGenomeAssemblyJoinRepository.count());
		SampleGenomeAssemblyJoin j = sampleGenomeAssemblyJoinRepository.findAll().iterator().next();

		assertEquals("Should have joined sample 2L", (Long) 2L, j.getSubject().getId());
		assertEquals("Should have joined assembly 1L", (Long) 1L, j.getObject().getId());
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testUpdateFailPermission() {
		AnalysisSubmission a = analysisSubmissionRepository.findOne(1L);
		Sample s = sampleRepository.findOne(1L);

		assemblySampleUpdatorService.update(Sets.newHashSet(s), a);
	}

	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateFailMultipleSamples() {
		AnalysisSubmission a = analysisSubmissionRepository.findOne(1L);
		Sample s1 = sampleRepository.findOne(1L);
		Sample s2 = sampleRepository.findOne(2L);

		assemblySampleUpdatorService.update(Sets.newHashSet(s1, s2), a);
	}
}
