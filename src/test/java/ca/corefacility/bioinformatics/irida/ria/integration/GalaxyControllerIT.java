package ca.corefacility.bioinformatics.irida.ria.integration;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpSession;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.GalaxyController;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.upload.galaxy.GalaxyUploadService;

import com.google.common.collect.ImmutableList;

/**
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
public class GalaxyControllerIT {
	private static final Logger logger = LoggerFactory.getLogger(GalaxyControllerIT.class);

	private GalaxyController controller;
	private MessageSource messageSource;
	private GalaxyUploadService galaxyUploadService;
	private SampleService sampleService;

	@Before
	public void setUp() {
		messageSource = mock(MessageSource.class);
		galaxyUploadService = mock(GalaxyUploadService.class);
		sampleService = mock(SampleService.class);
		controller = new GalaxyController(messageSource, galaxyUploadService, sampleService);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPostUploadSampleToGalaxy() {
		Sample sample = TestDataFactory.constructSample();
		List<Sample> samples = ImmutableList.of(sample);
		UploadWorker worker = TestDataFactory.constructUploadWorker();
		MockHttpSession session = new MockHttpSession();

		when(sampleService.readMultiple(ImmutableList.of(sample.getId()))).thenReturn(samples);
		String accountEmail = "test@gmail.com";
		String accountUsername = "Test";
		when(galaxyUploadService.performUploadSelectedSamples(anySet(), any(GalaxyProjectName.class), any(
				GalaxyAccountEmail.class)))
				.thenReturn(worker);

		Map<String, Object> result = controller
				.upload(accountEmail, accountUsername, ImmutableList.of(sample.getId()), session,
						Locale.US);
		assertTrue(result.containsKey("result"));
		assertEquals("success", result.get("result"));
		assertTrue(result.containsKey("msg"));
	}
}
