package ca.corefacility.bioinformatics.irida.web.controller.test.unit.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;

import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;

/**
 * Unit tests for {@link RESTSampleSequenceFilesController}.
 */
public class SampleSequenceFilesControllerTest {
	private RESTSampleSequenceFilesController controller;
	private SequenceFileService sequenceFileService;
	private SampleService sampleService;
	private ProjectService projectService;
	private SequencingRunService miseqRunService;

	@Before
	public void setUp() {
		sampleService = mock(SampleService.class);
		sequenceFileService = mock(SequenceFileService.class);
		projectService = mock(ProjectService.class);
		miseqRunService = mock(SequencingRunService.class);

		controller = new RESTSampleSequenceFilesController(sequenceFileService, sampleService, projectService,
				miseqRunService);
	}

	@Test
	public void testGetSampleSequenceFiles() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFile sf = TestDataFactory.constructSequenceFile();
		Join<Sample, SequenceFile> r = new SampleSequenceFileJoin(s, sf);
		@SuppressWarnings("unchecked")
		List<Join<Sample, SequenceFile>> relationships = Lists.newArrayList(r);

		// mock out the service calls
		when(sequenceFileService.getSequenceFilesForSample(s)).thenReturn(relationships);
		when(sampleService.read(s.getId())).thenReturn(s);

		ModelMap modelMap = controller.getSampleSequenceFiles(p.getId(), s.getId());

		// verify that the service calls were used.
		verify(sequenceFileService).getSequenceFilesForSample(s);
		verify(sampleService).read(s.getId());

		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertTrue(o instanceof ResourceCollection);
		@SuppressWarnings("unchecked")
		ResourceCollection<SequenceFileResource> resources = (ResourceCollection<SequenceFileResource>) o;
		assertNotNull(resources);
		assertEquals(1, resources.size());

		Link selfCollection = resources.getLink(Link.REL_SELF);
		Link sample = resources.getLink(RESTSampleSequenceFilesController.REL_SAMPLE);
		String sampleLocation = "http://localhost/api/projects/" + p.getId() + "/samples/" + s.getId();
		String sequenceFileLocation = sampleLocation + "/sequenceFiles/" + sf.getId();

		assertEquals(sampleLocation + "/sequenceFiles", selfCollection.getHref());
		assertEquals(sampleLocation, sample.getHref());

		// confirm that the self rel for an individual sequence file exists
		SequenceFileResource sfr = resources.iterator().next();
		Link self = sfr.getLink(Link.REL_SELF);
		assertEquals(sequenceFileLocation, self.getHref());
		assertEquals(sf.getFile().toString(), sfr.getFile());
	}

	@Test
	public void testRemoveSequenceFileFromSample() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFile sf = TestDataFactory.constructSequenceFile();

		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequenceFileService.read(sf.getId())).thenReturn(sf);

		ModelMap modelMap = controller.removeSequenceFileFromSample(p.getId(), s.getId(), sf.getId());

		verify(projectService, times(1)).read(p.getId());
		verify(sampleService, times(1)).read(s.getId());
		verify(sequenceFileService, times(1)).read(sf.getId());

		verify(sampleService, times(1)).removeSequenceFileFromSample(s, sf);

		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertNotNull(o);
		assertTrue(o instanceof RootResource);
		RootResource resource = (RootResource) o;

		Link sample = resource.getLink(RESTSampleSequenceFilesController.REL_SAMPLE);
		Link sequenceFiles = resource.getLink(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);

		String projectLocation = "http://localhost/api/projects/" + p.getId();
		String sampleLocation = projectLocation + "/samples/" + s.getId();

		assertNotNull(sample);
		assertEquals(sampleLocation, sample.getHref());
		assertNotNull(sequenceFiles);
		assertEquals(sampleLocation + "/sequenceFiles", sequenceFiles.getHref());
	}

	@Test
	public void testGetSequenceFileForSample() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFile sf = TestDataFactory.constructSequenceFile();
		SequenceFile pairFile = TestDataFactory.constructSequenceFile();

		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequenceFileService.read(sf.getId())).thenReturn(sf);
		when(sequenceFileService.getPairedFileForSequenceFile(sf)).thenReturn(pairFile);

		ModelMap modelMap = controller.getSequenceFileForSample(p.getId(), s.getId(), sf.getId());

		verify(projectService).read(p.getId());
		verify(sampleService).read(s.getId());
		verify(sequenceFileService).read(sf.getId());
		verify(sequenceFileService).getPairedFileForSequenceFile(sf);

		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertNotNull(o);
		assertTrue(o instanceof SequenceFileResource);
		SequenceFileResource sfr = (SequenceFileResource) o;
		assertEquals(sf.getFile().toString(), sfr.getFile());

		Link self = sfr.getLink(Link.REL_SELF);
		Link sampleSequenceFiles = sfr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);
		Link sample = sfr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE);
		Link pair = sfr.getLink(RESTSampleSequenceFilesController.REL_PAIR);

		String sampleLocation = "http://localhost/api/projects/" + p.getId() + "/samples/" + s.getId();
		String sequenceFileLocation = sampleLocation + "/sequenceFiles/" + sf.getId();

		assertNotNull(self);
		assertEquals(sequenceFileLocation, self.getHref());
		assertNotNull(sampleSequenceFiles);
		assertEquals(sampleLocation + "/sequenceFiles", sampleSequenceFiles.getHref());
		assertNotNull(sample);
		assertNotNull(pair);
		assertEquals(sampleLocation, sample.getHref());
	}

	@Test
	public void testAddNewSequenceFileToSample() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFile sf = TestDataFactory.constructSequenceFile();
		Join<Sample, SequenceFile> r = new SampleSequenceFileJoin(s, sf);
		SequenceFileResource resource = new SequenceFileResource();

		Path f = Files.createTempFile(null, null);
		MockMultipartFile mmf = new MockMultipartFile("filename", "filename", "blurgh", FileCopyUtils.copyToByteArray(f
				.toFile()));

		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequenceFileService.createSequenceFileInSample(Matchers.any(SequenceFile.class), Matchers.eq(s)))
				.thenReturn(r);
		when(projectService.read(p.getId())).thenReturn(p);

		ResponseEntity<String> response = controller.addNewSequenceFileToSample(p.getId(), s.getId(), mmf, resource);

		verify(sampleService).getSampleForProject(p, s.getId());
		verify(projectService).read(p.getId());
		verify(sampleService, times(1)).read(s.getId());
		verify(sequenceFileService).createSequenceFileInSample(Matchers.any(SequenceFile.class), Matchers.eq(s));

		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
		assertNotNull(locations);
		assertFalse(locations.isEmpty());
		assertEquals(1, locations.size());
		assertEquals(
				"http://localhost/api/projects/" + p.getId() + "/samples/" + s.getId() + "/sequenceFiles/" + sf.getId(),
				locations.iterator().next());

		Files.delete(f);
	}

	@Test
	public void testAddExistingSequenceFileToSample() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFile sf = TestDataFactory.constructSequenceFile();
		Join<Sample, SequenceFile> r = new SampleSequenceFileJoin(s, sf);

		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.getSampleForProject(p, s.getId())).thenReturn(s);
		when(sequenceFileService.read(sf.getId())).thenReturn(sf);
		when(sampleService.addSequenceFileToSample(s, sf)).thenReturn(r);

		Map<String, String> requestBody = ImmutableMap.of(RESTSampleSequenceFilesController.SEQUENCE_FILE_ID_KEY, sf
				.getId().toString());

		ResponseEntity<String> response = controller.addExistingSequenceFileToSample(p.getId(), s.getId(), requestBody);

		verify(projectService).read(p.getId());
		verify(sampleService).getSampleForProject(p, s.getId());
		verify(sequenceFileService).read(sf.getId());
		verify(sampleService).addSequenceFileToSample(s, sf);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
		assertNotNull(locations);
		assertFalse(locations.isEmpty());
		assertEquals(1, locations.size());
		// the sequence file location is still the same, but we've added a new
		// relationship
		assertEquals(
				"http://localhost/api/projects/" + p.getId() + "/samples/" + s.getId() + "/sequenceFiles/" + sf.getId(),
				locations.iterator().next());
	}

	@Test
	public void testAddExistingSequenceFileToSampleBadRequest() {
		Map<String, String> requestBody = new HashMap<>();
		try {
			controller.addExistingSequenceFileToSample(1L, 1L, requestBody);
			fail();
		} catch (InvalidPropertyException e) {

		} catch (Exception e) {
			fail();
		}
	}
}
