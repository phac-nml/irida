package ca.corefacility.bioinformatics.irida.web.controller.test.unit.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
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
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
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
		miseqRunService= mock(SequencingRunService.class);

		controller = new RESTSampleSequenceFilesController(sequenceFileService, sampleService, projectService,miseqRunService);
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
		SampleSequenceFileJoin join = new SampleSequenceFileJoin(s, sf);
		SequenceFile pairFile = TestDataFactory.constructSequenceFile();

		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequenceFileService.getSequenceFileForSample(s, sf.getId())).thenReturn(join);
		when(sequenceFileService.getPairedFileForSequenceFile(sf)).thenReturn(pairFile);

		ModelMap modelMap = controller.getSequenceFileForSample(p.getId(), s.getId(), sf.getId());

		verify(projectService).read(p.getId());
		verify(sampleService).read(s.getId());
		verify(sequenceFileService).getSequenceFileForSample(s, sf.getId());
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

	@Test(expected = EntityNotFoundException.class)
	public void testCantGetSequenceFileForOtherSample() {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFile sf = new SequenceFile();
		sf.setId(5l);

		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequenceFileService.getSequenceFileForSample(s, sf.getId())).thenThrow(
				new EntityNotFoundException("not in sample"));

		controller.getSequenceFileForSample(p.getId(), s.getId(), sf.getId());

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
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequenceFileService.createSequenceFileInSample(Matchers.any(SequenceFile.class), Matchers.eq(s)))
				.thenReturn(r);
		when(projectService.read(p.getId())).thenReturn(p);
		when(sequenceFileService.read(sf.getId())).thenReturn(sf);
		ModelMap modelMap = controller.addNewSequenceFileToSample(p.getId(), s.getId(), mmf, resource,response);
		verify(sampleService).getSampleForProject(p, s.getId());
		verify(projectService).read(p.getId());
		verify(sampleService, times(1)).read(s.getId());
		verify(sequenceFileService).createSequenceFileInSample(Matchers.any(SequenceFile.class), Matchers.eq(s));
		
		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertNotNull(o);
		assertTrue(o instanceof SequenceFileResource);
		SequenceFileResource sfr = (SequenceFileResource) o;

		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		Link self = sfr.getLink(Link.REL_SELF);
		Link sampleSequenceFiles = sfr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);
		Link sample = sfr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE);
		
		String sampleLocation = "http://localhost/api/projects/" + p.getId() + "/samples/" + s.getId();
		String sequenceFileLocation = sampleLocation + "/sequenceFiles/" + sf.getId();
		assertNotNull(self);
		assertEquals(sequenceFileLocation, self.getHref());
		assertNotNull(sampleSequenceFiles);
		assertEquals(sampleLocation + "/sequenceFiles", sampleSequenceFiles.getHref());
		assertNotNull(sample);
		assertEquals(sampleLocation, sample.getHref());
		
		Files.delete(f);
	}
	
	@Test
	public void testAddNewSequenceFilePairToSample() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFile sf1 = TestDataFactory.constructSequenceFile();
		SequenceFile sf2 = TestDataFactory.constructSequenceFile();
		sf1.setId(3245l);
		Join<Sample, SequenceFile> r1 = new SampleSequenceFileJoin(s, sf1);
		Join<Sample, SequenceFile> r2 = new SampleSequenceFileJoin(s, sf2);
		List<Join<Sample, SequenceFile>> relationships = Lists.newArrayList();
		relationships.add(r1);
		relationships.add(r2);
		SequenceFileResource resource1 = new SequenceFileResource();
		SequenceFileResource resource2 = new SequenceFileResource();
		Path f1 = Files.createTempFile(null, null);
		Path f2 = Files.createTempFile(null, null);
		MockMultipartFile mmf1 = new MockMultipartFile("filename1", "filename1", "blurgh1", 
				FileCopyUtils.copyToByteArray(f1.toFile()));
		MockMultipartFile mmf2 = new MockMultipartFile("filename2", "filename2", "blurgh2", 
				FileCopyUtils.copyToByteArray(f2.toFile()));
		MockHttpServletResponse response = new MockHttpServletResponse();
		// mock out the service calls
		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.getSampleForProject(p, s.getId())).thenReturn(s);
		when(sampleService.getSampleForProject(p, s.getId())).thenReturn(s);
		when(sequenceFileService.createSequenceFilePairInSample(any(SequenceFile.class),
				any(SequenceFile.class),any(Sample.class))).thenReturn(relationships);
		ModelMap modelMap = controller.addNewSequenceFilePairToSample(p.getId(), s.getId(),
				mmf1, resource1, mmf2, resource2, response);
		verify(projectService).read(p.getId());
		verify(sampleService).getSampleForProject(p, s.getId());
		verify(sequenceFileService).createSequenceFilePairInSample(any(SequenceFile.class),
				any(SequenceFile.class),any(Sample.class));
		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertNotNull("Object should not be null",o);
		assertTrue("Object should be an instance of ResourceCollection",o instanceof ResourceCollection);
		@SuppressWarnings("unchecked")
		ResourceCollection<LabelledRelationshipResource<Sample,SequenceFile>> rc =
				(ResourceCollection<LabelledRelationshipResource<Sample,SequenceFile>>) o;
		assertTrue("Resource collection should have only 2 LabelledRelationshipResource instances",rc.size() == 2);
		Link selfCollection = rc.getLink(Link.REL_SELF);
		Link sampleRC = rc.getLink(RESTSampleSequenceFilesController.REL_SAMPLE);
		String sampleLocation = "http://localhost/api/projects/" + p.getId() + "/samples/" + s.getId();
		String sequenceFilesLocation = sampleLocation + "/sequenceFilePairs";
		assertEquals("Collection location should be correct",sequenceFilesLocation, selfCollection.getHref());
		assertEquals("Sample location should be correct",sampleLocation,sampleRC.getHref());
		
		String sequenceFileLocation1 = sampleLocation + "/sequenceFiles/" + sf1.getId();
		String sequenceFileLocation2 = sampleLocation + "/sequenceFiles/" + sf2.getId();
		String[] sequenceFileLocs = new String[] {sequenceFileLocation1,sequenceFileLocation2};	
		List<String> locations = response.getHeaders(HttpHeaders.LOCATION);
		assertNotNull(locations);
		assertFalse(locations.isEmpty());
		assertEquals(2, locations.size());
		SequenceFile[] sequences = new SequenceFile[] {sf1,sf2};
		for(int i = 0; i< 2; i++) {
			LabelledRelationshipResource<Sample,SequenceFile> lrr = rc.getResources().get(i);
			Link self = lrr.getLink(Link.REL_SELF);
			Link sampleSequenceFiles = lrr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);
			Link sample = lrr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE);
			assertNotNull("Self link should not be null",self);
			assertEquals("Self reference should be correct",sequenceFileLocs[i], self.getHref());
			assertNotNull("Sequence file location should not be null",sampleSequenceFiles);
			assertEquals("Sequence file location should be correct",sampleLocation + "/sequenceFiles", sampleSequenceFiles.getHref());
			assertNotNull("Sample location should not be null",sample);
			assertEquals("Sample location should be correct",sampleLocation, sample.getHref());
			assertEquals("http://localhost/api/projects/" + p.getId() + "/samples/" + s.getId() +
					"/sequenceFiles/" + sequences[i].getId(), locations.get(i));
		}
		assertEquals("HTTP status must be CREATED",HttpStatus.CREATED.value(), response.getStatus());
		Files.delete(f1);
		Files.delete(f2);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testAddNewSequenceFilePairToSampleMissingPairMember() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFile sf1 = TestDataFactory.constructSequenceFile();
		SequenceFile sf2 = TestDataFactory.constructSequenceFile();
		Join<Sample, SequenceFile> r1 = new SampleSequenceFileJoin(s, sf1);
		Join<Sample, SequenceFile> r2 = new SampleSequenceFileJoin(s, sf2);
		List<Join<Sample, SequenceFile>> relationships = Lists.newArrayList();
		relationships.add(r1);
		relationships.add(r2);
		SequenceFileResource resource1 = new SequenceFileResource();
		SequenceFileResource resource2 = new SequenceFileResource();
		resource1.setMiseqRunId(1l);
		resource2.setMiseqRunId(2l);
		Path f1 = Files.createTempFile(null, null);
		Path f2 = Files.createTempFile(null, null);
		MockMultipartFile mmf1 = new MockMultipartFile("filename1", "filename1", "blurgh1", 
				FileCopyUtils.copyToByteArray(f1.toFile()));
		MockMultipartFile mmf2 = new MockMultipartFile("filename2", "filename2", "blurgh2", 
				FileCopyUtils.copyToByteArray(f2.toFile()));
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.getSampleForProject(p, s.getId())).thenReturn(s);
		when(sampleService.getSampleForProject(p, s.getId())).thenReturn(s);
		when(sequenceFileService.createSequenceFilePairInSample(any(SequenceFile.class),
				any(SequenceFile.class),any(Sample.class))).thenReturn(relationships);
		ModelMap modelMap = controller.addNewSequenceFilePairToSample(p.getId(), s.getId(),
				mmf1, resource1, mmf2, resource2, response);
		modelMap.clear();
		Files.delete(f1);
		Files.delete(f2);
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
