package ca.corefacility.bioinformatics.irida.service.upload.galaxy.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assume;
import org.junit.Before;
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

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.upload.galaxy.UploadSampleConversionServiceGalaxy;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/upload/galaxy/integration/UploadIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UploadConversionServiceGalaxyIT {

	@Autowired
	private DatabaseSetupGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private SequenceFileService sequenceFileService;

	@Autowired
	private ProjectRepository projectRepository;

	private Path sequenceFilePath1;
	private Path sequenceFilePath2;
	private Path sequenceFilePath2b;

	@Autowired
	private UploadSampleConversionServiceGalaxy sampleConversionService;

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());

		Path sequenceFilePathReal1 = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource(
						"testData1.fastq").toURI());

		sequenceFilePath1 = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath1);
		Files.copy(sequenceFilePathReal1, sequenceFilePath1);

		Path sequenceFilePathReal2 = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource(
						"testData2.fastq").toURI());

		sequenceFilePath2 = Files.createTempFile("testData2", ".fastq");
		Files.delete(sequenceFilePath2);
		Files.copy(sequenceFilePathReal2, sequenceFilePath2);

		sequenceFilePath2b = Files.createTempFile("testData2b", ".fastq");
		Files.delete(sequenceFilePath2b);
		Files.copy(sequenceFilePathReal2, sequenceFilePath2b);
	}

	/**
	 * Tests successfully converting a single sample to an upload sample.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testConvertSingleSampleSuccess() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath1, sequenceFilePath2);

		Project project = projectRepository.findOne(1L);
		Sample sample = sampleService.getSampleForProject(project, 1L);

		UploadSample uploadSample = sampleConversionService
				.convertToUploadSample(sample);

		assertEquals(sample.getSampleName(), uploadSample.getSampleName()
				.getName());
		List<Path> sampleFiles = uploadSample.getSampleFiles();
		assertEquals(2, sampleFiles.size());
	}

	/**
	 * Tests successfully converting a single sample to an upload sample.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testConvertSampleSetSuccess() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath1, sequenceFilePath2);

		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(2L,
				sequenceFilePath2b);

		Project project = projectRepository.findOne(1L);
		Sample sample1 = sampleService.getSampleForProject(project, 1L);
		Sample sample2 = sampleService.getSampleForProject(project, 2L);

		Set<UploadSample> uploadSamples = sampleConversionService
				.convertToUploadSamples(Sets.newHashSet(sample1, sample2));

		assertEquals(2, uploadSamples.size());
	}

	/**
	 * Tests successfully converting a single sample (permissions).
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testConvertSampleSetSuccessPermissions() {
		Sample sample1 = new Sample("Sample1");
		sample1.setId(1L);
		Sample sample2 = new Sample("Sample2");
		sample2.setId(2L);

		sampleConversionService.convertToUploadSamples(Sets.newHashSet(sample1,
				sample2));
	}

	/**
	 * Tests failing to converting a single sample due to permissions
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testConvertSampleSetFailPermissions() {
		Sample sample1 = new Sample("Sample1");
		sample1.setId(1L);
		Sample sample2 = new Sample("Sample2");
		sample2.setId(2L);

		sampleConversionService.convertToUploadSamples(Sets.newHashSet(sample1,
				sample2));
	}

	/**
	 * Tests successfully converting a single sequence file to an upload sample.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testConvertSingleSequenceFileSuccess() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath1);

		Project project = projectRepository.findOne(1L);
		Sample sample = sampleService.getSampleForProject(project, 1L);
		List<Join<Sample, SequenceFile>> sampleSequenceFiles = sequenceFileService
				.getSequenceFilesForSample(sample);
		assertEquals(1, sampleSequenceFiles.size());

		SequenceFile sequenceFile = sampleSequenceFiles.get(0).getObject();

		Set<UploadSample> uploadSamples = sampleConversionService
				.convertSequenceFilesToUploadSamples(Sets
						.newHashSet(sequenceFile));

		assertEquals(1, uploadSamples.size());
		UploadSample uploadSample = uploadSamples.iterator().next();

		assertEquals(sample.getSampleName(), uploadSample.getSampleName()
				.getName());
		List<Path> sampleFiles = uploadSample.getSampleFiles();
		assertEquals(1, sampleFiles.size());
	}

	/**
	 * Tests failing to convert a sequence file to an UploadSample due to
	 * permission issues.
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testConvertSingleSequenceFileFailPermissions() {
		List<SequenceFile> sequenceFiles = analysisExecutionGalaxyITService
				.setupSampleSequenceFileInDatabase(1L, sequenceFilePath1);

		SequenceFile sequenceFile = sequenceFiles.get(0);

		sampleConversionService.convertSequenceFilesToUploadSamples(Sets
				.newHashSet(sequenceFile));
	}

	/**
	 * Tests successfully converting two sequence files in the same sample to an
	 * upload sample.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testConvertTwoSequenceFilesSuccess() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath1, sequenceFilePath2);

		Project project = projectRepository.findOne(1L);
		Sample sample = sampleService.getSampleForProject(project, 1L);
		List<Join<Sample, SequenceFile>> sampleSequenceFiles = sequenceFileService
				.getSequenceFilesForSample(sample);
		assertEquals(2, sampleSequenceFiles.size());

		SequenceFile sequenceFile1 = sampleSequenceFiles.get(0).getObject();
		SequenceFile sequenceFile2 = sampleSequenceFiles.get(1).getObject();

		Set<UploadSample> uploadSamples = sampleConversionService
				.convertSequenceFilesToUploadSamples(Sets.newHashSet(
						sequenceFile1, sequenceFile2));

		assertEquals(1, uploadSamples.size());
		UploadSample uploadSample = uploadSamples.iterator().next();

		assertEquals(sample.getSampleName(), uploadSample.getSampleName()
				.getName());
		List<Path> sampleFiles = uploadSample.getSampleFiles();
		assertEquals(2, sampleFiles.size());
	}
	
	/**
	 * Tests successfully converting two sequence files by ids in the same sample to an
	 * upload sample.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testConvertTwoSequenceFilesByIdsSuccess() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath1, sequenceFilePath2);

		Project project = projectRepository.findOne(1L);
		Sample sample = sampleService.getSampleForProject(project, 1L);
		List<Join<Sample, SequenceFile>> sampleSequenceFiles = sequenceFileService
				.getSequenceFilesForSample(sample);
		assertEquals(2, sampleSequenceFiles.size());

		SequenceFile sequenceFile1 = sampleSequenceFiles.get(0).getObject();
		SequenceFile sequenceFile2 = sampleSequenceFiles.get(1).getObject();

		Set<UploadSample> uploadSamples = sampleConversionService
				.convertSequenceFilesByIdToUploadSamples(Sets.newHashSet(
						sequenceFile1.getId(), sequenceFile2.getId()));

		assertEquals(1, uploadSamples.size());
		UploadSample uploadSample = uploadSamples.iterator().next();

		assertEquals(sample.getSampleName(), uploadSample.getSampleName()
				.getName());
		List<Path> sampleFiles = uploadSample.getSampleFiles();
		assertEquals(2, sampleFiles.size());
	}
	
	/**
	 * Tests failing to convert two sequence files by ids in the same sample to an
	 * upload sample due to permissions.
	 */
	@Test(expected=AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testConvertTwoSequenceFilesByIdsFailPermission() {
		List<SequenceFile> sequenceFiles = analysisExecutionGalaxyITService
				.setupSampleSequenceFileInDatabase(1L, sequenceFilePath1);

		SequenceFile sequenceFile = sequenceFiles.get(0);

		sampleConversionService.convertSequenceFilesByIdToUploadSamples(Sets
				.newHashSet(sequenceFile.getId()));
	}

	/**
	 * Tests successfully converting two sequence files in a different sample an
	 * upload sample.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testConvertTwoSequenceFilesDifferentSampleSuccess() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath1);
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(2L,
				sequenceFilePath2);

		Project project = projectRepository.findOne(1L);
		Sample sample1 = sampleService.getSampleForProject(project, 1L);
		List<Join<Sample, SequenceFile>> sampleSequenceFiles = sequenceFileService
				.getSequenceFilesForSample(sample1);
		assertEquals(1, sampleSequenceFiles.size());
		SequenceFile sequenceFile1 = sampleSequenceFiles.get(0).getObject();

		Sample sample2 = sampleService.getSampleForProject(project, 2L);
		sampleSequenceFiles = sequenceFileService
				.getSequenceFilesForSample(sample2);
		assertEquals(1, sampleSequenceFiles.size());
		SequenceFile sequenceFile2 = sampleSequenceFiles.get(0).getObject();

		Set<UploadSample> uploadSamples = sampleConversionService
				.convertSequenceFilesToUploadSamples(Sets.newHashSet(
						sequenceFile1, sequenceFile2));

		assertEquals(2, uploadSamples.size());
		Iterator<UploadSample> uploadSamplesIter = uploadSamples.iterator();
		UploadSample uploadSample1 = uploadSamplesIter.next();
		UploadSample uploadSample2 = uploadSamplesIter.next();

		UploadSample uploadSampleA = null, uploadSampleB = null;
		Sample sampleA = null, sampleB = null;
		if (sample1.getSampleName().equals(
				uploadSample1.getSampleName().getName())) {
			sampleA = sample1;
			uploadSampleA = uploadSample1;
		} else if (sample1.getSampleName().equals(
				uploadSample2.getSampleName().getName())) {
			sampleA = sample1;
			uploadSampleA = uploadSample2;
		} else {
			fail("Could not match up sample A and uploadSample A");
		}

		if (sample2.getSampleName().equals(
				uploadSample2.getSampleName().getName())) {
			sampleB = sample2;
			uploadSampleB = uploadSample2;
		} else if (sample2.getSampleName().equals(
				uploadSample1.getSampleName().getName())) {
			sampleB = sample2;
			uploadSampleB = uploadSample1;
		} else {
			fail("Could not match up sample B and uploadSample B");
		}

		assertEquals(sampleA.getSampleName(), uploadSampleA.getSampleName()
				.getName());
		assertEquals(sampleB.getSampleName(), uploadSampleB.getSampleName()
				.getName());
		assertEquals(1, uploadSampleA.getSampleFiles().size());
		assertEquals(1, uploadSampleB.getSampleFiles().size());
	}
}
