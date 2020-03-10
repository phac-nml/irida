package ca.corefacility.bioinformatics.irida.web.controller.test.unit.samples;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssemblyFromAnalysis;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleAssemblyController;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RESTSampleAssemblyControllerTest {
	private RESTSampleAssemblyController controller;
	private SampleService sampleService;

	GenomeAssemblyFromAnalysis assemblyFromAnalysis;
	List<SampleGenomeAssemblyJoin> assemblies;
	Sample s1;

	@Before
	public void setUp() {
		sampleService = mock(SampleService.class);

		controller = new RESTSampleAssemblyController(sampleService);

		s1 = new Sample("s1");
		s1.setId(1L);

		when(sampleService.read(s1.getId())).thenReturn(s1);

		AnalysisSubmission analysisSubmission = TestDataFactory.constructAnalysisSubmission();
		analysisSubmission.setId(2L);

		assemblyFromAnalysis = new GenomeAssemblyFromAnalysis(analysisSubmission);
		assemblyFromAnalysis.setId(3L);

		assemblies = Lists.newArrayList(new SampleGenomeAssemblyJoin(s1, assemblyFromAnalysis));
		when(sampleService.getAssembliesForSample(s1)).thenReturn(assemblies);
	}

	@Test
	public void testListAssemblies() {
		ModelMap modelMap = controller.listAssembliesForSample(s1.getId());

		assertTrue("resources should exist", modelMap.containsAttribute(RESTGenericController.RESOURCE_NAME));
	}

}
