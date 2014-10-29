package ca.corefacility.bioinformatics.irida.ria.unit.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.components.ProjectSamplesCart;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

/**
 * Unit Test for {@link ProjectSamplesCart}
 */
public class ProjectSamplesCartTest {
	private ProjectSamplesCart cart;
	private SampleService sampleService;
	private SequenceFileService sequenceFileService;

	@Before
	public void setUp() {
		sampleService = mock(SampleService.class);
		sequenceFileService = mock(SequenceFileService.class);
		cart = new ProjectSamplesCart(sampleService, sequenceFileService);
	}

	@Test
	public void testAddRemoveSampleToCart() {
		Long projectId1 = 1L;
		Long projectId2 = 2L;
		Sample sample = TestDataFactory.constructSample();

		when(sampleService.read(anyLong())).thenReturn(sample);
		when(sequenceFileService.getSequenceFilesForSample(sample))
				.thenReturn(new ArrayList<>());
		List<Long> project1Samples = ImmutableList.of(1L, 2L, 3L, 4L);
		List<Long> project2Samples = ImmutableList.of(5L, 6L, 7L, 8L);

		// Add samples to the first project
		for (int i = 0; i < project1Samples.size(); i++) {
			assertEquals(i+1, cart.addSampleToCart(projectId1, project1Samples.get(i)));
		}

		// Add samples to the second project
		for (int i = 0; i < project2Samples.size(); i++) {
			assertEquals(i+1, cart.addSampleToCart(projectId2, project2Samples.get(i)));
		}

		for (Long id : cart.getSelectedSamples(projectId1)) {
			assertTrue(project1Samples.contains(id));
			assertFalse(project2Samples.contains(id));
		}

		for (Long id : cart.getSelectedSamples(projectId2)) {
			assertTrue(project2Samples.contains(id));
			assertFalse(project1Samples.contains(id));
		}

		// Remove all the samples from the first cart
		for (int i = 0; i < project1Samples.size(); i++) {
			assertEquals(project1Samples.size() - (i + 1),
					cart.removeSampleFromCart(projectId1, project1Samples.get(i)));
		}
		assertEquals(0, cart.getSelectedSamples(projectId1).size());
		// Check to make sure none of the samples exist
		for (Long id : cart.getSelectedSamples(projectId1)) {
			assertFalse(project1Samples.contains(id));
		}

		// Ensure samples still exist for project2
		assertEquals(project2Samples.size(), cart.getSelectedSamples(projectId2).size());
	}
}
