package ca.corefacility.bioinformatics.irida.ria.web.cart.components;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.*;
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
	private Map<Long, Map<Long, CartSample>> cart = new HashMap<>();

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
		Project project = projectService.read(addToCartRequest.getProjectId());
		AddToCartResponse response = new AddToCartResponse();
		Map<Long, CartSample> sampleIdsInCart = cart.getOrDefault(addToCartRequest.getProjectId(), new HashMap<>());
		int added = 0;
		List<String> duplicates = new ArrayList<>();
		List<String> existing = new ArrayList<>();

 		for (CartSampleRequest sample : addToCartRequest.getSamples()) {
			/*
			First lets see if the id is here, t
			 */
			if (currentSampleIds.contains(sample.getId())) {
				existing.add(sample.getLabel());
			} else if (currentSampleLabels.contains(sample.getLabel())) {
				duplicates.add(sample.getLabel());
			} else {
				sampleIdsInCart.put(sample.getId(), new CartSample(project, sample));
				currentSampleLabels.add(sample.getLabel());
				currentSampleIds.add(sample.getId());
				added++;
			}
		}

		/*
		Get a count of how many samples where added to the cart
		 */
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
			response.setExisting(messageSource.getMessage("cart.in-cart", new Object[] {existing.get(0)}, locale));
		} else if (existing.size() > 1) {
			response.setExisting(
					messageSource.getMessage("cart.in-cart-multiple", new Object[] { existing.size() }, locale));
		}

		response.setCount(this.currentSampleLabels.size());

		cart.put(addToCartRequest.getProjectId(), sampleIdsInCart);
		return response;
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
	public Map<Long, Map<Long, CartSample>> get() {
		return cart;
	}

	/**
	 * Remove all {@link Sample}s / {@link Project}s from the cart.
	 */
	public void empty() {
		this.cart.clear();
		this.currentSampleIds.clear();
		this.currentSampleLabels.clear();
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
	 * Get a set of all {@link Project} identifiers in the cart.
	 *
	 * @return {@link Set} of {@link Long}
	 */
	public Set<Long> getProjectIdsInCart() {
		return cart.keySet();
	}

	/**
	 * Get a {@link List} of {@link CartSample} belonging to a specific project.
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link CartSample} for a specific {@link Project}
	 */
	public List<CartSample> getCartSamplesForProject(Long projectId) {
		if (cart.containsKey(projectId)) {
			return new ArrayList<>(cart.get(projectId).values());
		}
		return new ArrayList<>();
	}

	/**
	 * Remove a single {@link Sample} from the cart.
	 *
	 * @param removeSampleRequest {@link RemoveSampleRequest} contains information about the sample to be removed.
	 * @return {@link RemoveSampleResponse} contains the state the UI needs to update to.
	 */
	public RemoveSampleResponse removeSampleFromCart(RemoveSampleRequest removeSampleRequest) {
		Map<Long, CartSample> cartProject = cart.get(removeSampleRequest.getProjectId());
		CartSample sample = cartProject.get(removeSampleRequest.getSampleId());
		currentSampleIds.remove(removeSampleRequest.getSampleId());
		currentSampleLabels.remove(sample.getLabel());
		cartProject.remove(removeSampleRequest.getSampleId());
		return new RemoveSampleResponse(this.getNumberOfSamples());
	}

	/**
	 * Remove all {@link Sample}s from a specific {@link Project} from the cart.
	 *
	 * @param id {@link Long} identifier for a {@link Project} in the cart.
	 * @return {@link RemoveSampleResponse} contains the state the UI needs to update to.
	 */
	public RemoveSampleResponse removeProjectFromCart(Long id) {
		Map<Long, CartSample> sampleMap = cart.get(id);
		currentSampleIds.removeAll(sampleMap.keySet());
		currentSampleLabels.removeAll(sampleMap.values()
				.stream()
				.map(CartSample::getLabel)
				.collect(Collectors.toSet()));
		cart.remove(id);
		return new RemoveSampleResponse(this.getNumberOfSamples());
	}
}
