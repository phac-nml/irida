package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.CartController;
import ca.corefacility.bioinformatics.irida.ria.web.cart.components.Cart;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.*;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CartControllerTest {
	SampleService sampleService;
	ProjectService projectService;
	MessageSource messageSource;

	CartController controller;
	Cart cart;

	private Long projectId;
	Set<Long> sampleIds;
	private Project project;
	private Set<Sample> samples;

	@Before
	public void setup() {
		sampleService = mock(SampleService.class);
		projectService = mock(ProjectService.class);
		messageSource = mock(MessageSource.class);
		cart = new Cart(projectService, messageSource);
		String iridaPipelinePluginStyle = "";

		controller = new CartController(sampleService, projectService, iridaPipelinePluginStyle, cart);

		testData();

		// Set up messages
		when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("A i18n string has been returned");
	}

	@Test
	public void testAddProjectSample() {
		AddToCartRequest request = new AddToCartRequest(1L,
				ImmutableSet.of(new CartSampleRequest(1L, "sample1"), new CartSampleRequest(2L, "sample2"),
						new CartSampleRequest(3L, "sample3")));
		AddToCartResponse response = controller.addSamplesToCart(request, Locale.ENGLISH);
		assertEquals("Should have 3 samples in the cart", 3, response.getCount());
		assertNull("Should be no duplicated", response.getDuplicate());
		assertNull("Should be no existing", response.getExisting());

		// Try adding a sample the second time.
		AddToCartRequest secondRequest = new AddToCartRequest(1L, ImmutableSet.of(new CartSampleRequest(1L, "sample1"), new CartSampleRequest(4L, "sample4")));
		AddToCartResponse secondsResponse = controller.addSamplesToCart(secondRequest, Locale.ENGLISH);
		assertNotNull("Should indicate that there was an existing sample added", secondsResponse.getExisting());
		assertEquals("Should now be 4 samples in the cart", 4, secondsResponse.getCount());
		assertNull("Should be no duplicated", response.getDuplicate());

		// Try adding a sample with a duplicate sample name, but different ID.
		AddToCartRequest thirdRequest = new AddToCartRequest(1L, ImmutableSet.of(new CartSampleRequest(5L, "sample1")));
		AddToCartResponse thirdResponse = controller.addSamplesToCart(thirdRequest, Locale.ENGLISH);
		assertNotNull("Should give a duplicate sample name message", thirdResponse);
		assertEquals("Should not have added the duplicate to the cart", 4, thirdResponse.getCount());
	}

	@Test
	public void testClearCart() {
		// Add some samples to the cart
		AddToCartRequest addToCartRequest = new AddToCartRequest(1L,
				ImmutableSet.of(new CartSampleRequest(1L, "sample2"), new CartSampleRequest(4L, "sample4"),
						new CartSampleRequest(5L, "sample1")));
		AddToCartResponse addToCartResponse = controller.addSamplesToCart(addToCartRequest, Locale.ENGLISH);
		assertEquals("Should be 3 samples in the cart", 3, addToCartResponse.getCount());

		// Test emptying the cart.
		controller.clearCart();
		assertEquals("The cart should be empty", 0, cart.get()
				.size());
	}

	@Test
	public void testRemoveSamplesFromCart() {
		// Add some samples to the cart
		AddToCartRequest addToCartRequest = new AddToCartRequest(1L,
				ImmutableSet.of(new CartSampleRequest(1L, "sample2"), new CartSampleRequest(4L, "sample4"),
						new CartSampleRequest(5L, "sample1")));
		AddToCartResponse addToCartResponse = controller.addSamplesToCart(addToCartRequest, Locale.ENGLISH);
		assertEquals("Should be 3 samples in the cart", 3, addToCartResponse.getCount());

		// Test removing a single sample from the cart
		RemoveSampleRequest removeSampleRequest = new RemoveSampleRequest(1L, 1L);
		RemoveSampleResponse removeSampleResponse = controller.removeSamplesFromCart(removeSampleRequest);
		assertEquals("After removing a sample there should be 2 left in cart.", 2, removeSampleResponse.getCount());

	}

	@Test
	public void testRemoveProjectFromCart() {
		// Add some samples to the cart
		AddToCartRequest addToCartRequest = new AddToCartRequest(1L,
				ImmutableSet.of(new CartSampleRequest(1L, "sample2")));
		controller.addSamplesToCart(addToCartRequest, Locale.ENGLISH);
		AddToCartRequest addToCartRequest2 = new AddToCartRequest(2L,
				ImmutableSet.of(new CartSampleRequest(11L, "sample4"), new CartSampleRequest(12L, "sample1")));
		AddToCartResponse addToCartResponse2 = controller.addSamplesToCart(addToCartRequest2, Locale.ENGLISH);
		assertEquals("Should be 3 samples in the cart", 3, addToCartResponse2.getCount());

		RemoveSampleResponse removeSampleResponse = controller.removeProjectFromCart(1L);
		assertEquals("There are 2 different projects in the cart, therefore there still should be 2 samples", 2,
				removeSampleResponse.getCount());

	}

	private void testData() {
		projectId = 1L;
		sampleIds = Sets.newHashSet(2L, 3L);

		project = new Project("project");
		project.setId(projectId);
		samples = new HashSet<>();
		when(projectService.read(projectId)).thenReturn(project);

		for (Long id : sampleIds) {
			Sample sample = new Sample("sample" + id);
			sample.setId(id);
			samples.add(sample);
			when(sampleService.getSampleForProject(project, id)).thenReturn(new ProjectSampleJoin(project,sample, true));
		}

		// Need a second project to test removing a project from the cart.
		Project project2 = new Project("Project2");
		project2.setId(2L);
		when(projectService.read(2L)).thenReturn(project2);

		Set<Long> sampleIds2 = Sets.newHashSet(11L, 12L, 13L, 14L);
		for (Long id : sampleIds2) {
			Sample sample = new Sample("sample" + id);
			sample.setId(id);
			samples.add(sample);
			when(sampleService.getSampleForProject(project2, id)).thenReturn(
					new ProjectSampleJoin(project2, sample, true));
		}

		final ArrayList<Long> ids = new ArrayList<>(sampleIds);

		when(messageSource.getMessage("cart.in-cart", new Object[] {}, Locale.ENGLISH)).thenReturn(
				"Sample already in cart");

		when(messageSource.getMessage("cart.excluded", new Object[] { "sample1" }, Locale.ENGLISH)).thenReturn(
				"Sample name is already in the cart, no duplicate names please.");

		when(sampleService.getSamplesInProject(project, ids)).thenReturn(new ArrayList<>(samples));
		ArrayList<Long> subIds = Lists.newArrayList(sampleIds.iterator().next());
		when(sampleService.getSamplesInProject(project, subIds)).thenReturn(samples.stream().filter(x -> Objects.equals(
				x.getId(), subIds.get(0))).collect(
				Collectors.toList()));
	}
}
