package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartUpdateResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;
import ca.corefacility.bioinformatics.irida.ria.web.sessionAttrs.Cart;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;
import static org.mockito.Mockito.*;

public class UICartServiceTest {
	private UICartService service;
	private Cart cart;

	private final Long PROJECT_ID = 1L;
	private final Sample SAMPLE_1 = new Sample("SAMPLE_1");
	private final Sample SAMPLE_2 = new Sample("SAMPLE_2");
	private final Sample SAMPLE_3 = new Sample("SAMPLE_3");
	private final Project PROJECT_1 = new Project("PROJECT_1");
	private ProjectService projectService;
	private SampleService sampleService;

	@BeforeEach
	public void setUp() {
		cart = new Cart();
		projectService = Mockito.mock(ProjectService.class);
		sampleService = Mockito.mock(SampleService.class);
		MessageSource messageSource = Mockito.mock(MessageSource.class);
		UpdateSamplePermission updateSamplePermission = Mockito.mock(UpdateSamplePermission.class);
		service = new UICartService(cart, projectService, sampleService, updateSamplePermission, messageSource);

		SAMPLE_1.setId(1L);
		SAMPLE_2.setId(2L);
		SAMPLE_3.setId(3L);
		PROJECT_1.setId(PROJECT_ID);
		Mockito.when(projectService.read(PROJECT_ID)).thenReturn(PROJECT_1);
		Mockito.when(sampleService.readMultiple(ImmutableList.of(1L, 2L)))
				.thenReturn(ImmutableList.of(SAMPLE_1, SAMPLE_2));
		Mockito.when(sampleService.readMultiple(ImmutableList.of(2L, 3L)))
				.thenReturn(ImmutableList.of(SAMPLE_2, SAMPLE_3));
		Mockito.when(sampleService.read(1L)).thenReturn(SAMPLE_1);
	}

	@Test
	public void addSamplesToCartTest() {
		CartUpdateResponse response = service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(1L, 2L)),
				Locale.ENGLISH);
		assertEquals(2, response.getCount());
		assertEquals(2, cart.size());

		// Try adding the same sample again with a new one
		response = service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(2L, 3L)), Locale.ENGLISH);
		assertEquals(3, response.getCount());
		assertEquals(3, cart.size());
	}

	@Test
	public void getNumberOfSamplesInCart() {
		service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(1L, 2L)), Locale.ENGLISH);
		assertEquals(2, service.getNumberOfSamplesInCart());
	}

	@Test
	public void emptyCartTest() {
		service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(1L, 2L)), Locale.ENGLISH);
		assertEquals(2, service.getNumberOfSamplesInCart());
		service.emptyCart();
		assertEquals(0, service.getNumberOfSamplesInCart());

		/*
		NOTE: This is an extra test because of a previous failure when emptying the cart then trying
		to re-add a sample failed.
		 */
		service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(1L, 2L)), Locale.ENGLISH);
		assertEquals(2, service.getNumberOfSamplesInCart());
	}

	@Test
	public void removeSampleTest() {
		service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(1L, 2L)), Locale.ENGLISH);
		assertEquals(2, service.getNumberOfSamplesInCart());
		service.removeSample(1L, Locale.ENGLISH);
		assertEquals(1, service.getNumberOfSamplesInCart());

	}

	@Test
	public void testGetSampleIdsFromCart() {
		List<Long> expectedSampleIdsInCart = new ArrayList<Long>(Arrays.asList(1L, 2L));
		AddToCartRequest addToCartRequest = createAddRequest(PROJECT_ID, ImmutableList.of(1L, 2L));
		service.addSamplesToCart(addToCartRequest, Locale.ENGLISH);
		verify(projectService).read(addToCartRequest.getProjectId());
		verify(sampleService).readMultiple(addToCartRequest.getSampleIds());
		assertEquals(2, service.getNumberOfSamplesInCart());
		when(service.getCartSampleIds()).thenReturn(expectedSampleIdsInCart);
		when(projectService.readMultiple(service.getProjectIdsInCart()))
				.thenReturn(new ArrayList<Project>(Arrays.asList(PROJECT_1)));
		List<Long> cartSampleIds = service.getCartSampleIds();
		verify(projectService).readMultiple(service.getProjectIdsInCart());
		assertEquals(2, cartSampleIds.size(), "There should be 2 samples in the cart and their ids should be returned");
		assertEquals(expectedSampleIdsInCart, cartSampleIds, "The returned sample ids should match the list");
	}

	private AddToCartRequest createAddRequest(Long projectId, List<Long> sampleIds) {
		AddToCartRequest request = new AddToCartRequest();
		request.setSampleIds(sampleIds);
		request.setProjectId(projectId);
		return request;
	}

}
