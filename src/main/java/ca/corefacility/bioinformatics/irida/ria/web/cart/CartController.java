package ca.corefacility.bioinformatics.irida.ria.web.cart;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.cart.components.Cart;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.AddToCartResponse;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartRequestSample;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;

/**
 * Controller managing interactions with the selected sequences
 * 
 *
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

	@Autowired
	public CartController(SampleService sampleService, UserService userService, ProjectService projectService,
			SequencingObjectService sequencingObjectService, Cart cart) {
		this.sampleService = sampleService;
		this.projectService = projectService;
		this.userService = userService;
		this.sequencingObjectService = sequencingObjectService;
		this.cart = cart;
	}

	/**
	 * Get a modal dialog in order to export sample files to Galaxy
	 * @param model
	 *            The model to add attributes to for the template
	 * @param principal
	 *            A reference to the logged in user.
	 * @param projectId
	 *            The {@link Project} ID
	 * @return the name of the galaxy export modal dialog page
	 */
	@RequestMapping(value = "/template/galaxy/project/{projectId}", produces = MediaType.TEXT_HTML_VALUE)
	public String getGalaxyModal(Model model, Principal principal,@PathVariable Long projectId ) {
		model.addAttribute("email", userService.getUserByUsername(principal.getName()).getEmail());
		model.addAttribute("name", projectService.read(projectId).getName() + "-" + principal.getName());
		String orgName = projectService.read(projectId).getOrganism() + "-" + principal.getName();
		model.addAttribute("orgName", orgName);
		return "templates/galaxy.tmpl";
	}

	/**
	 * Get a Json representation of what's in the cart.
	 * <p>
	 * Format:
	 * {@code
	 * 'projects' : [ { 'id': '5', 'label': 'project', 'samples': [ { 'id': '6',
	 * 'label': 'a sample' } ] } ]
	 * }
	 *
	 * @return a {@code Map<String,Object>} containing the cart information.
	 */
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> getCartMap() {
		List<Map<String, Object>> projects = getProjectsAsList();
		return ImmutableMap.of("projects", projects);
	}

	/**
	 * Get a Json representation of what's in the cart for export to Galaxy.
	 * <p>
	 * Format:
	 * {@code
	 * 'projects' : [ { 'id': '5', 'label': 'project', 'samples': [ { 'id': '6',
	 * 'label': 'a sample', 'sequenceFiles': [ { 'selfRef' :
	 * 'http://localhost/projects/1/samples/1/sequenceFiles/1' } ] } ] } ]
	 * }
	 *
	 * @return a {@code Map<String,Object>} containing the cart information.
	 */
	@RequestMapping(value = "/galaxy-export", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> getCartMapForGalaxy() {
		List<Map<String, Object>> projects = getProjectsAsListForGalaxy();
		return ImmutableMap.of("projects", projects);
	}

	/**
	 * Clear the cart
	 *
	 * @return Success message
	 */
	@RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> clearCart() {
		cart.empty();
		return ImmutableMap.of("success", true);
	}

	/**
	 * Get the cart object. This method should only be accessed
	 * programmatically.
	 *
	 * @return The cart map
	 */
	public Map<Project, Set<Sample>> getSelected() {
		/*
		Inflating the whole cart here.  This is going to be a serious performance hit!
		 */
		Map<Long, Set<Long>> projects = cart.get();
		Map<Project, Set<Sample>> result = new HashMap<>();

		for (Long id : projects.keySet()) {
			Project project = projectService.read(id);
			Set<Sample> samples = (Set<Sample>) sampleService.readMultiple(projects.get(id));
			result.put(project, samples);
		}
		return result;
	}

	/**
	 * Set the cart object programmatically. Used mostly for testing.
	 *
	 * @param selected
	 *            A {@code Map<Project,Set<Sample>>} of selected samples
	 * @param locale {@link Locale}
	 */
	public void addSelected(Map<Project, Set<Sample>> selected, Locale locale) {
		// this.selected = selected;
		for (Project project : selected.keySet()) {
			Set<CartRequestSample> cartRequestSamples = selected.get(project)
					.stream()
					.map(s -> new CartRequestSample(s.getId(), s.getLabel()))
					.collect(Collectors.toSet());
			cart.addProjectSamplesToCart(new AddToCartRequest(project.getId(), cartRequestSamples), locale);
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
		Set<CartRequestSample> samples = sampleIds.stream()
				.map(id -> {
					ProjectSampleJoin join = sampleService.getSampleForProject(project, id);
					Sample sample = join.getObject();
					return new CartRequestSample(sample.getId(), sample.getSampleName());
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
	 * Delete a {@link Sample} from the cart from a given {@link Project}
	 *
	 * @param projectId
	 *            The {@link Project} ID
	 * @param sampleIds
	 *            The {@link Sample} ID
	 */
	@RequestMapping(value = "/project/{projectId}/samples", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public void removeProjectSamples(@PathVariable Long projectId, @RequestBody Set<Long> sampleIds, HttpServletResponse response) {
		cart.removeProjectSamples(projectId, sampleIds);
		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Remove a single sample from the cart
	 *
	 * @param projectId
	 *            The project id of the sample
	 * @param sampleId
	 *            the id of the sample
	 */
	@RequestMapping(value = "/project/{projectId}/samples/{sampleId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void removeProjectSample(@PathVariable Long projectId, @PathVariable Long sampleId, HttpServletResponse response) {
		cart.removeProjectSamples(projectId, ImmutableSet.of(sampleId));
		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Add an entire {@link Project} to the cart
	 *
	 * @param projectId
	 *            The ID of the {@link Project}
	 * @param locale {@link Locale}
	 * @return a map stating success
	 */
	@RequestMapping(value = "/project/{projectId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> addProject(@PathVariable Long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		Set<CartRequestSample> samples = samplesForProject.stream()
				.map(j -> new CartRequestSample(j.getId(), j.getLabel()))
				.collect(Collectors.toSet());
		cart.addProjectSamplesToCart(new AddToCartRequest(projectId, samples), locale);
		return ImmutableMap.of("success", true);
	}

	/**
	 * Delete an entire project from the cart
	 *
	 * @param projectId
	 *            The ID of the {@link Project} to delete
	 * @return a map stating success
	 */
	@RequestMapping(value = "/project/{projectId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, Object> removeProject(@PathVariable Long projectId) {
		cart.removeProject(projectId);
		return ImmutableMap.of("success", true);
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
		Map<Long, Set<Long>> currentCart = cart.get();
		List<Map<String, Object>> projectList = new ArrayList<>();
		for (Long id : currentCart.keySet()) {
			Project p = projectService.read(id);
			Set<Sample> selectedSamplesForProject = currentCart.get(id)
					.stream()
					.map(sampleService::read)
					.collect(Collectors.toSet());
			List<Map<String, Object>> samples = getSamplesAsList(selectedSamplesForProject);

			Map<String, Object> projectMap = ImmutableMap.of("id", p.getId(), "label", p.getLabel(), "samples",
					samples);
			projectList.add(projectMap);
		}

		return projectList;
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
		Map<Long, Set<Long>> currentCart = cart.get();
		List<Map<String, Object>> projectList = new ArrayList<>();
		for (Long id : currentCart.keySet()) {
			Set<Sample> selectedSamplesForProject = currentCart.get(id)
					.stream()
					.map(sampleService::read)
					.collect(Collectors.toSet());
			List<Map<String, Object>> samples = getSamplesAsListForGalaxy(selectedSamplesForProject, id);
			Project p = projectService.read(id);
			Map<String, Object> projectMap = ImmutableMap.of("id", p.getId(), "label", p.getLabel(), "samples",
					samples);
			projectList.add(projectMap);
		}

		return projectList;
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

}
