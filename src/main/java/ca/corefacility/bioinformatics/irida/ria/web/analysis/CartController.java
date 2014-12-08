package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.components.Cart;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableMap;

/**
 * Controller managing interactions with the user's {@link Cart}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Controller
@Scope("session")
@RequestMapping("/cart")
public class CartController {
	private final Cart cart;

	private final SampleService sampleService;

	private final ProjectService projectService;

	@Autowired
	public CartController(Cart cart, SampleService sampleService, ProjectService projectService) {
		this.cart = cart;
		this.sampleService = sampleService;
		this.projectService = projectService;
	}

	/**
	 * Get a Json representation of what's in the {@link Cart}. Format: {
	 * 'projects' : [ { 'id': '5', 'label': 'project', 'samples': [ { 'id': '6',
	 * 'label': 'a sample' } ] } ]}
	 * 
	 * @return a Map<String,Object> containing the cart information.
	 */
	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> getCartMap() {
		List<Map<String, Object>> projects = getProjectsAsList(cart);
		return ImmutableMap.of("projects", projects);
	}

	/**
	 * Add a {@link Sample} to the {@link Cart} from a given {@link Project}
	 * 
	 * @param projectId
	 *            The {@link Project} ID
	 * @param sampleIds
	 *            The {@link Sample} id
	 * @return a map stating success
	 */
	@RequestMapping(value = "/project/{projectId}/samples", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> addProjectSample(@PathVariable Long projectId, @RequestBody Set<Long> sampleIds) {
		Project project = projectService.read(projectId);
		Set<Sample> samples = getSamplesForProjet(project, sampleIds);

		cart.addProjectSample(project, samples);

		return ImmutableMap.of("success", true);
	}

	/**
	 * Delete a {@link Sample} from the {@link Cart} from a given
	 * {@link Project}
	 * 
	 * @param projectId
	 *            The {@link Project} ID
	 * @param sampleIds
	 *            The {@link Sample} ID
	 * @return a map stating success
	 */
	@RequestMapping(value = "/project/{projectId}/samples", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> removeProjectSample(@PathVariable Long projectId, @RequestBody Set<Long> sampleIds) {
		Project project = projectService.read(projectId);
		Set<Sample> samples = getSamplesForProjet(project, sampleIds);
		cart.removeProjectSample(project, samples);

		return ImmutableMap.of("success", true);
	}

	/**
	 * Add an entire {@link Project} to the {@link Cart}
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

		cart.addProjectSample(project, samples);

		return ImmutableMap.of("success", true);
	}

	/**
	 * Delete an entire project from the cart
	 * 
	 * @param projectId
	 *            The ID of the {@link Project} to delete
	 * @return a map stating success
	 */
	@RequestMapping(value = "/project/{projectId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> removeProject(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		cart.removeProject(project);

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
	private Set<Sample> getSamplesForProjet(Project project, Set<Long> sampleIds) {
		return sampleIds.stream().map((id) -> {
			return sampleService.getSampleForProject(project, id);
		}).collect(Collectors.toSet());

	}

	/**
	 * Get the {@link Project}s in the {@link Cart} as a List for JSON
	 * serialization
	 * 
	 * @param cart
	 *            The {@link Cart} object
	 * @return A List<Map<String,Object>> containing the relevant Project and
	 *         Sample information
	 */
	private List<Map<String, Object>> getProjectsAsList(Cart cart) {
		Set<Project> projects = cart.getProjects();
		List<Map<String, Object>> projectList = new ArrayList<>();
		for (Project p : projects) {
			Set<Sample> selectedSamplesForProject = cart.getSelectedSamplesForProject(p);
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
			Map<String, Object> sampleMap = ImmutableMap.of("id", s.getId(), "label", s.getLabel());
			sampleList.add(sampleMap);
		}
		return sampleList;
	}

}
