package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartSampleRequest;
import ca.corefacility.bioinformatics.irida.ria.web.sessionAttrs.Cart;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Component
public class UICartService {
	private final Cart cart;
	private final ProjectService projectService;
	private final SampleService sampleService;

	@Autowired
	public UICartService(Cart cart, ProjectService projectService, SampleService sampleService) {
		this.cart = cart;
		this.projectService = projectService;
		this.sampleService = sampleService;
	}

	public int addSamplesToCart(AddToCartRequest request) {
		Project project = projectService.read(request.getProjectId());
		List<Sample> samples = (List<Sample>) sampleService.readMultiple(request.getSamples().stream().map(CartSampleRequest::getId).collect(
				Collectors.toUnmodifiableList()));
		return cart.add(project, samples);
	}

	public int getNumberOfSamplesInCart() {
		return cart.getNumberOfSamplesInCart();
	}
}
