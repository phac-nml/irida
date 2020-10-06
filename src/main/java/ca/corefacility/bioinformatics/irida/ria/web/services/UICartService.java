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

/**
 * Service for handling all aspects interaction with the Cart.
 */
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

	/**
	 * Add samples from a project to the cart.
	 *
	 * @param request Information about the project and samples to add to the cart
	 * @return number of total samples in the cart
	 */
	public int addSamplesToCart(AddToCartRequest request) {
		return cart.add(request.getProjectId(), (List<Long>) request.getSampleIds());
	}

	/**
	 * Get the number of samples in the cart
	 *
	 * @return number of total samples in the cart
	 */
	public int getNumberOfSamplesInCart() {
		return cart.getNumberOfSamplesInCart();
	}

	/**
	 * Remove all samples from the cart
	 */
	public void emptyCart() {
		cart.clear();
	}

	/**
	 * Remove a specific sample from the cart.
	 *
	 * @param request Information about the samplet o remove from the cart
	 * @return number of total samples in the cart
	 */
	public int removeSample(RemoveSampleRequest request) {
		return cart.removeSample(request.getProjectId(), request.getSampleId());
	}

	/**
	 * Remove all samples from a specific project from the cart.
	 *
	 * @param id identifier for the project to remove from the cart.
	 * @return number of total samples in the cart
	 */
	public int removeProject(Long id) {
		return cart.removeProject(id);
	}

	/**
	 * Get a set of  identifiers for {@link Project}s in the cart
	 *
	 * @return {@link Set} of {@link Project} identifiers
	 */
	public Set<Long> getProjectIdsInCart() {
		return cart.getProjectIdsInCart();
	}

	/**
	 * Get a list of sample in the cart belonging to a list of projects
	 *
	 * @param ids List of identifiers for project to get the samples for.
	 * @return {@link List} of {@link CartProject}s containing project and sample information for items in the cart.
	 */
	public List<CartProject> getSamplesForProjects(List<Long> ids) {
		List<Project> projects = (List<Project>) projectService.readMultiple(ids);
		List<CartProject> cartProjects = new ArrayList<>();
		for (Project project : projects) {
			CartProject cartProject = new CartProject(project.getId(), project.getLabel());
			List<CartSample> samples = new ArrayList<>();
			sampleService.readMultiple(cart.getCartSampleIdsForProject(project.getId()))
					.forEach(sample -> {
						samples.add(new CartSample(sample));
					});
			cartProject.setSamples(samples);
			cartProjects.add(cartProject);
		}
		return cartProjects;
	}

	/**
	 * Get the entire cart flushed out into {@link Project}s with their {@link Sample}s
	 *
	 * @return All proejcts and samples in the part
	 */
	public Map<Project, List<Sample>> getFullCart() {
		Iterable<Project> projects = projectService.readMultiple(cart.getProjectIdsInCart());
		Map<Project, List<Sample>> results = new HashMap<>();
		projects.forEach(project -> {
			List<Sample> samples = (List<Sample>) sampleService.readMultiple(
					cart.getCartSampleIdsForProject(project.getId()));
			results.put(project, samples);
		});
		return results;
	}
}
