package ca.corefacility.bioinformatics.irida.service.upload.galaxy.integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

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
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.upload.galaxy.UploadSampleConversionServiceGalaxy;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

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
public class UploadConversionServiceGalaxyIT {

	@Autowired
	private AnalysisExecutionGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ProjectSampleJoinRepository psjRepository;

	@Autowired
	private SampleSequenceFileJoinRepository ssfjRepository;

	private Path sequenceFilePath1;
	private Path sequenceFilePath2;
	private Path sequenceFilePath2b;

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

		sampleConversionService = new UploadSampleConversionServiceGalaxy(
				projectRepository, psjRepository, ssfjRepository);

		Path sequenceFilePathReal1 = Paths
				.get(AnalysisExecutionGalaxyITService.class.getResource(
						"testData1.fastq").toURI());

		sequenceFilePath1 = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath1);
		Files.copy(sequenceFilePathReal1, sequenceFilePath1);

		Path sequenceFilePathReal2 = Paths
				.get(AnalysisExecutionGalaxyITService.class.getResource(
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
	@WithMockUser(username = "aaron", roles = "ADMIN")
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
}
