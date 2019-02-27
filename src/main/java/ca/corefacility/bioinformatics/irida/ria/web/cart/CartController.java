package ca.corefacility.bioinformatics.irida.ria.web.cart;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.cart.components.Cart;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.*;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;

import com.google.common.collect.ImmutableMap;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Controller managing interactions with the selected sequences
 */
@Controller
@Scope("session")
@RequestMapping("/cart")
public class CartController {
	private Cart cart;

	private final SampleService sampleService;
	private final UserService userService;
	private final ProjectService projectService;
	private final SequencingObjectService sequencingObjectService;
	/*
	 * Additional variables
	 */
	private String iridaPipelinePluginStyle;

	@Autowired
	public CartController(SampleService sampleService, UserService userService, ProjectService projectService,
			SequencingObjectService sequencingObjectService,
			@Qualifier("iridaPipelinePluginStyle") String iridaPipelinePluginStyle, Cart cart) {
		this.sampleService = sampleService;
		this.projectService = projectService;
		this.userService = userService;
		this.sequencingObjectService = sequencingObjectService;
		this.iridaPipelinePluginStyle = iridaPipelinePluginStyle;
		this.cart = cart;
	}

	/**
	 * Get the dedicated page for the Cart
	 *
	 * @param model {@link Model}
	 * @return {@link String} path to the cart page template
	 */
	@RequestMapping(value = {"", "/*"}, produces = MediaType.TEXT_HTML_VALUE)
	public String getCartPage(Model model) {
		model.addAttribute("pipeline_plugin_style", iridaPipelinePluginStyle);
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
	 * Set the cart object programmatically. Used mostly for testing.
	 *
	 * @param selected
	 *            A {@code Map<Project,Set<Sample>>} of selected samples
	 * @param locale {@link Locale}
	 */
	public void addSelected(Map<Project, Set<Sample>> selected, Locale locale) {
		for (Project project : selected.keySet()) {
			Set<CartSampleRequest> cartSampleRequests = selected.get(project)
					.stream()
					.map(s -> new CartSampleRequest(s.getId(), s.getLabel()))
					.collect(Collectors.toSet());
			cart.addProjectSamplesToCart(new AddToCartRequest(project.getId(), cartSampleRequests), locale);
		}
	}

	/**
	 * Add a {@link Sample} to the cart from a given {@link Project}
	 *
	 * @param projectId The {@link Project} ID
	 * @param sampleIds The {@link Sample} id
	 * @param locale    Locale of the logged in user
	 * @return a map stating success
	 */
	@RequestMapping(value = "/add/samples", method = RequestMethod.POST)
	@ResponseBody
	public AddToCartResponse addProjectSample(@RequestParam Long projectId,
			@RequestParam(value = "sampleIds[]") Set<Long> sampleIds, Locale locale) {
		Project project = projectService.read(projectId);
		Set<CartSampleRequest> samples = sampleIds.stream()
				.map(id -> {
					ProjectSampleJoin join = sampleService.getSampleForProject(project, id);
					Sample sample = join.getObject();
					return new CartSampleRequest(sample.getId(), sample.getSampleName());
				})
				.collect(Collectors.toSet());
		AddToCartRequest addToCartRequest = new AddToCartRequest(projectId, samples);
		return this.cart.addProjectSamplesToCart(addToCartRequest, locale);
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
	 * Add an entire {@link Project} to the cart
	 *
	 * @param projectId
	 *            The ID of the {@link Project}
	 * @param locale {@link Locale}
	 */
	@RequestMapping(value = "/project/{projectId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void addProject(@PathVariable Long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		Set<CartSampleRequest> samples = samplesForProject.stream()
				.map(j -> new CartSampleRequest(j.getId(), j.getLabel()))
				.collect(Collectors.toSet());
		cart.addProjectSamplesToCart(new AddToCartRequest(projectId, samples), locale);
	}

	/**
	 * Get the {@link Sample}s in a {@link Project} with the given IDs
	 *
	 * @param project
	 *            The {@link Project} to get {@link Sample}s for
	 * @param sampleIds
	 *            the {@link Sample} ids
	 * @return A Set of {@link Sample}s
	 */
	private Set<Sample> loadSamplesForProject(Project project, Set<Long> sampleIds) {
		return new HashSet<>(sampleService.getSamplesInProject(project, new ArrayList<>(sampleIds)));

	}

	/**
	 * Get the {@link Project}s in the cart as a List for JSON serialization
	 *
	 * @return A List<Map<String,Object>> containing the relevant Project and
	 *         Sample information
	 */
	private List<Map<String, Object>> getProjectsAsList() {
//		Map<Long, Set<Long>> currentCart = cart.get();
//		List<Map<String, Object>> projectList = new ArrayList<>();
//		for (Long id : currentCart.keySet()) {
//			Project p = projectService.read(id);
//			Set<Sample> selectedSamplesForProject = currentCart.get(id)
//					.stream()
//					.map(sampleService::read)
//					.collect(Collectors.toSet());
//			List<Map<String, Object>> samples = getSamplesAsList(selectedSamplesForProject);
//
//			Map<String, Object> projectMap = ImmutableMap.of("id", p.getId(), "label", p.getLabel(), "samples",
//					samples);
//			projectList.add(projectMap);
//		}
//
//		return projectList;
		return null;
	}

	/**
	 * Get the set of given {@link Sample}s as a List for JSON serialization
	 *
	 * @param samples
	 *            The {@link Sample} set
	 * @return A List<Map<String,Object>> containing the relevant Sample
	 *         information
	 */
	private List<Map<String, Object>> getSamplesAsList(Set<Sample> samples) {
		List<Map<String, Object>> sampleList = new ArrayList<>();
		for (Sample s : samples) {
			Map<String, Object> sampleMap = ImmutableMap.of("id", s.getId(), "label", s.getLabel(), "createdDate", s.getCreatedDate());
			sampleList.add(sampleMap);
		}
		return sampleList;
	}

	/**
	 * Get the {@link Project}s in the cart as a List for JSON serialization for export to Galaxy.
	 *
	 * @return A List<Map<String,Object>> containing the relevant Project and
	 * Sample information
	 */
	private List<Map<String, Object>> getProjectsAsListForGalaxy() {
//		Map<Long, Set<Long>> currentCart = cart.get();
//		List<Map<String, Object>> projectList = new ArrayList<>();
//		for (Long id : currentCart.keySet()) {
//			Set<Sample> selectedSamplesForProject = currentCart.get(id)
//					.stream()
//					.map(sampleService::read)
//					.collect(Collectors.toSet());
//			List<Map<String, Object>> samples = getSamplesAsListForGalaxy(selectedSamplesForProject, id);
//			Project p = projectService.read(id);
//			Map<String, Object> projectMap = ImmutableMap.of("id", p.getId(), "label", p.getLabel(), "samples",
//					samples);
//			projectList.add(projectMap);
//		}
//
//		return projectList;
		return null;
	}

	/**
	 * Get the set of given {@link Sample}s as a List for JSON serialization for export to Galaxy.
	 *
	 * @param samples
	 *            The {@link Sample} set
	 * @return A List<Map<String,Object>> containing the relevant Sample
	 *         information
	 */
	private List<Map<String, Object>> getSamplesAsListForGalaxy(Set<Sample> samples, Long projectId) {
		List<Map<String, Object>> sampleList = new ArrayList<>();
		for (Sample s : samples) {
			String sampleHref = linkTo(
					methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, s.getId())).withSelfRel()
					.getHref();
			Map<String, Object> sampleMap = ImmutableMap.of("id", s.getId(), "label", s.getLabel(), "createdDate",
					s.getCreatedDate(), "sequenceFiles", getSequenceFileListForGalaxy(s), "href", sampleHref);
			sampleList.add(sampleMap);
		}
		return sampleList;

	}

	/**
	 * Get {@link SequenceFile}s as a List from a {@link Sample} for JSON serialization for export to Galaxy.
	 *
	 * @param sample The {@link Sample} set
	 * @return A List<Map<String,Object>> containing the relevant SequenceFile information
	 */
	private List<Map<String, Object>> getSequenceFileListForGalaxy(Sample sample) {
		Collection<SampleSequencingObjectJoin> sequencingObjectsForSample = sequencingObjectService.getSequencingObjectsForSample(
				sample);
		List<Map<String, Object>> sequenceFiles = new ArrayList<>();
		for (SampleSequencingObjectJoin join : sequencingObjectsForSample) {
			for (SequenceFile seq : join.getObject()
					.getFiles()) {
				String objectType = RESTSampleSequenceFilesController.objectLabels.get(join.getObject()
						.getClass());
				String seqFileLoc = linkTo(
						methodOn(RESTSampleSequenceFilesController.class).readSequenceFileForSequencingObject(
								sample.getId(), objectType, join.getObject()
										.getId(), seq.getId())).withSelfRel()
						.getHref();
				Map<String, Object> seqMap = ImmutableMap.of("selfRef", seqFileLoc);
				sequenceFiles.add(seqMap);
			}
		}
		return sequenceFiles;
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
	 * @param projectId {@link Long} identifier for a project
	 * @return {@link List} of {@link CartSample}s belonging to the {@link Project}
	 */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<CartSample> getCartSamplesForProject(@RequestParam Long projectId) {
		return cart.getCartSamplesForProject(projectId);
	}

}
