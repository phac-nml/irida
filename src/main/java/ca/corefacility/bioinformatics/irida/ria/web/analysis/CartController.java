package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.remote.SampleRemoteService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;

import com.google.common.collect.ImmutableMap;

/**
 * Controller managing interactions with the selected sequences
 * 
 *
 */
@Controller
@Scope("session")
@RequestMapping("/cart")
public class CartController {
	private Map<Project, Set<Sample>> selected;
		
	private Map<String,Sample> remoteSelected;

	private final SampleService sampleService;
	private final UserService userService;
	private final ProjectService projectService;
	private final SequenceFileService sequenceFileService;
	
	private final SampleRemoteService sampleRemoteService;

	@Autowired
	public CartController(SampleService sampleService, UserService userService, ProjectService projectService, SequenceFileService sequenceFileService, SampleRemoteService sampleRemoteService) {
		this.sampleService = sampleService;
		this.projectService = projectService;
		this.userService = userService;
		this.sequenceFileService = sequenceFileService;
		this.sampleRemoteService = sampleRemoteService;
		selected = new HashMap<>();
		remoteSelected = new HashMap<>();
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
	@RequestMapping("/template/galaxy/project/{projectId}")
	public String getGalaxyModal(Model model, Principal principal,@PathVariable Long projectId ) {
		model.addAttribute("email", userService.getUserByUsername(principal.getName()).getEmail());
		model.addAttribute("name", projectService.read(projectId).getName() + "-" + principal.getName());
		String orgName = projectService.read(projectId).getOrganism() + "-" + principal.getName();
		model.addAttribute("orgName", orgName);
		return "templates/galaxy.tmpl";
	}

	/**
	 * Get a Json representation of what's in the cart. Format: {@code 
	 * 'projects' : [ { 'id': '5', 'label': 'project', 'samples': [ { 'id': '6',
	 * 'label': 'a sample', 'sequenceFiles': [ { 'selfRef' : 
	 * 'http://localhost/projects/1/samples/1/sequenceFiles/1' } ] } ] } ] }
	 * @return a {@code Map<String,Object>} containing the cart information.
	 */
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> getCartMap() {
		List<Map<String, Object>> projects = getProjectsAsList();
		return ImmutableMap.of("projects", projects, "remote", remoteSelected);
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
		remoteSelected.clear();
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
	 * @param projectId
	 *            The {@link Project} ID
	 * @param sampleIds
	 *            The {@link Sample} id
	 * @return a map stating success
	 */
	@RequestMapping(value = "/add/samples", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addProjectSample(@RequestParam Long projectId,
			@RequestParam(value = "sampleIds[]") Set<Long> sampleIds) {
		Project project = projectService.read(projectId);
		Set<Sample> samples = loadSamplesForProject(project, sampleIds);

		getSelectedSamplesForProject(project).addAll(samples);

		return ImmutableMap.of("success", true);
	}
	
	/**
	 * Add a remote sample to the cart
	 * 
	 * @param sampleURL
	 *            The URL of the sample to add
	 * @return Success if sample was added
	 */
	@RequestMapping(value = "/add/samples/remote", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addRemoteSample(@RequestParam String sampleURL) {
		Sample read = sampleRemoteService.read(sampleURL);
		remoteSelected.put(read.getLink(Link.REL_SELF).getHref(), read);

		return ImmutableMap.of("success", true);
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
		Sample sampleForProject = sampleService.getSampleForProject(project, sampleId);
		Set<Sample> selectedSamplesForProject = getSelectedSamplesForProject(project);

		selectedSamplesForProject.remove(sampleForProject);

		if (selectedSamplesForProject.isEmpty()) {
			selected.remove(project);
		}

		return ImmutableMap.of("success", true);
	}
	
	/**
	 * Remove all remote samples
	 * 
	 * @return Success if the samples were removed.
	 */
	@RequestMapping(value = "/remove/samples/remote", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, Object> removeRemoteSamples() {
		remoteSelected.clear();

		return ImmutableMap.of("success", true);
	}

	/**
	 * Remove a specific remote sample
	 * 
	 * @param sampleURL
	 *            The URL of the sample to remove
	 * @return Success if it was removed
	 */
	@RequestMapping(value = "/remove/samples/remote", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> removeRemoteSample(@RequestParam String sampleURL) {
		remoteSelected.remove(sampleURL);

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
		return sampleIds.stream().map((id) -> {
			return sampleService.getSampleForProject(project, id);
		}).collect(Collectors.toSet());

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

			Map<String, Object> projectMap = ImmutableMap
					.of("id", p.getId(), "label", p.getLabel(), "samples", samples);
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
			Map<String, Object> sampleMap = ImmutableMap.of("id", s.getId(), "label", s.getLabel()
					,"sequenceFiles",getSequenceFileList(s));
			sampleList.add(sampleMap);
		}
		return sampleList;
	}
	
	/**
	 * Get {@link SequenceFile}s as a List from a {@link Sample} for JSON serialization
	 * 
	 * @param samples
	 *            The {@link Sample} set
	 * @return A List<Map<String,Object>> containing the relevant SequenceFile
	 *         information
	 */
	private List<Map<String,Object>> getSequenceFileList(Sample sample) {
		List<Join<Sample, SequenceFile>> seqJoinList = sequenceFileService.getSequenceFilesForSample(sample);
		List<Map<String,Object>> sequenceFiles = new ArrayList<>();
		for(Join<Sample, SequenceFile>seqJoin : seqJoinList) {
			SequenceFile seq = seqJoin.getObject();
			List<Join<Project,Sample>> projectSamples = projectService.getProjectsForSample(sample);
			long projectId = projectSamples.get(0).getSubject().getId();
			
			String seqFileLoc = linkTo(methodOn(RESTSampleSequenceFilesController.class)
					.getSequenceFileForSample(projectId, sample.getId(),seq.getId())).withSelfRel().getHref();
			Map<String, Object> seqMap = ImmutableMap.of("selfRef",seqFileLoc);
			sequenceFiles.add(seqMap);
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
