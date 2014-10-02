package ca.corefacility.bioinformatics.irida.service.upload.galaxy.unit;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.service.upload.galaxy.UploadSampleConversionServiceGalaxy;

public class UploadSampleConversionServiceGalaxyTest {
	
	private UploadSampleConversionServiceGalaxy uploadSampleConversionService;
		
	private static final String sampleName = "sample1";
	private Sample sample1;
	private SequenceFile sf1;
	
	@Mock private SampleSequenceFileJoinRepository ssfjRepository;
	@Mock private Path path1;
	
	/**
	 * Setup variables for tests.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		uploadSampleConversionService = new UploadSampleConversionServiceGalaxy(ssfjRepository);
		
		sample1 = new Sample(sampleName);
		sf1 = new SequenceFile(path1);
		
		Join<Sample, SequenceFile> sampleSf1 = new SampleSequenceFileJoin(sample1,sf1);
		
		when(ssfjRepository.getFilesForSample(sample1)).thenReturn(Arrays.asList(sampleSf1));
	}
	
	/**
	 * Tests conversion of a sample to an upload sample successfully.
	 */
	@Test
	public void testConvertToUploadSampleSuccess() {
		UploadSample uploadSample1 
			= uploadSampleConversionService.convertToUploadSample(sample1);
		
		assertEquals(sampleName, uploadSample1.getSampleName().getName());
		assertEquals(1, uploadSample1.getSampleFiles().size());
		assertEquals(path1, uploadSample1.getSampleFiles().get(0));
	}
}
