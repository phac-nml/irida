package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CartSampleModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.cart.CartProjectModel;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartResponse;
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
	private final MessageSource messageSource;

	@Autowired
	public UICartService(Cart cart, ProjectService projectService, SampleService sampleService,
			MessageSource messageSource) {
		this.cart = cart;
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.messageSource = messageSource;
	}

	/**
	 * Add samples from a project to the cart.
	 *
	 * @param request Information about the project and samples to add to the cart
	 * @param locale  Current users locale
	 * @return {@link AddToCartResponse} contain information about what was added to the cart
	 */
	public AddToCartResponse addSamplesToCart(AddToCartRequest request, Locale locale) {
		Project project = projectService.read(request.getProjectId());
		List<Sample> samples = (List<Sample>) sampleService.readMultiple(request.getSampleIds());
		Set<String> existingSampleNames = cart.getSampleNamesInCart();

		// Modify the cart here so we can properly return the UI.

		List<Sample> duplicateNames = new ArrayList<>();
		List<Sample> existsInCart = new ArrayList<>();
		List<Sample> newToCart = new ArrayList<>();
		for (Sample sample : samples) {
			// Check to see if sample is already in the cart
			if (cart.containsKey(sample.getId())) {
				existsInCart.add(sample);
			} else if (existingSampleNames.contains(sample.getLabel())) {
				duplicateNames.add(sample);
			} else {
				newToCart.add(sample);
			}
		}

		// Update the cart
		if (newToCart.size() > 0) {
			newToCart.forEach(sample -> cart.put(sample.getId(), project.getId()));
		}

		AddToCartResponse response = new AddToCartResponse();
		response.setCount(cart.size());

		// Set UI messages
		if (newToCart.size() == 1) {
			response.setAdded(messageSource.getMessage("server.cart.one-sample-added",
					new Object[] { newToCart.get(0).getLabel() }, locale));
		} else if (newToCart.size() > 1) {
			response.setAdded(messageSource.getMessage("server.cart.many-samples-added",
					new Object[] { newToCart.size(), project.getLabel() }, locale));
		}

		if (duplicateNames.size() > 0) {
			String duplicates = duplicateNames.stream().map(Sample::getLabel).collect(Collectors.joining(", "));
			response.setDuplicate(
					messageSource.getMessage("server.cart.excluded", new Object[] { duplicates }, locale));
		}

		if (existsInCart.size() == 1) {
			response.setExisting(messageSource.getMessage("server.cart.in-cart", new Object[]{existsInCart.get(0).getLabel()}, locale));
		} else if (existsInCart.size() > 1) {
			response.setExisting(messageSource.getMessage("server.cart.in-cart-multiple", new Object[]{existsInCart.size()}, locale));
		}

		return response;
	}

	/**
	 * Get the number of samples in the cart
	 *
	 * @return number of total samples in the cart
	 */
	public int getNumberOfSamplesInCart() {
		return cart.size();
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
	 * @param request Information about the sample o remove from the cart
	 * @return number of total samples in the cart
	 */
	public int removeSample(Long sampleId) {
		cart.remove(sampleId);
		return cart.size();
	}

	/**
	 * Remove all samples from a specific project from the cart.
	 *
	 * @param id identifier for the project to remove from the cart.
	 * @return number of total samples in the cart
	 */
	public int removeProject(Long id) {
		cart.entrySet()
				.removeIf(entry -> entry.getValue()
						.equals(id));
		return cart.size();
	}

	/**
	 * Get a set of  identifiers for {@link Project}s in the cart
	 *
	 * @return {@link Set} of {@link Project} identifiers
	 */
	public Set<Long> getProjectIdsInCart() {
		return cart.getProjectsIdsInCart();
	}

	/**
	 * Get a list of sample in the cart belonging to a list of projects
	 *
	 * @param ids List of identifiers for project to get the samples for.
	 * @return {@link List} of {@link CartProjectModel}s containing project and sample information for items in the cart.
	 */
	public List<CartProjectModel> getSamplesForProjects(List<Long> ids) {
		List<Project> projects = (List<Project>) projectService.readMultiple(ids);
		List<CartProjectModel> cartProjectModels = new ArrayList<>();

		for (Project project : projects) {
			CartProjectModel cartProjectModel = new CartProjectModel(project.getId(), project.getLabel());
			List<Long> sampleIds = cart.entrySet()
					.stream()
					.filter(entry -> ids.contains(entry.getValue()))
					.map(Map.Entry::getKey)
					.collect(Collectors.toList());

			List<CartSampleModel> samples = new ArrayList<>();
			for (Sample sample : sampleService.readMultiple(sampleIds)) {
				CartSampleModel cartSampleModel = new CartSampleModel(sample);
				samples.add(cartSampleModel);
			}
			cartProjectModel.setSamples(samples);
			cartProjectModels.add(cartProjectModel);
		}
		return cartProjectModels;
	}

	/**
	 * Get the entire cart flushed out into {@link Project}s with their {@link Sample}s
	 *
	 * @return All projects and samples in the part
	 */
	public Map<Project, List<Sample>> getFullCart() {
		Map<Project, List<Sample>> response = new HashMap<>();

		// Get unique project ids;
		cart.values().stream().distinct().forEach(projectId -> {
			Project project = projectService.read(projectId);
			List<Long> sampleIds = new ArrayList<>();
			cart.forEach((key, value) -> {
				if (value.equals(projectId)) {
					sampleIds.add(key);
				}
			});
			response.put(project, (List<Sample>) sampleService.readMultiple(sampleIds));
		});


		return response;
	}
}
