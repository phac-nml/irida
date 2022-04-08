package ca.corefacility.bioinformatics.irida.ria.unit.web.samples;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.samples.SamplesController;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.ReadSamplePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.ReadSampleCollectionPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 */
public class SamplesControllerTest {

	// Services
	private SamplesController controller;
	private SampleService sampleService;
	private ProjectService projectService;
	private SequencingObjectService sequencingObjectService;
	private UpdateSamplePermission updateSamplePermission;
	private ReadSamplePermission readSamplePermission;
	private ReadSampleCollectionPermission readSampleCollectionPermission;
	private MetadataTemplateService metadataTemplateService;
	private GenomeAssemblyService genomeAssemblyService;
	private MessageSource messageSource;

	@BeforeEach
	public void setUp() {
		sampleService = mock(SampleService.class);
		sequencingObjectService = mock(SequencingObjectService.class);
		projectService = mock(ProjectService.class);
		metadataTemplateService = mock(MetadataTemplateService.class);
		genomeAssemblyService = mock(GenomeAssemblyService.class);
		messageSource = mock(MessageSource.class);
		updateSamplePermission = mock(UpdateSamplePermission.class);
		readSamplePermission = mock(ReadSamplePermission.class);
		controller = new SamplesController(sampleService, projectService, sequencingObjectService,
				updateSamplePermission, readSampleCollectionPermission, metadataTemplateService, genomeAssemblyService, messageSource);
	}

	// ************************************************************************************************
	// PAGE REQUESTS
	// ************************************************************************************************

	@Test
	public void testGetSampleSpecificPage() {

		Model model = new ExtendedModelMap();
		Sample sample = TestDataFactory.constructSample();
		when(sampleService.read(sample.getId())).thenReturn(sample);
		String result = controller.getSampleSpecificPage(model, sample.getId());
		assertEquals("samples/sample", result, "Returns the correct page name");
		assertTrue(model.containsAttribute("sample"), "Model contains the sample");
	}

	@Test
	public void testGetEditSampleSpecificPage() {
		Model model = new ExtendedModelMap();
		Sample sample = TestDataFactory.constructSample();
		when(sampleService.read(sample.getId())).thenReturn(sample);
		String result = controller.getEditSampleSpecificPage(model, sample.getId());
		assertEquals("samples/sample_edit", result, "Returns the correct page name");
		assertTrue(model.containsAttribute("sample"), "Model contains the sample");
		assertTrue(model.containsAttribute("errors"), "Model should ALWAYS have an error attribute");
	}

	@Test
	public void testUpdateSample() {
		Model model = new ExtendedModelMap();
		Sample sample = TestDataFactory.constructSample();
		final String contextPath = "/some-nonsense";
		String organism = "E. coli";
		String geographicLocationName = "The Forks";
		Map<String, Object> updatedValues = ImmutableMap.of(SamplesController.ORGANISM, organism,
				SamplesController.GEOGRAPHIC_LOCATION_NAME, geographicLocationName);
		Map<String, String> update = ImmutableMap.of(SamplesController.ORGANISM, organism,
				SamplesController.GEOGRAPHIC_LOCATION_NAME, geographicLocationName);
		when(sampleService.updateFields(sample.getId(), updatedValues)).thenReturn(sample);
		when(sampleService.read(sample.getId())).thenReturn(sample);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE,
				"/projects/5/samples/" + sample.getId() + "/edit");
		String result = controller.updateSample(model, sample.getId(), null, null, null, null, update, request);
		assertTrue(result.contains(sample.getId() + "/details"), "Returns the correct redirect");
		assertTrue(result.startsWith("redirect:"), "Should be a redirect response.");
		assertFalse(result.contains(contextPath), "Redirect should **not** contain the context path.");
		assertTrue(model.containsAttribute(SamplesController.ORGANISM),
				"Model should be populated with updated attributes");
		assertTrue(model.containsAttribute(SamplesController.GEOGRAPHIC_LOCATION_NAME),
				"Model should be populated with updated attributes");
		assertFalse(model.containsAttribute(SamplesController.LATITUDE),
				"Model should not be populated with non-updated attributes");
	}

	@Test
	public void testGetSampleFiles() throws IOException {
		ExtendedModelMap model = new ExtendedModelMap();
		Long sampleId = 1L;
		Sample sample = new Sample();
		SequenceFile file = new SequenceFile(Paths.get("/tmp"));
		file.setId(2L);
		Project project = new Project();

		List<SampleSequencingObjectJoin> files = Lists.newArrayList(new SampleSequencingObjectJoin(sample,
				new SingleEndSequenceFile(file)));

		when(sampleService.read(sampleId)).thenReturn(sample);
		when(sequencingObjectService.getSequencesForSampleOfType(sample, SingleEndSequenceFile.class))
				.thenReturn(files);
		when(projectService.getProjectsForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project, sample, true)));
		when(updateSamplePermission.isAllowed(any(), eq(sample))).thenReturn(true);

		String sampleFiles = controller.getSampleFilesWithoutProject(model, sampleId);

		assertEquals(SamplesController.SAMPLE_FILES_PAGE, sampleFiles);
		assertTrue((boolean) model.get(SamplesController.MODEL_ATTR_CAN_MANAGE_SAMPLE));

		verify(sampleService).read(sampleId);
		verify(sequencingObjectService).getSequencesForSampleOfType(sample, SingleEndSequenceFile.class);
		verify(sequencingObjectService).getSequencesForSampleOfType(sample, SequenceFilePair.class);
	}

	@Test
	public void testGetSampleFilesAsAdmin() throws IOException {
		ExtendedModelMap model = new ExtendedModelMap();
		Long sampleId = 1L;
		Sample sample = new Sample();
		SequenceFile file = new SequenceFile(Paths.get("/tmp"));
		file.setId(2L);

		List<SampleSequencingObjectJoin> files = Lists.newArrayList(new SampleSequencingObjectJoin(sample,
				new SingleEndSequenceFile(file)));

		when(sampleService.read(sampleId)).thenReturn(sample);
		when(sequencingObjectService.getSequencesForSampleOfType(sample, SingleEndSequenceFile.class))
				.thenReturn(files);
		when(updateSamplePermission.isAllowed(any(), eq(sample))).thenReturn(true);

		String sampleFiles = controller.getSampleFilesWithoutProject(model, sampleId);

		assertEquals(SamplesController.SAMPLE_FILES_PAGE, sampleFiles);
		assertTrue((boolean) model.get(SamplesController.MODEL_ATTR_CAN_MANAGE_SAMPLE));
		

		verify(sampleService).read(sampleId);
		verify(sequencingObjectService).getSequencesForSampleOfType(sample, SingleEndSequenceFile.class);
		verify(sequencingObjectService).getSequencesForSampleOfType(sample, SequenceFilePair.class);
		verifyNoInteractions(projectService);
	}

	@Test
	public void testGetSampleFilesNoAccess() throws IOException {
		ExtendedModelMap model = new ExtendedModelMap();

		Long sampleId = 1L;
		Sample sample = new Sample();
		SequenceFile file = new SequenceFile(Paths.get("/tmp"));
		file.setId(2L);
		Project project = new Project();

		List<SampleSequencingObjectJoin> files = Lists.newArrayList(new SampleSequencingObjectJoin(sample,
				new SingleEndSequenceFile(file)));

		when(sampleService.read(sampleId)).thenReturn(sample);

		when(sequencingObjectService.getSequencesForSampleOfType(sample, SingleEndSequenceFile.class))
				.thenReturn(files);
		when(updateSamplePermission.isAllowed(any(), eq(sample))).thenReturn(false);

		when(projectService.getProjectsForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project, sample, true)));

		String sampleFiles = controller.getSampleFilesWithoutProject(model, sampleId);

		assertEquals(SamplesController.SAMPLE_FILES_PAGE, sampleFiles);
		assertFalse((boolean) model.get(SamplesController.MODEL_ATTR_CAN_MANAGE_SAMPLE));

		verify(sampleService).read(sampleId);
		verify(sequencingObjectService).getSequencesForSampleOfType(sample, SingleEndSequenceFile.class);
		verify(sequencingObjectService).getSequencesForSampleOfType(sample, SequenceFilePair.class);
	}

	@Test
	public void testRemoveFileFromSample() {
		Long sampleId = 1L;
		Long fileId = 2L;
		Sample sample = new Sample();
		SequencingObject file = new SingleEndSequenceFile(new SequenceFile(Paths.get("/tmp")));

		when(sampleService.read(sampleId)).thenReturn(sample);
		when(sequencingObjectService.readSequencingObjectForSample(sample, fileId)).thenReturn(file);

		RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();
		HttpServletRequest request = new MockHttpServletRequest();
		controller.removeSequencingObjectFromSample(attributes, sampleId, fileId, request, Locale.US);

		verify(sampleService).removeSequencingObjectFromSample(sample, file);
	}
	
	@Test
	public void testDownloadAssembly() throws IOException {
		HttpServletResponse response = new MockHttpServletResponse();

		Long sampleId = 1L;
		Long assemblyId = 3L;
		SequenceFile file = new SequenceFile(Paths.get("/tmp"));
		file.setId(2L);

		Sample sample = new Sample();
		GenomeAssembly genomeAssembly = TestDataFactory.constructGenomeAssembly();

		when(sampleService.read(sampleId)).thenReturn(sample);
		when(genomeAssemblyService.getGenomeAssemblyForSample(sample, assemblyId)).thenReturn(genomeAssembly);
		when(readSamplePermission.isAllowed(any(Authentication.class), eq(sample))).thenReturn(true);

		controller.downloadAssembly(sampleId, assemblyId, response);

		verify(sampleService).read(sampleId);
		verify(genomeAssemblyService).getGenomeAssemblyForSample(sample, assemblyId);
	}
	
	@Test
	public void testRemoveGenomeAssemblyFromSample() {
		Long sampleId = 1L;
		Long assemblyId = 2L;
		GenomeAssembly genomeAssembly = TestDataFactory.constructGenomeAssembly();
		Sample sample = new Sample();

		when(sampleService.read(sampleId)).thenReturn(sample);
		when(genomeAssemblyService.getGenomeAssemblyForSample(sample, assemblyId)).thenReturn(genomeAssembly);
		when(updateSamplePermission.isAllowed(any(Authentication.class), eq(sample))).thenReturn(true);
		
		RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();
		HttpServletRequest request = new MockHttpServletRequest();
		
		controller.removeGenomeAssemblyFromSample(attributes, sampleId, assemblyId, request, Locale.US);

		verify(genomeAssemblyService).removeGenomeAssemblyFromSample(sample, assemblyId);
	}
}
