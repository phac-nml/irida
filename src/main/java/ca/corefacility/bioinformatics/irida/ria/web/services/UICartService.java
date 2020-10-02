package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CartSample;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.cart.CartProject;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.RemoveSampleRequest;
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
		return cart.add(request.getProjectId(), (List<Long>) request.getSampleIds());
	}

	public int getNumberOfSamplesInCart() {
		return cart.getNumberOfSamplesInCart();
	}

	public Cart getCart() {
		return cart;
	}

	public void emptyCart() {
		cart.clear();
	}

	public int removeSample(RemoveSampleRequest request) {
		return cart.removeSample(request.getProjectId(), request.getSampleId());
	}

	public int removeProject(Long id) {
		return cart.removeProject(id);
	}

	public Set<Long> getProjectIdsInCart() {
		return cart.getProjectIdsInCart();
	}


	public List<CartProject> getSamplesForProjects(List<Long> ids) {
		List<Project> projects = (List<Project>) projectService.readMultiple(ids);
		List<CartProject> cartProjects = new ArrayList<>();
		for (Project project : projects) {
			CartProject cartProject = new CartProject(project.getId(), project.getLabel());
			List<CartSample> samples = new ArrayList<>();
			sampleService.readMultiple(cart.getCartSampleIdsForProject(project.getId())).forEach(sample -> {
				samples.add(new CartSample(sample));
			});
			cartProject.setSamples(samples);
			cartProjects.add(cartProject);
		}
		return cartProjects;
	}

	public Map<Project, List<Sample>> getFullCart() {
		Iterable<Project> projects = projectService.readMultiple(cart.getProjectIdsInCart());
		Map<Project, List<Sample>> results = new HashMap<>();
		projects.forEach(project -> {
			List<Sample> samples = (List<Sample>) sampleService.readMultiple(cart.getCartSampleIdsForProject(project.getId()));
			results.put(project, samples);
		});
		return results;
	}
}
