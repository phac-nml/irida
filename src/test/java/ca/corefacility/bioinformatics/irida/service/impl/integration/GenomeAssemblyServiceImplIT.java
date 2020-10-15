package ca.corefacility.bioinformatics.irida.service.impl.integration;

import java.nio.file.Path;
import java.util.Collection;

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

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SampleServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class GenomeAssemblyServiceImplIT {

	@Autowired
	private SampleService sampleService;
	@Autowired
	private Path outputFileBaseDirectory;
	@Autowired
	private GenomeAssemblyService genomeAssemblyService;

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testGetAssembliesForSampleSuccess() {
		Sample s = sampleService.read(1L);
		Collection<SampleGenomeAssemblyJoin> joins = genomeAssemblyService.getAssembliesForSample(s);
		assertEquals("should have same size for assemblies", 1, joins.size());

		SampleGenomeAssemblyJoin join = joins.iterator()
				.next();
		assertEquals("Should be same sample", s.getId(), join.getSubject()
				.getId());
		assertEquals("Should be same assembly", Long.valueOf(1), join.getObject()
				.getId());
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testGetAssembliesForSampleFail() {
		Sample s = sampleService.read(1L);
		genomeAssemblyService.getAssembliesForSample(s);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testGetGenomeAssemblyForSampleSuccess() throws AnalysisAlreadySetException {
		Path expectedAssemblyPath = outputFileBaseDirectory.resolve("testfile.fasta");
		Sample s = sampleService.read(1L);

		GenomeAssembly genomeAssembly = genomeAssemblyService.getGenomeAssemblyForSample(s, 1L);
		assertEquals("should have same path for assembly", expectedAssemblyPath, genomeAssembly.getFile());
	}

	@Test(expected = EntityNotFoundException.class)
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testGetGenomeAssemblyForSampleFailNoAssembly() {
		Sample s = sampleService.read(1L);
		genomeAssemblyService.getGenomeAssemblyForSample(s, 2L);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testGetGenomeAssemblyForSampleFailDenied() {
		Sample s = sampleService.read(1L);
		genomeAssemblyService.getGenomeAssemblyForSample(s, 1L);
	}

	@Test(expected = EntityNotFoundException.class)
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testRemoveGenomeAssemblyFromSampleSuccess() {
		Sample s = sampleService.read(1L);
		assertNotNull(genomeAssemblyService.getGenomeAssemblyForSample(s, 1L));

		genomeAssemblyService.removeGenomeAssemblyFromSample(s, 1L);

		genomeAssemblyService.getGenomeAssemblyForSample(s, 1L);

	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testRemoveGenomeAssemblyFromSampleFail() {
		Sample s = sampleService.read(1L);
		assertNotNull(genomeAssemblyService.getGenomeAssemblyForSample(s, 1L));

		genomeAssemblyService.removeGenomeAssemblyFromSample(s, 1L);
	}
}
