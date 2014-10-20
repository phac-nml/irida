package ca.corefacility.bioinformatics.irida.service.upload.galaxy.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.validation.ConstraintViolationException;

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

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.IridaApiServicesTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.upload.galaxy.GalaxyUploadService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiServicesTestConfig.class,
		IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/upload/galaxy/integration/UploadIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class GalaxyUploadServiceIT {

	@Autowired
	private DatabaseSetupGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private GalaxyUploadService galaxyUploadService;

	private Path sequenceFilePath;
	private static final GalaxyProjectName invalidProjectName = new GalaxyProjectName(
			"  __Project__.&'");
	private static final GalaxyAccountEmail invalidAccountName = new GalaxyAccountEmail(
			"x");
	private GalaxyProjectName projectName;
	private GalaxyAccountEmail fakeAccountName;
	private GalaxyAccountEmail accountName;

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());

		Path sequenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource(
						"testData1.fastq").toURI());

		sequenceFilePath = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath);
		Files.copy(sequenceFilePathReal, sequenceFilePath);

		projectName = new GalaxyProjectName("Name");
		accountName = localGalaxy.getUser1Name();
		fakeAccountName = localGalaxy.getNonExistentGalaxyUserName();
	}

	/**
	 * Tests successfully uploading a sample with 1 sequence file to Galaxy.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadAllSamplesSuccess() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath);

		UploadWorker uploadWorker = galaxyUploadService
				.buildUploadWorkerAllSamples(1L, projectName, accountName);

		uploadWorker.run();

		assertFalse(uploadWorker.exceptionOccured());
		UploadResult uploadResult = uploadWorker.getUploadResult();

		assertNotNull(uploadResult.getDataLocation());
		assertEquals(projectName, uploadResult.getLocationName());
	}

	/**
	 * Tests failing to upload a sample due to permissions.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testUploadAllSamplesFailPermission() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath);

		galaxyUploadService.buildUploadWorkerAllSamples(1L, projectName,
				accountName);
	}

	/**
	 * Tests failing to upload samples to Galaxy (invalid project id).
	 */
	@Test(expected = EntityNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadAllSamplesFailProjectIdInvalid() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath);

		galaxyUploadService.buildUploadWorkerAllSamples(2L, invalidProjectName,
				accountName);
	}

	/**
	 * Tests failing to upload samples to Galaxy (invalid project name).
	 */
	@Test(expected = ConstraintViolationException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadAllSamplesFailProjectInvalid() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath);

		galaxyUploadService.buildUploadWorkerAllSamples(1L, invalidProjectName,
				accountName);
	}

	/**
	 * Tests failing to upload samples to Galaxy (invalid account name).
	 */
	@Test(expected = ConstraintViolationException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadAllSamplesFailAccountInvalid() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath);

		galaxyUploadService.buildUploadWorkerAllSamples(1L, projectName,
				invalidAccountName);
	}

	/**
	 * Tests failing to upload samples to Galaxy (non-existent account).
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadAllSamplesFailNoAccount() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath);

		UploadWorker uploadWorker = galaxyUploadService
				.buildUploadWorkerAllSamples(1L, projectName, fakeAccountName);

		uploadWorker.run();

		assertTrue(uploadWorker.exceptionOccured());
		assertEquals(GalaxyUserNotFoundException.class, uploadWorker
				.getUploadException().getClass());
	}

	/**
	 * Tests successfully uploading a set of samples to Galaxy.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadSelectedSamplesSuccess() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath);

		Project project = projectRepository.findOne(1L);
		Sample sample = sampleService.getSampleForProject(project, 1L);

		UploadWorker uploadWorker = galaxyUploadService
				.buildUploadWorkerSelectedSamples(Sets.newHashSet(sample),
						projectName, accountName);

		uploadWorker.run();

		assertFalse(uploadWorker.exceptionOccured());
		UploadResult uploadResult = uploadWorker.getUploadResult();

		assertNotNull(uploadResult.getDataLocation());
		assertEquals(projectName, uploadResult.getLocationName());
	}

	/**
	 * Tests success in permissions for regular user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testUploadSelectedSamplesSuccessPermissions() {
		Sample sample = new Sample("Sample");
		sample.setId(1L);

		galaxyUploadService.buildUploadWorkerSelectedSamples(
				Sets.newHashSet(sample), projectName, accountName);
	}

	/**
	 * Tests failing to upload a set of samples to Galaxy due to permissions.
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testUploadSelectedSamplesFailPermissions() {
		Sample sample = new Sample("Sample");
		sample.setId(1L);

		galaxyUploadService.buildUploadWorkerSelectedSamples(
				Sets.newHashSet(sample), projectName, accountName);
	}

	/**
	 * Tests failing to upload a set of samples to Galaxy (invalid project
	 * name).
	 */
	@Test(expected = ConstraintViolationException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadSelectedSamplesFailProjectInvalid() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath);

		Project project = projectRepository.findOne(1L);
		Sample sample = sampleService.getSampleForProject(project, 1L);

		galaxyUploadService.buildUploadWorkerSelectedSamples(
				Sets.newHashSet(sample), invalidProjectName, accountName);
	}

	/**
	 * Tests failing to upload a set of samples to Galaxy (invalid account
	 * name).
	 */
	@Test(expected = ConstraintViolationException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadSelectedSamplesFailAccountInvalid() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath);

		Project project = projectRepository.findOne(1L);
		Sample sample = sampleService.getSampleForProject(project, 1L);

		galaxyUploadService.buildUploadWorkerSelectedSamples(
				Sets.newHashSet(sample), projectName, invalidAccountName);
	}

	/**
	 * Tests failing to upload samples to Galaxy (non-existent account).
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUploadSelectedSamplesFailNoAccount() {
		analysisExecutionGalaxyITService.setupSampleSequenceFileInDatabase(1L,
				sequenceFilePath);

		Project project = projectRepository.findOne(1L);
		Sample sample = sampleService.getSampleForProject(project, 1L);

		UploadWorker uploadWorker = galaxyUploadService
				.buildUploadWorkerSelectedSamples(Sets.newHashSet(sample),
						projectName, fakeAccountName);

		uploadWorker.run();

		assertTrue(uploadWorker.exceptionOccured());
		assertEquals(GalaxyUserNotFoundException.class, uploadWorker
				.getUploadException().getClass());
	}
}
