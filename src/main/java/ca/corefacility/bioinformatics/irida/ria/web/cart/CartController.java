package ca.corefacility.bioinformatics.irida.ria.web.cart;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.cart.components.Cart;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartResponse;
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
 * 
 *
 */
@Controller
@Scope("session")
@RequestMapping("/cart")
public class CartController {
	private static final Logger logger = LoggerFactory.getLogger(CartController.class);
	private Map<Project, Set<Sample>> selected;

	private Cart cart;
	
	private final SampleService sampleService;
	private final UserService userService;
	private final ProjectService projectService;
	private final SequencingObjectService sequencingObjectService;
	private final MessageSource messageSource;

	@Autowired
	public CartController(SampleService sampleService, UserService userService, ProjectService projectService,
			SequencingObjectService sequencingObjectService, Cart cart, MessageSource messageSource) {
		this.sampleService = sampleService;
		this.projectService = projectService;
		this.userService = userService;
		this.sequencingObjectService = sequencingObjectService;
		this.messageSource = messageSource;
		this.cart = cart;
		selected = new HashMap<>();
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
		selected.clear();
		return ImmutableMap.of("success", true);
	}

	/**
	 * Get the cart object. This method should only be accessed
	 * programmatically.
	 * 
	 * @return The cart map
	 */
	public Map<Project, Set<Sample>> getSelected() {
		return selected;
	}

	/**
	 * Set the cart object programatically. Used mostly for testing.
	 * 
	 * @param selected
	 *            A {@code Map<Project,Set<Sample>>} of selected samples
	 */
	public void setSelected(Map<Project, Set<Sample>> selected) {
		this.selected = selected;
	}

	/**
	 * Add a {@link Sample} to the cart from a given {@link Project}
	 *
	 * @param projectId The {@link Project} ID
	 * @param sampleIds The {@link Sample} id
	 * @param locale    Locale of the logged in user
	 * @return a map stating success
	 */
	@RequestMapping(value = "/add/samples", method = RequestMethod.PUT)
	@ResponseBody
	public CartResponse addProjectSample(@RequestBody CartRequest cartRequest, Locale locale) {
		return this.cart.addProjectSamplesToCart(cartRequest, locale);
	}

	/**
	 * Delete a {@link Sample} from the cart from a given {@link Project}
	 * 
	 * @param projectId
	 *            The {@link Project} ID
	 * @param sampleIds
	 *            The {@link Sample} ID
	 * @return a map stating success
	 */
	@RequestMapping(value = "/project/{projectId}/samples", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> removeProjectSamples(@PathVariable Long projectId, @RequestBody Set<Long> sampleIds) {
		Project project = projectService.read(projectId);
		Set<Sample> samples = loadSamplesForProject(project, sampleIds);
		Set<Sample> selectedSamplesForProject = getSelectedSamplesForProject(project);
		selectedSamplesForProject.removeAll(samples);

		if (selectedSamplesForProject.isEmpty()) {
			selected.remove(project);
		}

		return ImmutableMap.of("success", true);
	}

	/**
	 * Remove a single sample from the cart
	 * 
	 * @param projectId
	 *            The project id of the sample
	 * @param sampleId
	 *            the id of the sample
	 * @return Success if the sample was successfully removed
	 */
	@RequestMapping(value = "/project/{projectId}/samples/{sampleId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, Object> removeProjectSample(@PathVariable Long projectId, @PathVariable Long sampleId) {
		Project project = projectService.read(projectId);
		Sample sampleForProject = sampleService.getSampleForProject(project, sampleId).getObject();
		Set<Sample> selectedSamplesForProject = getSelectedSamplesForProject(project);

		selectedSamplesForProject.remove(sampleForProject);

		if (selectedSamplesForProject.isEmpty()) {
			selected.remove(project);
		}

		return ImmutableMap.of("success", true);
	}

	/**
	 * Add an entire {@link Project} to the cart
	 * 
	 * @param projectId
	 *            The ID of the {@link Project}
	 * @return a map stating success
	 */
	@RequestMapping(value = "/project/{projectId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> addProject(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		Set<Sample> samples = samplesForProject.stream().map((j) -> {
			return j.getObject();
		}).collect(Collectors.toSet());

		getSelectedSamplesForProject(project).addAll(samples);

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
		Project project = projectService.read(projectId);
		selected.remove(project);

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
		Set<Project> projects = selected.keySet();
		List<Map<String, Object>> projectList = new ArrayList<>();
		for (Project p : projects) {
			Set<Sample> selectedSamplesForProject = selected.get(p);
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
		Set<Project> projects = selected.keySet();
		List<Map<String, Object>> projectList = new ArrayList<>();
		for (Project p : projects) {
			Set<Sample> selectedSamplesForProject = selected.get(p);
			List<Map<String, Object>> samples = getSamplesAsListForGalaxy(selectedSamplesForProject, p.getId());

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

	private synchronized Set<Sample> getSelectedSamplesForProject(Project project) {
		if (!selected.containsKey(project)) {
			selected.put(project, new HashSet<>());
		}

		return selected.get(project);
	}

	/**
	 * Get the number of projects contained in the cart.
	 *
	 * @return {@link Integer} number of projects in the cart.
	 */
	public int getNumberOfProjects() {
		return this.selected.keySet().size();
	}

	/**
	 * Get the number of samples contained in the cart.
	 *
	 * @return {@link Integer} number of samples in the cart.
	 */
	public int getNumberOfSamples() {
		int count = 0;
		for (Project project : selected.keySet()) {
			count += selected.get(project).size();
		}

		return count;
	}

}
