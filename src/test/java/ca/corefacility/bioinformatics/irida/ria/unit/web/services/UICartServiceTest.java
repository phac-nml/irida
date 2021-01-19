package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartUpdateResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;
import ca.corefacility.bioinformatics.irida.ria.web.sessionAttrs.Cart;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

public class UICartServiceTest {
	private UICartService service;
	private Cart cart;

	private final Long PROJECT_ID = 1L;
	private final Sample SAMPLE_1 = new Sample("SAMPLE_1");
	private final Sample SAMPLE_2 = new Sample("SAMPLE_2");
	private final Sample SAMPLE_3 = new Sample("SAMPLE_3");
	private final Project PROJECT_1 = new Project("PROJECT_1");

	@Before
	public void setUp() {
		cart = new Cart();
		ProjectService projectService = Mockito.mock(ProjectService.class);
		SampleService sampleService = Mockito.mock(SampleService.class);
		MessageSource messageSource = Mockito.mock(MessageSource.class);
		service = new UICartService(cart, projectService, sampleService, messageSource);

		SAMPLE_1.setId(1L);
		SAMPLE_2.setId(2L);
		SAMPLE_3.setId(3L);
		PROJECT_1.setId(PROJECT_ID);
		Mockito.when(projectService.read(PROJECT_ID))
				.thenReturn(PROJECT_1);
		Mockito.when(sampleService.readMultiple(ImmutableList.of(1L, 2L))).thenReturn(ImmutableList.of(SAMPLE_1, SAMPLE_2));
		Mockito.when(sampleService.readMultiple(ImmutableList.of(2L, 3L))).thenReturn(ImmutableList.of(SAMPLE_2, SAMPLE_3));
		Mockito.when(sampleService.read(1L))
				.thenReturn(SAMPLE_1);
	}

	@Test
	public void addSamplesToCartTest() {
		CartUpdateResponse response = service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(1L, 2L)), Locale.ENGLISH);
		Assert.assertEquals(2, response.getCount());
		Assert.assertEquals(2, cart.size());

		// Try adding the same sample again with a new one
		response = service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(2L, 3L)), Locale.ENGLISH);
		Assert.assertEquals(3, response.getCount());
		Assert.assertEquals(3, cart.size());
	}

	@Test
	public void getNumberOfSamplesInCart() {
		service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(1L, 2L)), Locale.ENGLISH);
		Assert.assertEquals(2, service.getNumberOfSamplesInCart());
	}

	@Test
	public void emptyCartTest() {
		service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(1L, 2L)), Locale.ENGLISH);
		Assert.assertEquals(2, service.getNumberOfSamplesInCart());
		service.emptyCart();
		Assert.assertEquals(0, service.getNumberOfSamplesInCart());

		/*
		NOTE: This is an extra test because of a previous failure when emptying the cart then trying
		to re-add a sample failed.
		 */
		service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(1L, 2L)), Locale.ENGLISH);
		Assert.assertEquals(2, service.getNumberOfSamplesInCart());
	}

	@Test
	public void removeSampleTest() {
		service.addSamplesToCart(createAddRequest(PROJECT_ID, ImmutableList.of(1L, 2L)), Locale.ENGLISH);
		Assert.assertEquals(2, service.getNumberOfSamplesInCart());
		service.removeSample(1L, Locale.ENGLISH);
		Assert.assertEquals(1, service.getNumberOfSamplesInCart());

	}

	private AddToCartRequest createAddRequest(Long projectId, List<Long> sampleIds) {
		AddToCartRequest request = new AddToCartRequest();
		request.setSampleIds(sampleIds);
		request.setProjectId(projectId);
		return request;
	}
}
