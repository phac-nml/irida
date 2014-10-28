package ca.corefacility.bioinformatics.irida.ria.unit.components;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.components.ProjectSamplesCart;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableSet;

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
		assertEquals(1, cart.addSampleToCart(1L));
		assertEquals(2, cart.addSampleToCart(2L));
		assertEquals(3, cart.addSampleToCart(3L));
		assertEquals(2, cart.removeSampleFromCart(1L));
		assertEquals("Removing a non-existing sample should not throw an error", 2, cart.removeSampleFromCart(1L));
		assertEquals("Adding the same ID twice does not increase the count", 2, cart.addSampleToCart(3L));
	}

	@Test
	public void testMarkSequenceFileAsActiveInactive() {
		Long sampleId = 1L;
		assertEquals("Should not be able to mark a file as inactive if the sample is not selected", 0,
				cart.markSequenceFileAsInactive(sampleId, 2L));
		cart.addSampleToCart(sampleId);
		assertEquals(1, cart.markSequenceFileAsInactive(sampleId, 2L));
		assertEquals(2, cart.markSequenceFileAsInactive(sampleId, 3L));
		assertEquals("Same count returned if trying to deactivate the same file", 2, cart.markSequenceFileAsInactive(sampleId, 3L));
		assertEquals(1, cart.markSequenceFileAsActive(sampleId, 3L));
		assertEquals("Same count returned if trying to activate an actived file", 1, cart.markSequenceFileAsActive(sampleId, 3L));
	}

	@Test
	public void testGetSampleIds() {
		Set<Long> ids = ImmutableSet.of(1L, 3L, 15L, 332L, 55L);
		ids.forEach(cart::addSampleToCart);
		assertEquals(ids, cart.getSelectedSampleIds());
	}

	@Test
	public void testCountAndEmpty() {
		Set<Long> ids = ImmutableSet.of(1L, 3L, 15L, 332L, 55L);
		ids.forEach(cart::addSampleToCart);
		assertEquals(ids.size(), cart.getSelectedCount());
		cart.empty();
		assertEquals(0, cart.getSelectedCount());
	}
}
