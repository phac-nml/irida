package ca.corefacility.bioinformatics.irida.ria.web.cart.components;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartResponse;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartRequestSample;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * "Cart" to represent samples that have been selected to perform a future action on (e.g. run a pipeline).
 */
@Component
@Scope("session")
public class Cart {
	/**
	 * Container for all the {@link Sample} identifiers in the cart organized by {@link Project} identifier
	 */
	private Map<Long, Set<Long>> cart = new HashMap<>();

	/**
	 * Cannot have the same sample in the cart twice, this is here to ensure that the sample was not added via
	 * another project.
	 */
	private Set<Long> currentSampleIds = new HashSet<>();

	/**
	 * Cannot have duplicate sample names (different samples can have the same name by coincidence).
	 */
	private Set<String> currentSampleLabels = new HashSet<>();

	private ProjectService projectService;
	private MessageSource messageSource;

	@Autowired
	public Cart(ProjectService projectService, MessageSource messageSource) {
		this.projectService = projectService;
		this.messageSource = messageSource;
	}

	/**
	 * Add {@link Sample} to the cart.
	 *
	 * @param addToCartRequest {@link AddToCartRequest} containing identifiers for {@link Sample}s to add.
	 * @param locale           {@link Locale}
	 * @return {@link AddToCartResponse} containing the result of the action.
	 */
	public AddToCartResponse addProjectSamplesToCart(AddToCartRequest addToCartRequest, Locale locale) {
		AddToCartResponse response = new AddToCartResponse();
		Set<Long> sampleIdsInCart = cart.getOrDefault(addToCartRequest.getProjectId(), new HashSet<>());
		int added = 0;
		List<String> duplicates = new ArrayList<>();
		List<String> existing = new ArrayList<>();

		for (CartRequestSample sample : addToCartRequest.getSamples()) {
			/*
			First lets see if the id is here, t
			 */
			if (currentSampleIds.contains(sample.getId())) {
				existing.add(sample.getLabel());
			} else if (currentSampleLabels.contains(sample.getLabel())) {
				duplicates.add(sample.getLabel());
			} else {
				sampleIdsInCart.add(sample.getId());
				currentSampleLabels.add(sample.getLabel());
				currentSampleIds.add(sample.getId());
				added++;
			}
		}

		/*
		Get a count of how many samples where added to the cart
		 */
		Project project = projectService.read(addToCartRequest.getProjectId());
		if (added == 1) {
			response.setAdded(
					messageSource.getMessage("cart.one-sample-added", new Object[] { project.getLabel() }, locale));
		} else if (added > 1) {
			response.setAdded(messageSource.getMessage("cart.many-samples-added",
					new Object[] { String.valueOf(added), project.getLabel() }, locale));
		}

		if (duplicates.size() > 0) {
			response.setDuplicate(
					messageSource.getMessage("cart.excluded", new Object[] { String.join(", ", duplicates) }, locale));
		}

		if (existing.size() == 1) {
			response.setExisting(messageSource.getMessage("cart.in-cart", new Object[] {}, locale));
		} else if (existing.size() > 1) {
			response.setExisting(
					messageSource.getMessage("cart.in-cart-multiple", new Object[] { existing.size() }, locale));
		}

		response.setCount(this.currentSampleLabels.size());

		cart.put(addToCartRequest.getProjectId(), sampleIdsInCart);
		return response;
	}

	/**
	 * Remove {@link Sample}s from the cart
	 * @param projectId {@link Long} identifier for the {@link Project} the {@link Sample}s belong to.
	 * @param currentSampleIds {@link Set} of {@link Long} identifiers for {@link Sample}s to remove from the cart.
	 */
	public void removeProjectSamples(Long projectId, Set<Long> currentSampleIds) {
		cart.get(projectId)
				.removeAll(currentSampleIds);
		if (cart.get(projectId)
				.size() == 0) {
			cart.remove(projectId);
		}
	}

	/**
	 * Remove all {@link Sample}s from a particular {@link Project}
	 * @param projectId {@link Long} identifier for a {@link Project} to remove.
	 */
	public void removeProject(Long projectId) {
		cart.remove(projectId);
	}

	/**
	 * Get a copy of the cart.
	 * @return {@link Map} of that contains {@link Long} {@link Project} identifiers as key
	 * and {@link Set} of {@link Long} {@link Sample} identifiers as value
	 */
	public Map<Long, Set<Long>> get() {
		return cart;
	}

	/**
	 * Remove all {@link Sample}s / {@link Project}s from the cart.
	 */
	public void empty() {
		this.cart.clear();
	}

	/**
	 * Get the number of {@link Project}s in the cart.
	 * @return {@link int}
	 */
	public int getNumberOfProjects() {
		return cart.keySet()
				.size();
	}

	/**
	 * Get the number of {@link Sample}s total in the cart.
	 * @return {@link int}
	 */
	public int getNumberOfSamples() {
		return cart.keySet()
				.stream()
				.mapToInt(i -> cart.get(i)
						.size())
				.sum();
	}

	/**
	 * Get the identifiers for samples in the cart
	 */
	public Set<Long> getSampleIdsInCart() {
		Set<Long> ids = new HashSet<>();
		cart.values()
				.forEach(ids::addAll);
		return ids;
	}
}
