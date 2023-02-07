package ca.corefacility.bioinformatics.irida.web.controller.test.unit.samples;

import java.util.List;

import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssemblyFromAnalysis;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleAssemblyController;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RESTSampleAssemblyController}
 */
public class RESTSampleAssemblyControllerTest {
	private RESTSampleAssemblyController controller;
	private SampleService sampleService;
	private GenomeAssemblyService genomeAssemblyService;

	private IridaFileStorageUtility iridaFileStorageUtility;

	GenomeAssemblyFromAnalysis assemblyFromAnalysis;
	List<SampleGenomeAssemblyJoin> assemblies;
	Sample s1;

	@BeforeEach
	public void setUp() {
		sampleService = mock(SampleService.class);
		genomeAssemblyService = mock(GenomeAssemblyService.class);
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl();
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);

		controller = new RESTSampleAssemblyController(sampleService, genomeAssemblyService);

		s1 = new Sample("s1");
		s1.setId(1L);

		when(sampleService.read(s1.getId())).thenReturn(s1);

		AnalysisSubmission analysisSubmission = TestDataFactory.constructAnalysisSubmission();
		analysisSubmission.setId(2L);

		assemblyFromAnalysis = new GenomeAssemblyFromAnalysis(analysisSubmission);
		assemblyFromAnalysis.setId(3L);

		assemblies = Lists.newArrayList(new SampleGenomeAssemblyJoin(s1, assemblyFromAnalysis));
		when(genomeAssemblyService.getAssembliesForSample(s1)).thenReturn(assemblies);
	}

	@Test
	public void testListAssemblies() {
		ResponseResource<ResourceCollection<GenomeAssembly>> responseResource = controller.listAssembliesForSample(
				s1.getId());

		ResourceCollection<GenomeAssembly> readAssemblies = responseResource.getResource();

		assertEquals(assemblies.size(), readAssemblies.size(), "should be the same number of assemblies");

		assertNotNull(readAssemblies.getSelfHref(), "has self rel");

		GenomeAssembly genomeAssembly = readAssemblies.getResources()
				.iterator()
				.next();

		assertTrue(genomeAssembly.getLink("self").isPresent(), "has self rel");
		assertEquals(assemblyFromAnalysis.getId(), genomeAssembly.getId(), "should be same assembly");
	}

	@Test
	public void testReadAssembly() {
		ResponseResource<GenomeAssembly> responseResource = controller.readAssemblyForSample(s1.getId(),
				assemblyFromAnalysis.getId());

		GenomeAssembly genomeAssembly = responseResource.getResource();

		assertTrue(genomeAssembly.getLink("self").isPresent(), "has self rel");
		assertEquals(assemblyFromAnalysis.getId(), genomeAssembly.getId(), "should be same assembly");
	}

}
