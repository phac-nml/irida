package ca.corefacility.bioinformatics.irida.ria.unit.web.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.samples.SamplesController;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Created by josh on 14-07-30.
 */
public class SamplesControllerTest {

	// Services
	private SamplesController controller;
	private SampleService sampleService;
	private SequenceFileService sequenceFileService;

	@Before
	public void setUp() {
		sampleService = mock(SampleService.class);
		sequenceFileService = mock(SequenceFileService.class);
		controller = new SamplesController(sampleService, sequenceFileService);
	}

	// ************************************************************************************************
	// PAGE REQUESTS
	// ************************************************************************************************

	@Test
	public void testGetSampleSpecificPage() {
		Model model = new ExtendedModelMap();
		Sample sample = TestDataFactory.constructSample();
		when(sampleService.read(sample.getId())).thenReturn(sample);
		String result = controller.getSampleSpecificPage(model, sample.getId());
		assertEquals("Returns the correct page namge", "samples/sample", result);
		assertTrue("Model contains the sample", model.containsAttribute("sample"));
	}

	// ************************************************************************************************
	// AJAX REQUESTS
	// ************************************************************************************************

	@Test
	public void testGetFilesForSample() throws IOException {
		Sample sample = TestDataFactory.constructSample();
		List<Join<Sample, SequenceFile>> joinList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Path path = Paths.get("/tmp/sequence-files/fake-file" + i + ".fast");
			SequenceFile file = new SequenceFile(path);
			file.setId(1L + i);
			joinList.add(new SampleSequenceFileJoin(sample, file));
		}
		when(sampleService.read(1L)).thenReturn(sample);
		when(sequenceFileService.getSequenceFilesForSample(sample)).thenReturn(joinList);
		List<Map<String, Object>> result = controller.getFilesForSample(1L);
		assertEquals("Should have the correct number of sequence file records.", joinList.size(), result.size());

		Map<String, Object> file1 = result.get(0);
		assertTrue("File has an id", file1.containsKey("id"));
		assertTrue("File has an name", file1.containsKey("name"));
		assertTrue("File has an created", file1.containsKey("created"));
	}
}
