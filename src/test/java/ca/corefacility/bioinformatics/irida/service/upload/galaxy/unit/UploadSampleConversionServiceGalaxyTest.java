package ca.corefacility.bioinformatics.irida.service.upload.galaxy.unit;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.upload.galaxy.UploadSampleConversionServiceGalaxy;

public class UploadSampleConversionServiceGalaxyTest {

	private UploadSampleConversionServiceGalaxy uploadSampleConversionService;

	private static final String sampleName = "sample1";
	private Sample sample1;
	private SequenceFile sf1;

	private static final long PROJECT_ID = 1;

	@Mock
	private ProjectRepository projectRepository;
	@Mock
	private ProjectSampleJoinRepository psjRepository;
	@Mock
	private SampleSequenceFileJoinRepository ssfjRepository;
	@Mock
	private SequenceFileRepository sfRepository;
	@Mock
	private Path path1;
	@Mock
	private Project project;
	@Mock
	private Join<Project, Sample> projectSampleJoin;

	/**
	 * Setup variables for tests.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		uploadSampleConversionService = new UploadSampleConversionServiceGalaxy(
				projectRepository, psjRepository, ssfjRepository, sfRepository);

		sample1 = new Sample(sampleName);
		sf1 = new SequenceFile(path1);

		Join<Sample, SequenceFile> sampleSf1 = new SampleSequenceFileJoin(
				sample1, sf1);

		when(ssfjRepository.getFilesForSample(sample1)).thenReturn(
				Arrays.asList(sampleSf1));

		when(projectRepository.findOne(PROJECT_ID)).thenReturn(project);
		when(psjRepository.getSamplesForProject(project)).thenReturn(
				Arrays.asList(projectSampleJoin));
		when(projectSampleJoin.getObject()).thenReturn(sample1);
	}

	/**
	 * Tests conversion of a sample to an upload sample successfully.
	 */
	@Test
	public void testConvertToUploadSampleSuccess() {
		UploadSample uploadSample1 = uploadSampleConversionService
				.convertToUploadSample(sample1);

		assertEquals(sampleName, uploadSample1.getSampleName().getName());
		assertEquals(1, uploadSample1.getSampleFiles().size());
		assertEquals(path1, uploadSample1.getSampleFiles().get(0));
	}

	/**
	 * Tests conversion of a set of samples to a set of upload samples
	 * successfully.
	 */
	@Test
	public void testConvertToUploadSamplesSuccess() {
		Set<UploadSample> uploadSamples = uploadSampleConversionService
				.convertToUploadSamples(Sets.newHashSet(sample1));

		assertEquals(1, uploadSamples.size());

		UploadSample uploadSample1 = uploadSamples.iterator().next();
		assertEquals(sampleName, uploadSample1.getSampleName().getName());
		assertEquals(1, uploadSample1.getSampleFiles().size());
		assertEquals(path1, uploadSample1.getSampleFiles().get(0));
	}

	/**
	 * Tests successfully getting a set of samples from a project.
	 */
	@Test
	public void testGetUploadSamplesForProjectSuccess() {
		Set<UploadSample> uploadSamples = uploadSampleConversionService
				.getUploadSamplesForProject(PROJECT_ID);

		UploadSample uploadSample1 = uploadSamples.iterator().next();
		assertEquals(sampleName, uploadSample1.getSampleName().getName());
		assertEquals(1, uploadSample1.getSampleFiles().size());
		assertEquals(path1, uploadSample1.getSampleFiles().get(0));
	}
}
