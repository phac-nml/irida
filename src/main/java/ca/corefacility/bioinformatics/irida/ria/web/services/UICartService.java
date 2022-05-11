package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CartSampleModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.cart.CartProjectModel;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartProjectSample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartSamplesByUserPermissions;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartUpdateResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.notification.ErrorNotification;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.notification.Notification;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.notification.SuccessNotification;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.notification.WarnNotification;
import ca.corefacility.bioinformatics.irida.ria.web.sessionAttrs.Cart;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Service for handling all aspects of interaction with the Cart.
 */
@Component
public class UICartService {
	private final Cart cart;
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final UpdateSamplePermission updateSamplePermission;
	private final MessageSource messageSource;

	@Autowired
	public UICartService(Cart cart, ProjectService projectService, SampleService sampleService,
			UpdateSamplePermission updateSamplePermission, MessageSource messageSource) {
		this.cart = cart;
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.updateSamplePermission = updateSamplePermission;
		this.messageSource = messageSource;
	}

	/**
	 * Add samples from a project to the cart.
	 *
	 * @param request Information about the project and samples to add to the cart
	 * @param locale  Current users locale
	 * @return {@link CartUpdateResponse} contain information about what was added to the cart
	 */
	public CartUpdateResponse addSamplesToCart(AddToCartRequest request, Locale locale) {
		Project project = projectService.read(request.getProjectId());
		List<Sample> samples = (List<Sample>) sampleService.readMultiple(request.getSampleIds());
		Set<String> existingSampleNames = cart.getSampleNamesInCart();

		// Modify the cart here, so we can properly return the UI.

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
			newToCart.forEach(sample -> cart.addSample(sample, project.getId()));
		}

		CartUpdateResponse response = new CartUpdateResponse();
		response.setCount(cart.size());

		// Set UI messages
		if (newToCart.size() == 1) {
			Notification notification = new SuccessNotification(messageSource.getMessage("server.cart.one-sample-added",
					new Object[] { newToCart.get(0).getLabel() }, locale));
			response.addNotification(notification);
		} else if (newToCart.size() > 1) {
			Notification notification = new SuccessNotification(
					messageSource.getMessage("server.cart.many-samples-added",
							new Object[] { newToCart.size(), project.getLabel() }, locale));
			response.addNotification(notification);
		}

		if (duplicateNames.size() > 0) {
			String duplicates = duplicateNames.stream()
					.map(Sample::getLabel)
					.collect(Collectors.joining(", "));
			Notification notification = new ErrorNotification(
					messageSource.getMessage("server.cart.excluded", new Object[] { duplicates }, locale));
			response.addNotification(notification);
		}

		if (existsInCart.size() == 1) {
			Notification notification = new WarnNotification(
					messageSource.getMessage("server.cart.in-cart", new Object[] { existsInCart.get(0).getLabel() },
							locale));
			response.addNotification(notification);
		} else if (existsInCart.size() > 1) {
			Notification notification = new WarnNotification(
					messageSource.getMessage("server.cart.in-cart-multiple", new Object[] { existsInCart.size() },
							locale));
			response.addNotification(notification);
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
		cart.empty();
	}

	/**
	 * Remove a specific sample from the cart.
	 *
	 * @param sampleId identifier for the sample to remove from the cart
	 * @param locale   Current users locale
	 * @return number of total samples in the cart
	 */
	public CartUpdateResponse removeSample(Long sampleId, Locale locale) {
		CartUpdateResponse response = new CartUpdateResponse();
		Sample sample = sampleService.read(sampleId);
		if (cart.containsKey(sampleId)) {
			cart.removeSample(sampleId);
			response.addNotification(new SuccessNotification(
					messageSource.getMessage("server.cart.remove-sample", new Object[] { sample.getSampleName() },
							locale)));
		} else {
			response.addNotification(new ErrorNotification(
					messageSource.getMessage("server.cart.remove-sample.exception",
							new Object[] { sample.getSampleName() }, locale)));
		}
		response.setCount(cart.size());
		return response;
	}

	/**
	 * Remove all samples from a specific project from the cart.
	 *
	 * @param id     identifier for the project to remove from the cart.
	 * @param locale Current users locale
	 * @return number of total samples in the cart
	 */
	public CartUpdateResponse removeProject(Long id, Locale locale) {
		int count = cart.removeProject(id);
		Project project = projectService.read(id);
		Notification notification = new SuccessNotification(
				messageSource.getMessage("server.cart.remove-project", new Object[] { project.getLabel() }, locale));
		CartUpdateResponse response = new CartUpdateResponse();
		response.setCount(count);
		response.addNotification(notification);
		return response;
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
	 * @return {@link List} of {@link CartProjectModel}s containing project and sample information for items in the cart.
	 */
	public List<CartProjectModel> getSamplesForProjects() {
		List<Project> projects = (List<Project>) projectService.readMultiple(getProjectIdsInCart());
		List<CartProjectModel> models = new ArrayList<>();

		for (Project project : projects) {
			CartProjectModel cartProjectModel = new CartProjectModel(project.getId(), project.getLabel());
			List<Long> sampleIds = cart.entrySet()
					.stream()
					.filter(entry -> project.getId()
							.equals(entry.getValue()))
					.map(Map.Entry::getKey)
					.collect(Collectors.toList());

			List<CartSampleModel> samples = sampleIds.stream()
					.map(id -> sampleService.getSampleForProject(project, id))
					.map(join -> new CartSampleModel(join.getObject(), join.isOwner()))
					.collect(Collectors.toList());
			cartProjectModel.setSamples(samples);

			models.add(cartProjectModel);
		}
		return models;
	}

	/**
	 * Get the entire cart flushed out into {@link Project}s with their {@link Sample}s
	 *
	 * @return All projects and samples in the part
	 */
	public Map<Project, List<Sample>> getFullCart() {
		Map<Project, List<Sample>> response = new HashMap<>();

		// Get unique project ids;
		cart.values()
				.stream()
				.distinct()
				.forEach(projectId -> {
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

	/**
	 * Determine if the cart is empty
	 *
	 * @return Boolean if the cart is empty
	 */
	public Boolean isCartEmpty() {
		return cart.isEmpty();
	}

	/**
	 * Determine if a sample is in the cart.
	 *
	 * @param sampleId identifier for a sample
	 * @return the project identifier if the sample is in the cart
	 */
	public Long isSampleInCart(Long sampleId) {
		return cart.isSampleInCart(sampleId);
	}

	/**
	 * Get a list of samples that are currently loaded into the cart that can be added to a new project
	 * This requires a special method because the user can only add samples to the new project
	 * that they already can modify.
	 *
	 * @return {@link CartSamplesByUserPermissions}
	 */
	public CartSamplesByUserPermissions getCartSamplesForNewProject() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		Map<Project, List<Sample>> cart = getFullCart();

		List<CartProjectSample> unlocked = new ArrayList<>();
		List<CartProjectSample> locked = new ArrayList<>();

		for (Map.Entry<Project, List<Sample>> s : cart.entrySet()) {
			for (Sample sample : s.getValue()) {
				if (updateSamplePermission.isAllowed(authentication, sample)) {
					unlocked.add(new CartProjectSample(sample, s.getKey().getId()));
				} else {
					locked.add(new CartProjectSample(sample, s.getKey().getId()));
				}
			}
		}
		return new CartSamplesByUserPermissions(locked, unlocked);
	}
}