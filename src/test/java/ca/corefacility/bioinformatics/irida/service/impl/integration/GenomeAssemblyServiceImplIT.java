package ca.corefacility.bioinformatics.irida.service.impl.integration;

import java.nio.file.Path;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceIntegrationTest
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
		assertEquals(1, joins.size(), "should have same size for assemblies");

		SampleGenomeAssemblyJoin join = joins.iterator().next();
		assertEquals(s.getId(), join.getSubject().getId(), "Should be same sample");
		assertEquals(Long.valueOf(1), join.getObject().getId(), "Should be same assembly");
	}

	@Test
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testGetAssembliesForSampleFail() {
		assertThrows(AccessDeniedException.class, () -> {
			Sample s = sampleService.read(1L);
			genomeAssemblyService.getAssembliesForSample(s);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testGetGenomeAssemblyForSampleSuccess() throws AnalysisAlreadySetException {
		Path expectedAssemblyPath = outputFileBaseDirectory.resolve("testfile.fasta");
		Sample s = sampleService.read(1L);

		GenomeAssembly genomeAssembly = genomeAssemblyService.getGenomeAssemblyForSample(s, 1L);
		assertEquals(expectedAssemblyPath, genomeAssembly.getFile(), "should have same path for assembly");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testGetGenomeAssemblyForSampleFailNoAssembly() {
		Sample s = sampleService.read(1L);
		assertThrows(EntityNotFoundException.class, () -> {
			genomeAssemblyService.getGenomeAssemblyForSample(s, 2L);
		});
	}

	@Test
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testGetGenomeAssemblyForSampleFailDenied() {
		assertThrows(AccessDeniedException.class, () -> {
			Sample s = sampleService.read(1L);
			genomeAssemblyService.getGenomeAssemblyForSample(s, 1L);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testRemoveGenomeAssemblyFromSampleSuccess() {
		Sample s = sampleService.read(1L);
		assertNotNull(genomeAssemblyService.getGenomeAssemblyForSample(s, 1L));

		genomeAssemblyService.removeGenomeAssemblyFromSample(s, 1L);

		assertThrows(EntityNotFoundException.class, () -> {
			genomeAssemblyService.getGenomeAssemblyForSample(s, 1L);
		});

	}

	@Test
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testRemoveGenomeAssemblyFromSampleFail() {
		assertThrows(AccessDeniedException.class, () -> {
			Sample s = sampleService.read(1L);
			assertNotNull(genomeAssemblyService.getGenomeAssemblyForSample(s, 1L));

			genomeAssemblyService.removeGenomeAssemblyFromSample(s, 1L);
		});
	}
}
