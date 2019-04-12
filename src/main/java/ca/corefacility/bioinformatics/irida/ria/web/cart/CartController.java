package ca.corefacility.bioinformatics.irida.ria.web.cart;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.components.Cart;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.*;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Controller managing interactions with the selected sequences
 */
@Controller
@Scope("session")
@RequestMapping("/cart")
public class CartController {
	private Cart cart;

	private final SampleService sampleService;
	private final ProjectService projectService;
	/*
	 * Additional variables
	 */
	private String iridaPipelinePluginStyle;

	@Autowired
	public CartController(SampleService sampleService, ProjectService projectService,
			@Qualifier("iridaPipelinePluginStyle") String iridaPipelinePluginStyle, Cart cart) {
		this.sampleService = sampleService;
		this.projectService = projectService;
		this.iridaPipelinePluginStyle = iridaPipelinePluginStyle;
		this.cart = cart;
	}

	/**
	 * Get the dedicated page for the Cart
	 *
	 * @param model {@link Model}
	 * @return {@link String} path to the cart page template
	 */
	@RequestMapping(value = { "", "/*" }, produces = MediaType.TEXT_HTML_VALUE)
	public String getCartPage(Model model,
			@RequestParam(required = false, name = "automatedProject")
					Long automatedProject) {
		model.addAttribute("pipeline_plugin_style", iridaPipelinePluginStyle);
		model.addAttribute("automatedProject", automatedProject);
		return "cart";
	}

	/**
	 * Remove all {@link Project}s and {@link Sample}s from the cart
	 */
	@RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void clearCart() {
		cart.empty();
	}

	/**
	 * Remove a single {@link Sample} from the cart.
	 *
	 * @param removeSampleRequest {@link RemoveSampleRequest} contains information about the sample to be removed.
	 * @return {@link RemoveSampleResponse} contains the state the UI needs to update to.
	 */
	@RequestMapping(value = "/sample", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RemoveSampleResponse removeSamplesFromCart(@RequestBody RemoveSampleRequest removeSampleRequest) {
		return cart.removeSampleFromCart(removeSampleRequest);
	}

	/**
	 * Remove all {@link Sample}s from a specific {@link Project} from the cart.
	 *
	 * @param id {@link Long} identifier for a {@link Project} in the cart.
	 * @return {@link RemoveSampleResponse} contains the state the UI needs to update to.
	 */
	@RequestMapping(value = "/project", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RemoveSampleResponse removeProjectFromCart(@RequestParam Long id) {
		return cart.removeProjectFromCart(id);
	}

	/**
	 * Get the cart object. This method should only be accessed
	 * programmatically.
	 *
	 * @return The cart map
	 */
	public Map<Project, List<Sample>> getSelected() {
		Map<Project, List<Sample>> hydrated = new HashMap<>();
		Map<Long, Map<Long, CartSample>> contents = cart.get();
		List<Project> projects = (List<Project>) projectService.readMultiple(contents.keySet());

		for (Project project : projects) {
			List<Sample> samples = (List<Sample>) sampleService.readMultiple(contents.get(project.getId())
					.keySet());
			hydrated.put(project, samples);
		}

		return hydrated;
	}

	/**
	 * Update add samples to cart for the new LineList page.
	 *
	 * @param addToCartRequest {@link AddToCartRequest} contains the {@link Project} identifier and list of {@link Sample} data to add to the cart
	 * @param locale      {@link Locale}
	 * @return {@link AddToCartResponse}
	 */
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public AddToCartResponse addSamplesToCart(@RequestBody AddToCartRequest addToCartRequest, Locale locale) {
		return this.cart.addProjectSamplesToCart(addToCartRequest, locale);
	}

	/**
	 * Get the number of projects contained in the cart.
	 *
	 * @return {@link Integer} number of projects in the cart.
	 */
	public int getNumberOfProjects() {
		return cart.getNumberOfProjects();
	}

	/**
	 * Get the number of samples contained in the cart.
	 *
	 * @return {@link Integer} number of samples in the cart.
	 */
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	@ResponseBody
	public int getNumberOfSamples() {
		return cart.getNumberOfSamples();
	}

	/**
	 * Get a {@link Set} of {@link Project} identifiers
	 *
	 * @return {@link Set} of {@link Long}
	 */
	@RequestMapping("/ids")
	@ResponseBody
	public Set<Long> getProjectIdsInCart() {
		return cart.getProjectIdsInCart();
	}

	/**
	 * Get {@link Sample}s in the cart for a specific {@link Project}
	 *
	 * @param projectId {@link List} identifiers for a projects
	 * @return {@link List} of {@link CartSample}s belonging to the {@link Project}
	 */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<CartSample> getCartSamplesForProject(@RequestParam List<Long> projectId) {
		List<CartSample> samples = new ArrayList<>();
		for (Long id : projectId) {
			samples.addAll(cart.getCartSamplesForProject(id));
		}
		return samples;
	}
}
