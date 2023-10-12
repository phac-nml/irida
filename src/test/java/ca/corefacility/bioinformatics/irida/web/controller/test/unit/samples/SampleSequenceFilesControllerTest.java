package ca.corefacility.bioinformatics.irida.web.controller.test.unit.samples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.SequencingRunUploadStatus;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RESTSampleSequenceFilesController}.
 */
public class SampleSequenceFilesControllerTest {
	private RESTSampleSequenceFilesController controller;
	private SampleService sampleService;
	private SequencingRunService miseqRunService;
	private SequencingObjectService sequencingObjectService;
	private AnalysisService analysisService;
	private SequencingRun sequencingRun;

	private IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void setUp() {
		sampleService = mock(SampleService.class);
		miseqRunService = mock(SequencingRunService.class);
		sequencingObjectService = mock(SequencingObjectService.class);
		analysisService = mock(AnalysisService.class);
		sequencingRun = mock(SequencingRun.class);

		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl(true);
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);

		controller = new RESTSampleSequenceFilesController(sampleService, miseqRunService, sequencingObjectService,
				analysisService);

	}

	@Test
	public void testGetSampleSequenceFiles() throws IOException {
		Sample s = TestDataFactory.constructSample();
		SingleEndSequenceFile so = TestDataFactory.constructSingleEndSequenceFile();
		SampleSequencingObjectJoin r = new SampleSequencingObjectJoin(s, so);

		List<SampleSequencingObjectJoin> relationships = Lists.newArrayList(r);

		// mock out the service calls
		when(sampleService.read(s.getId())).thenReturn(s);

		when(sequencingObjectService.getSequencingObjectsForSample(s)).thenReturn(relationships);

		ResponseResource<ResourceCollection<SequenceFile>> responseResource = controller.getSampleSequenceFiles(
				s.getId());

		// verify that the service calls were used.
		verify(sampleService).read(s.getId());
		verify(sequencingObjectService).getSequencingObjectsForSample(s);

		ResourceCollection<SequenceFile> resources = responseResource.getResource();
		assertNotNull(resources);
		assertEquals(1, resources.size());

		Link selfCollection = resources.getLink(IanaLinkRelations.SELF.value()).map(i -> i).orElse(null);
		Link sample = resources.getLink(RESTSampleSequenceFilesController.REL_SAMPLE).map(i -> i).orElse(null);
		String sampleLocation = "http://localhost/api/samples/" + s.getId();
		String sequenceFileLocation =
				sampleLocation + "/unpaired/" + so.getIdentifier() + "/files/" + so.getSequenceFile().getId();

		assertEquals(sampleLocation + "/sequenceFiles", selfCollection.getHref());
		assertEquals(sampleLocation, sample.getHref());

		// confirm that the self rel for an individual sequence file exists
		SequenceFile sfr = resources.iterator().next();
		Link self = sfr.getLink(IanaLinkRelations.SELF.value()).map(i -> i).orElse(null);
		assertEquals(sequenceFileLocation, self.getHref());
		assertEquals(so.getSequenceFile().getFile(), sfr.getFile());
	}

	@Test
	public void testRemoveSequenceFileFromSample() throws Exception {
		Sample s = TestDataFactory.constructSample();
		SingleEndSequenceFile so = TestDataFactory.constructSingleEndSequenceFile();

		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequencingObjectService.readSequencingObjectForSample(s, so.getId())).thenReturn(so);

		ResponseResource<RootResource> responseResource = controller.removeSequenceFileFromSample(s.getId(), "unpaired",
				so.getId());

		verify(sampleService, times(1)).read(s.getId());
		verify(sequencingObjectService).readSequencingObjectForSample(s, so.getId());
		verify(sampleService, times(1)).removeSequencingObjectFromSample(s, so);

		RootResource resource = responseResource.getResource();
		assertNotNull(resource);

		Link sample = resource.getLink(RESTSampleSequenceFilesController.REL_SAMPLE).map(i -> i).orElse(null);
		Link sequenceFiles = resource.getLink(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES)
				.map(i -> i)
				.orElse(null);

		String sampleLocation = "http://localhost/api/samples/" + s.getId();

		assertNotNull(sample);
		assertEquals(sampleLocation, sample.getHref());
		assertNotNull(sequenceFiles);
		assertEquals(sampleLocation + "/sequenceFiles", sequenceFiles.getHref());
	}

	@Test
	public void testGetSequenceFileForSample() throws IOException {
		Sample s = TestDataFactory.constructSample();
		SingleEndSequenceFile so = TestDataFactory.constructSingleEndSequenceFile();

		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequencingObjectService.readSequencingObjectForSample(s, so.getId())).thenReturn(so);

		ResponseResource<SequenceFile> modelMap = controller.readSequenceFileForSequencingObject(s.getId(),
				RESTSampleSequenceFilesController.objectLabels.get(so.getClass()), so.getId(),
				so.getSequenceFile().getId());

		verify(sampleService).read(s.getId());
		verify(sequencingObjectService).readSequencingObjectForSample(s, so.getId());

		Object o = modelMap.getResource();
		assertNotNull(o);
		assertTrue(o instanceof SequenceFile);
		SequenceFile sfr = (SequenceFile) o;
		assertEquals(so.getSequenceFile().getFile(), sfr.getFile());

		Link self = sfr.getLink(IanaLinkRelations.SELF.value()).map(i -> i).orElse(null);
		Link sampleSequenceFiles = sfr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES)
				.map(i -> i)
				.orElse(null);
		Link sample = sfr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE).map(i -> i).orElse(null);

		String sampleLocation = "http://localhost/api/samples/" + s.getId();
		String sequenceFileLocation =
				sampleLocation + "/unpaired/" + so.getIdentifier() + "/files/" + so.getSequenceFile().getId();

		assertNotNull(self);
		assertEquals(sequenceFileLocation, self.getHref());
		assertNotNull(sampleSequenceFiles);
		assertEquals(sampleLocation + "/sequenceFiles", sampleSequenceFiles.getHref());
		assertNotNull(sample);
		assertEquals(sampleLocation, sample.getHref());
	}

	@Test
	public void testGetQcForFile() throws IOException {
		Sample s = TestDataFactory.constructSample();
		SingleEndSequenceFile so = TestDataFactory.constructSingleEndSequenceFile();

		AnalysisFastQC analysisFastQC = new AnalysisFastQC(AnalysisFastQC.builder());

		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequencingObjectService.readSequencingObjectForSample(s, so.getId())).thenReturn(so);
		when(analysisService.getFastQCAnalysisForSequenceFile(so, so.getSequenceFile().getId())).thenReturn(
				analysisFastQC);

		ResponseResource<AnalysisFastQC> readQCForSequenceFile = controller.readQCForSequenceFile(s.getId(),
				RESTSampleSequenceFilesController.objectLabels.get(so.getClass()), so.getId(),
				so.getSequenceFile().getId());

		verify(sampleService).read(s.getId());
		verify(sequencingObjectService).readSequencingObjectForSample(s, so.getId());
		verify(analysisService).getFastQCAnalysisForSequenceFile(so, so.getSequenceFile().getId());

		Object object = readQCForSequenceFile.getResource();
		assertTrue(object instanceof AnalysisFastQC);

		AnalysisFastQC qc = (AnalysisFastQC) object;

		assertNotNull(qc.getLink(RESTSampleSequenceFilesController.REL_QC_SEQFILE));
	}

	@Test
	public void testCantGetSequenceFileForOtherSample() {
		Sample s = TestDataFactory.constructSample();

		Long objectId = 5L;
		Long fileId = 3L;

		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequencingObjectService.readSequencingObjectForSample(s, objectId)).thenThrow(
				new EntityNotFoundException("not in sample"));

		assertThrows(EntityNotFoundException.class, () -> {
			controller.readSequenceFileForSequencingObject(s.getId(), "unpaired", objectId, fileId);
		});
	}

	@Test
	public void testAddNewSequenceFileToSample() throws IOException {
		Sample s = TestDataFactory.constructSample();
		SingleEndSequenceFile so = TestDataFactory.constructSingleEndSequenceFile();
		SequenceFile sf = so.getSequenceFile();
		SampleSequencingObjectJoin sso = new SampleSequencingObjectJoin(s, so);

		SequenceFileResource resource = new SequenceFileResource();
		resource.setMiseqRunId(6L);
		Path f = Files.createTempFile(null, null);
		MockMultipartFile mmf = new MockMultipartFile("filename", "filename", "blurgh",
				FileCopyUtils.copyToByteArray(f.toFile()));
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequencingObjectService.createSequencingObjectInSample(any(SingleEndSequenceFile.class),
				ArgumentMatchers.eq(s))).thenReturn(sso);
		when(sequencingObjectService.read(so.getId())).thenReturn(so);

		when(miseqRunService.read(any(long.class))).thenReturn(sequencingRun);
		when(sequencingRun.getUploadStatus()).thenReturn(SequencingRunUploadStatus.UPLOADING);

		ResponseResource<SequenceFile> responseResource = controller.addNewSequenceFileToSample(s.getId(), mmf,
				resource, response);
		verify(sampleService).read(s.getId());
		verify(sampleService, times(1)).read(s.getId());
		verify(sequencingObjectService).createSequencingObjectInSample(any(SingleEndSequenceFile.class),
				ArgumentMatchers.eq(s));

		SequenceFile sfr = responseResource.getResource();
		assertNotNull(sfr, "sequence file must not be null");

		assertEquals(HttpStatus.CREATED.value(), response.getStatus(), "response must have CREATED status");
		Link self = sfr.getLink(IanaLinkRelations.SELF.value()).map(i -> i).orElse(null);
		Link sampleSequenceFiles = sfr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES)
				.map(i -> i)
				.orElse(null);
		Link sample = sfr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE).map(i -> i).orElse(null);

		String sampleLocation = "http://localhost/api/samples/" + s.getId();
		String sequenceFileLocation = sampleLocation + "/unpaired/" + so.getIdentifier() + "/files/" + sf.getId();
		assertNotNull(self, "self reference must exist");
		assertEquals(sequenceFileLocation, self.getHref(), "self reference must be correct");
		assertNotNull(sampleSequenceFiles, "sequence files link must exist");
		assertEquals(sampleLocation + "/sequenceFiles", sampleSequenceFiles.getHref(),
				"sequence files location must be correct");
		assertNotNull(sample, "sample link must exist");
		assertEquals(sampleLocation, sample.getHref(), "sample location must be correct");

		Files.delete(f);
	}

	@Test
	public void testAddNewSequenceFileToSampleCompletedRun() throws IOException {
		Sample s = TestDataFactory.constructSample();
		SingleEndSequenceFile so = TestDataFactory.constructSingleEndSequenceFile();
		SampleSequencingObjectJoin sso = new SampleSequencingObjectJoin(s, so);

		SequenceFileResource resource = new SequenceFileResource();
		resource.setMiseqRunId(8L);
		Path f = Files.createTempFile(null, null);
		MockMultipartFile mmf = new MockMultipartFile("filename", "filename", "blurgh",
				FileCopyUtils.copyToByteArray(f.toFile()));
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequencingObjectService.createSequencingObjectInSample(any(SingleEndSequenceFile.class),
				ArgumentMatchers.eq(s))).thenReturn(sso);
		when(sequencingObjectService.read(so.getId())).thenReturn(so);

		when(miseqRunService.read(any(long.class))).thenReturn(sequencingRun);
		when(sequencingRun.getUploadStatus()).thenReturn(SequencingRunUploadStatus.COMPLETE);

		assertThrows(IllegalArgumentException.class, () -> {
			controller.addNewSequenceFileToSample(s.getId(), mmf, resource, response);
		});
	}

	@Test
	public void testAddNewSequenceFileToSampleErrorRun() throws IOException {
		Sample s = TestDataFactory.constructSample();
		SingleEndSequenceFile so = TestDataFactory.constructSingleEndSequenceFile();
		SampleSequencingObjectJoin sso = new SampleSequencingObjectJoin(s, so);

		SequenceFileResource resource = new SequenceFileResource();
		resource.setMiseqRunId(8L);
		Path f = Files.createTempFile(null, null);
		MockMultipartFile mmf = new MockMultipartFile("filename", "filename", "blurgh",
				FileCopyUtils.copyToByteArray(f.toFile()));
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(sampleService.read(s.getId())).thenReturn(s);
		when(sequencingObjectService.createSequencingObjectInSample(any(SingleEndSequenceFile.class),
				ArgumentMatchers.eq(s))).thenReturn(sso);
		when(sequencingObjectService.read(so.getId())).thenReturn(so);

		when(miseqRunService.read(any(long.class))).thenReturn(sequencingRun);
		when(sequencingRun.getUploadStatus()).thenReturn(SequencingRunUploadStatus.ERROR);

		assertThrows(IllegalArgumentException.class, () -> {
			controller.addNewSequenceFileToSample(s.getId(), mmf, resource, response);
		});
	}

	@Test
	public void testAddNewSequenceFilePairToSample() throws IOException {
		Sample s = TestDataFactory.constructSample();

		String file1Name = "file1_R1_01.fastq.gz";
		String file2Name = "file2_R2_01.fastq.gz";

		SequenceFilePair pair = TestDataFactory.constructSequenceFilePair();
		Iterator<SequenceFile> iterator = pair.getFiles().iterator();
		SequenceFile sf1 = iterator.next();
		SequenceFile sf2 = iterator.next();

		sf1.setFile(Paths.get(file1Name));
		sf2.setFile(Paths.get(file2Name));

		sf1.setId(3245L);

		SampleSequencingObjectJoin sso = new SampleSequencingObjectJoin(s, pair);

		SequenceFileResource resource1 = new SequenceFileResource();
		SequenceFileResource resource2 = new SequenceFileResource();
		resource1.setMiseqRunId(7L);
		resource2.setMiseqRunId(7L);
		Path f1 = Files.createTempFile(null, null);
		Path f2 = Files.createTempFile(null, null);
		MockMultipartFile mmf1 = new MockMultipartFile(file1Name, file1Name, "blurgh1",
				FileCopyUtils.copyToByteArray(f1.toFile()));
		MockMultipartFile mmf2 = new MockMultipartFile(file2Name, file2Name, "blurgh2",
				FileCopyUtils.copyToByteArray(f2.toFile()));
		MockHttpServletResponse response = new MockHttpServletResponse();
		// mock out the service calls
		when(sampleService.read(s.getId())).thenReturn(s);

		when(sequencingObjectService.createSequencingObjectInSample(any(SequenceFilePair.class),
				ArgumentMatchers.eq(s))).thenReturn(sso);

		when(miseqRunService.read(any(long.class))).thenReturn(sequencingRun);

		when(sequencingRun.getUploadStatus()).thenReturn(SequencingRunUploadStatus.UPLOADING);

		ResponseResource<SequencingObject> responseResource = controller.addNewSequenceFilePairToSample(s.getId(), mmf1,
				resource1, mmf2, resource2, response);

		verify(sampleService).read(s.getId());
		verify(sequencingObjectService).createSequencingObjectInSample(any(SequenceFilePair.class),
				ArgumentMatchers.eq(s));

		SequenceFilePair returnVal = (SequenceFilePair) responseResource.getResource();
		assertNotNull(returnVal, "sequence file pair should not be null");

		Link selfCollection = returnVal.getLink(IanaLinkRelations.SELF.value()).map(i -> i).orElse(null);
		Link sampleRC = returnVal.getLink(RESTSampleSequenceFilesController.REL_SAMPLE).map(i -> i).orElse(null);
		Link sampleSequenceFiles = returnVal.getLink(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES)
				.map(i -> i)
				.orElse(null);

		String sampleLocation = "http://localhost/api/samples/" + s.getId();
		String pairLocation = sampleLocation + "/pairs/" + pair.getId();
		assertEquals(pairLocation, selfCollection.getHref(), "Pair location should be correct");
		assertEquals(sampleLocation, sampleRC.getHref(), "Sample location should be correct");
		assertEquals(sampleLocation + "/sequenceFiles", sampleSequenceFiles.getHref(),
				"Sequence file location should be correct");

		String sequenceFileLocation1 = pairLocation + "/files/" + sf1.getId();
		String sequenceFileLocation2 = pairLocation + "/files/" + sf2.getId();

		String[] sequenceFileLocs = new String[] { sequenceFileLocation1, sequenceFileLocation2 };
		String locationHeader = response.getHeader(HttpHeaders.LOCATION);

		assertEquals(pairLocation, locationHeader, "The location header should have the self rel");

		Iterator<SequenceFile> filesIterator = returnVal.getFiles().iterator();

		for (int i = 0; i < 2; i++) {
			SequenceFile returnedFile = filesIterator.next();
			Link self = returnedFile.getLink(IanaLinkRelations.SELF.value()).map(l -> l).orElse(null);

			assertNotNull(self, "Self link should not be null");
			assertEquals(sequenceFileLocs[i], self.getHref(), "Self reference should be correct");
		}
		assertEquals(HttpStatus.CREATED.value(), response.getStatus(), "HTTP status must be CREATED");
		Files.delete(f1);
		Files.delete(f2);
	}

	@Test
	public void testAddNewSequenceFilePairToSampleMismatchedRunIDs() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFilePair pair = TestDataFactory.constructSequenceFilePair();

		SampleSequencingObjectJoin sso = new SampleSequencingObjectJoin(s, pair);

		SequenceFileResource resource1 = new SequenceFileResource();
		SequenceFileResource resource2 = new SequenceFileResource();
		resource1.setMiseqRunId(1L);
		resource2.setMiseqRunId(2L);
		Path f1 = Files.createTempFile(null, null);
		Path f2 = Files.createTempFile(null, null);
		MockMultipartFile mmf1 = new MockMultipartFile("filename1", "filename1", "blurgh1",
				FileCopyUtils.copyToByteArray(f1.toFile()));
		MockMultipartFile mmf2 = new MockMultipartFile("filename2", "filename2", "blurgh2",
				FileCopyUtils.copyToByteArray(f2.toFile()));
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(sampleService.getSampleForProject(p, s.getId())).thenReturn(new ProjectSampleJoin(p, s, true));

		when(sequencingObjectService.createSequencingObjectInSample(any(SequenceFilePair.class),
				ArgumentMatchers.eq(s))).thenReturn(sso);
		assertThrows(IllegalArgumentException.class, () -> {
			controller.addNewSequenceFilePairToSample(s.getId(), mmf1, resource1, mmf2, resource2, response);
		});
	}

	@Test
	public void testAddNewSequenceFilePairToSampleCompletedRun() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFilePair pair = TestDataFactory.constructSequenceFilePair();

		SampleSequencingObjectJoin sso = new SampleSequencingObjectJoin(s, pair);

		SequenceFileResource resource1 = new SequenceFileResource();
		SequenceFileResource resource2 = new SequenceFileResource();
		resource1.setMiseqRunId(4L);
		resource2.setMiseqRunId(4L);
		Path f1 = Files.createTempFile(null, null);
		Path f2 = Files.createTempFile(null, null);
		MockMultipartFile mmf1 = new MockMultipartFile("filename1", "filename1", "blurgh1",
				FileCopyUtils.copyToByteArray(f1.toFile()));
		MockMultipartFile mmf2 = new MockMultipartFile("filename2", "filename2", "blurgh2",
				FileCopyUtils.copyToByteArray(f2.toFile()));
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(sampleService.getSampleForProject(p, s.getId())).thenReturn(new ProjectSampleJoin(p, s, true));

		when(sequencingObjectService.createSequencingObjectInSample(any(SequenceFilePair.class),
				ArgumentMatchers.eq(s))).thenReturn(sso);

		when(miseqRunService.read(any(long.class))).thenReturn(sequencingRun);

		when(sequencingRun.getUploadStatus()).thenReturn(SequencingRunUploadStatus.COMPLETE);
		assertThrows(IllegalArgumentException.class, () -> {
			controller.addNewSequenceFilePairToSample(s.getId(), mmf1, resource1, mmf2, resource2, response);
		});
	}

	@Test
	public void testAddNewSequenceFilePairToSampleErrorRun() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFilePair pair = TestDataFactory.constructSequenceFilePair();

		SampleSequencingObjectJoin sso = new SampleSequencingObjectJoin(s, pair);

		SequenceFileResource resource1 = new SequenceFileResource();
		SequenceFileResource resource2 = new SequenceFileResource();
		resource1.setMiseqRunId(4L);
		resource2.setMiseqRunId(4L);
		Path f1 = Files.createTempFile(null, null);
		Path f2 = Files.createTempFile(null, null);
		MockMultipartFile mmf1 = new MockMultipartFile("filename1", "filename1", "blurgh1",
				FileCopyUtils.copyToByteArray(f1.toFile()));
		MockMultipartFile mmf2 = new MockMultipartFile("filename2", "filename2", "blurgh2",
				FileCopyUtils.copyToByteArray(f2.toFile()));
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(sampleService.getSampleForProject(p, s.getId())).thenReturn(new ProjectSampleJoin(p, s, true));

		when(sequencingObjectService.createSequencingObjectInSample(any(SequenceFilePair.class),
				ArgumentMatchers.eq(s))).thenReturn(sso);

		when(miseqRunService.read(any(long.class))).thenReturn(sequencingRun);

		when(sequencingRun.getUploadStatus()).thenReturn(SequencingRunUploadStatus.ERROR);
		assertThrows(IllegalArgumentException.class, () -> {
			controller.addNewSequenceFilePairToSample(s.getId(), mmf1, resource1, mmf2, resource2, response);
		});
	}

}
