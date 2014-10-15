package ca.corefacility.bioinformatics.irida.service.upload.galaxy.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.RemoteWorkflowServiceTestConfig;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.upload.galaxy.GalaxyUploadService;
import ca.corefacility.bioinformatics.irida.service.upload.galaxy.UploadSampleConversionServiceGalaxy;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class,
		NonWindowsLocalGalaxyConfig.class, WindowsLocalGalaxyConfig.class,
		AnalysisExecutionServiceTestConfig.class,
		RemoteWorkflowServiceTestConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/upload/galaxy/integration/GalaxyUploadServiceIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class GalaxyUploadServiceIT {

	@Autowired
	private AnalysisExecutionGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private Uploader<GalaxyProjectName, GalaxyAccountEmail> galaxyUploader;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ProjectSampleJoinRepository psjRepository;

	@Autowired
	private SampleSequenceFileJoinRepository ssfjRepository;

	private GalaxyUploadService galaxyUploadService;

	private Path sequenceFilePath;
	private GalaxyProjectName projectName;
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

		UploadSampleConversionServiceGalaxy sampleConversionService = new UploadSampleConversionServiceGalaxy(
				projectRepository, psjRepository, ssfjRepository);
		galaxyUploadService = new GalaxyUploadService(galaxyUploader,
				sampleConversionService);

		Path sequenceFilePathReal = Paths
				.get(AnalysisExecutionGalaxyITService.class.getResource(
						"testData1.fastq").toURI());

		sequenceFilePath = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath);
		Files.copy(sequenceFilePathReal, sequenceFilePath);

		projectName = new GalaxyProjectName("Name");
		accountName = localGalaxy.getUser1Name();
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
}
