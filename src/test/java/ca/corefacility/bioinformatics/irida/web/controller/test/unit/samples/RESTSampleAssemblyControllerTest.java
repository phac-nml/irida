package ca.corefacility.bioinformatics.irida.web.controller.test.unit.samples;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssemblyFromAnalysis;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleAssemblyController;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RESTSampleAssemblyController}
 */
public class RESTSampleAssemblyControllerTest {
	private RESTSampleAssemblyController controller;
	private SampleService sampleService;
	private GenomeAssemblyService genomeAssemblyService;

	GenomeAssemblyFromAnalysis assemblyFromAnalysis;
	List<SampleGenomeAssemblyJoin> assemblies;
	Sample s1;

	@Before
	public void setUp() {
		sampleService = mock(SampleService.class);
		genomeAssemblyService = mock(GenomeAssemblyService.class);

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
		ModelMap modelMap = controller.listAssembliesForSample(s1.getId());

		assertTrue("resources should exist", modelMap.containsAttribute(RESTGenericController.RESOURCE_NAME));

		ResourceCollection<GenomeAssembly> readAssemblies = (ResourceCollection<GenomeAssembly>) modelMap.getAttribute(
				RESTGenericController.RESOURCE_NAME);

		assertEquals("should be the same number of assemblies", assemblies.size(), readAssemblies.size());

		assertNotNull("has self rel", readAssemblies.getSelfHref());

		GenomeAssembly genomeAssembly = readAssemblies.getResources()
				.iterator()
				.next();

		assertNotNull("has self rel", genomeAssembly.getLink("self"));
		assertEquals("should be same assembly", assemblyFromAnalysis.getId(), genomeAssembly.getId());
	}

	@Test
	public void testReadAssembly() {
		ModelMap modelMap = controller.readAssemblyForSample(s1.getId(), assemblyFromAnalysis.getId());

		assertTrue("resource should exist", modelMap.containsAttribute(RESTGenericController.RESOURCE_NAME));

		GenomeAssembly genomeAssembly = (GenomeAssembly) modelMap.getAttribute(RESTGenericController.RESOURCE_NAME);

		assertNotNull("has self rel", genomeAssembly.getLink("self"));
		assertEquals("should be same assembly", assemblyFromAnalysis.getId(), genomeAssembly.getId());
	}

}
