package ca.corefacility.bioinformatics.irida.ria.web.cart.components;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartRequestSample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

@Component
@Scope("session")
public class Cart {
	/**
	 * Container for all the {@link Sample} identifiers in the cart organized by {@link Project} identifier
	 */
	private Map<Long, Set<Long>> cart = new HashMap<>();

	/**
	 * Cannot have the sample sample in the cart twice, this is here to ensure that the sample was not added via
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

	public CartResponse addProjectSamplesToCart(CartRequest cartRequest, Locale locale) {
		CartResponse response = new CartResponse();
		Set<Long> ids = cart.getOrDefault(cartRequest.getProjectId(), new HashSet<>());
		int added = 0;
		List<String> duplicates = new ArrayList<>();
		List<String> existing = new ArrayList<>();

		for (CartRequestSample sample : cartRequest.getSamples()) {
			/*
			First lets see if the id is here, t
			 */
			if (currentSampleIds.contains(sample.getId())) {
				existing.add(sample.getLabel());
			} else if (currentSampleLabels.contains(sample.getLabel())) {
				duplicates.add(sample.getLabel());
			} else {
				ids.add(sample.getId());
				currentSampleLabels.add(sample.getLabel());
				currentSampleIds.add(sample.getId());
				added++;
			}
		}

		/*
		Get a count of how many samples where added to the cart
		 */
		Project project = projectService.read(cartRequest.getProjectId());
		if (added == 1) {
			response.setAdded(
					messageSource.getMessage("cart.one-sample-added", new Object[] { project.getLabel() }, locale));
		} else if (added > 1) {
			response.setAdded(messageSource.getMessage("cart.many-samples-added",
					new Object[] { String.valueOf(added), project.getLabel() }, locale));
		}

		if (duplicates.size() > 0) {
			response.setAdded(
					messageSource.getMessage("cart.excluded", new Object[] { String.join(", ", duplicates) }, locale));
		}

		if (existing.size() == 1) {
			response.setExisting(messageSource.getMessage("cart.in-cart", new Object[] {}, locale));
		} else if (existing.size() > 1) {
			response.setExisting(
					messageSource.getMessage("cart.in-cart-multiple", new Object[] { existing.size() }, locale));
		}

		response.setCount(this.currentSampleLabels.size());

		cart.put(cartRequest.getProjectId(), ids);
		return response;
	}

	public void removeProjectSamples(Long projectId, Set<Long> currentSampleIds) {
		cart.get(projectId)
				.removeAll(currentSampleIds);
		if (cart.get(projectId)
				.size() == 0) {
			cart.remove(projectId);
		}
	}

	public void removeProject(Long projectId) {
		cart.remove(projectId);
	}

	public Map<Long, Set<Long>> get() {
		return cart;
	}

	public void empty() {
		this.cart.clear();
	}

	public int getNumberOfProjects() {
		return cart.keySet()
				.size();
	}

	public int getNumberOfSamples() {
		return cart.keySet()
				.stream()
				.map(i -> cart.get(i)
						.size())
				.reduce(0, (a, b) -> a + b);
	}
}
