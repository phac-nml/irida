package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.SequenceFileAnalysisException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesExportToFile;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesExportTypes;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DataTablesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.ProjectSampleModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.UISampleFilter;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProjectSamples;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Controller for handling interactions with samples in a project
 */
@Controller
public class ProjectSamplesController {
	// From configuration.properties
	private @Value("${ngsarchive.linker.available}") Boolean LINKER_AVAILABLE;
	private @Value("${ngsarchive.linker.script}") String LINKER_SCRIPT;

	// Sub Navigation Strings
	private static final String ACTIVE_NAV = "activeNav";
	private static final String ACTIVE_NAV_SAMPLES = "samples";

	public static final String PROJECT_NAME_PROPERTY = "name";

	// Page Names
	private static final String PROJECTS_DIR = "projects/";
	private static final String PROJECT_TEMPLATE_DIR = PROJECTS_DIR + "templates/";
	private static final String PROJECT_SAMPLES_PAGE = PROJECTS_DIR + "project_samples";
	private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

	// Services
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final ProjectControllerUtils projectControllerUtils;
	private final SequencingObjectService sequencingObjectService;
	private MessageSource messageSource;

	@Autowired
	public ProjectSamplesController(ProjectService projectService, SampleService sampleService, SequencingObjectService sequencingObjectService, ProjectControllerUtils projectControllerUtils,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.sequencingObjectService = sequencingObjectService;
		this.projectControllerUtils = projectControllerUtils;
		this.messageSource = messageSource;
	}

	/**
	 * Get the samples for a given project
	 *
	 * @param model
	 * 		A model for the sample list view
	 * @param principal
	 * 		The user reading the project
	 * @param projectId
	 * 		The ID of the project
	 * @param httpSession
	 * 		The user's session
	 *
	 * @return Name of the project samples list view
	 */
	@RequestMapping(value = { "/projects/{projectId}", "/projects/{projectId}/samples" })
	public String getProjectSamplesPage(final Model model, final Principal principal, @PathVariable long projectId,
			HttpSession httpSession) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		// Set up the template information
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		// Exporting functionality
		boolean haveGalaxyCallbackURL = (httpSession.getAttribute(ProjectsController.GALAXY_CALLBACK_VARIABLE_NAME)
				!= null);
		model.addAttribute("linkerAvailable", LINKER_AVAILABLE);
		model.addAttribute("galaxyCallback", haveGalaxyCallbackURL);

		// Add the associated projects
		List<RelatedProjectJoin> associatedJoin = projectService.getRelatedProjects(project);
		List<Project> associated = associatedJoin.stream().map(RelatedProjectJoin::getObject)
				.collect(Collectors.toList());
		model.addAttribute("associatedProjects", associated);

		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_SAMPLES);
		return PROJECT_SAMPLES_PAGE;
	}

	/**
	 * Get the create new sample page.
	 *
	 * @param projectId
	 * 		Id for the {@link Project} the sample will belong to.
	 * @param model
	 * 		{@link Model}
	 * @param sample
	 * 		{@link Sample} required if redirected back to the create page.
	 *
	 * @return Name of the add sample page.
	 */
	@RequestMapping("/projects/{projectId}/samples/new")
	public String getCreateNewSamplePage(@PathVariable Long projectId, Model model, Sample sample) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		model.addAttribute("sample", sample);
		return "projects/project_add_sample";
	}

	/**
	 * Create a new {@link Sample} in a {@link Project}
	 *
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}
	 * @param sample
	 * 		{@link Sample} to create in the {@link Project}
	 *
	 * @return Redirect to the newly created {@link Sample} page
	 */
	@RequestMapping(value = "/projects/{projectId}/samples/new", method = RequestMethod.POST)
	public String createNewSample(@PathVariable Long projectId, Sample sample) {
		Project project = projectService.read(projectId);

		// Need a check to see if the Organism name was actually set.
		if(sample.getOrganism().equals("")) {
			sample.setOrganism(null);
		}

		try {
			Join<Project, Sample> join = projectService.addSampleToProject(project, sample, true);
			return "redirect:/projects/" + projectId + "/samples/" + join.getObject().getId();
		} catch (EntityExistsException e) {
			// This will be thrown if a sample already exists in the project with this name.
			// This should have already been addressed on the client
			return "redirect:/projects/" + projectId + "/samples/new";
		}
	}

	/**
	 * Creates the modal to remove samples from a project.
	 *
	 * @param ids       {@link List} of sample names to remove.
	 * @param projectId current {@link Project} identifier
	 * @param model     UI model
	 * @return path to remove modal template
	 */
	@RequestMapping(value = "/projects/{projectId}/templates/remove-modal", produces = MediaType.TEXT_HTML_VALUE)
	public String getRemoveSamplesFromProjectModal(@RequestParam(name = "sampleIds[]") List<Long> ids, @PathVariable Long projectId, Model model) {
		List<Sample> samplesThatAreInMultiple = new ArrayList<>();
		List<Sample> samplesThatAreInOne = new ArrayList<>();

		for (Long id : ids) {
			Sample sample = sampleService.read(id);
			List<Join<Project, Sample>> join = projectService.getProjectsForSample(sample);

			if (join.size() > 1) {
				samplesThatAreInMultiple.add(sample);
			} else {
				samplesThatAreInOne.add(sample);
			}
		}

		model.addAttribute("samplesThatAreInMultiple", samplesThatAreInMultiple);
		model.addAttribute("samplesThatAreInOne", samplesThatAreInOne);
		model.addAttribute("project", projectService.read(projectId));
		return PROJECT_TEMPLATE_DIR + "remove-modal.tmpl";
	}

	/**
	 * Generate a modal for displaying the ngs-linker command
	 *
	 * @param ids       List of {@link Sample} identifiers
	 * @param projectId identtifier for the current {@link Project}
	 * @param model     UI Model
	 * @return Path to template
	 */
	@RequestMapping(value = "/projects/{projectId}/templates/linker-modal", produces = MediaType.TEXT_HTML_VALUE)
	public String getLinkerCommandModal(@RequestParam(name = "sampleIds[]", defaultValue = "") List<Long> ids,
			@PathVariable Long projectId, Model model) {
		Project project = projectService.read(projectId);
		int totalSamples = sampleService.getSamplesForProject(project).size();

		String cmd = LINKER_SCRIPT + " -p " + projectId;
		if (ids.size() != 0 && ids.size() != totalSamples) {
			cmd += " -s " +  StringUtils.join(ids, " -s ");
		}

		model.addAttribute("command", cmd);
		return PROJECT_TEMPLATE_DIR + "linker-modal.tmpl";
	}

	/**
	 * Create a modal dialog to merge samples in a project.
	 *
	 * @param projectId current {@link Project} identifier
	 * @param ids       {@link List} List of {@link Long} identifiers for {@link Sample} to merge.
	 * @param model     UI Model
	 * @return Path to merge modal template
	 */
	@RequestMapping(value = "/projects/{projectId}/templates/merge-modal", produces = MediaType.TEXT_HTML_VALUE)
	public String getMergeSamplesInProjectModal(@PathVariable Long projectId, @RequestParam(name = "sampleIds[]") List<Long> ids, Model model) {
		Project project = projectService.read(projectId);
		List<Sample> samples = new ArrayList<>();
		List<Sample> locked = new ArrayList<>();

		//check for locked samples
		ids.forEach(i -> {
			ProjectSampleJoin join = sampleService.getSampleForProject(project, i);
			samples.add(join.getObject());

			if (!join.isOwner()) {
				locked.add(join.getObject());
			}
		});

		model.addAttribute("project", project);
		model.addAttribute("samples", samples);
		model.addAttribute("locked", locked);

		return PROJECT_TEMPLATE_DIR + "merge-modal.tmpl";
	}

	/**
	 * Create a modal dialogue for moving or sharing {@link Sample} to another {@link Project}
	 *
	 * @param ids       {@link List} of identifiers for {@link Sample}s to copy or move.
	 * @param projectId Identifier for the current {@link Project}
	 * @param model     UI Model
	 * @param move      Whether or not to display share or move wording.
	 * @return Path to share or move modal template.
	 */
	@RequestMapping(value = "/projects/{projectId}/templates/copy-move-modal", produces = MediaType.TEXT_HTML_VALUE)
	public String getShareSamplesModal(@RequestParam(name = "sampleIds[]") List<Long> ids, @PathVariable Long projectId,
			Model model, @RequestParam(required = false) boolean move) {
		Project project = projectService.read(projectId);
		
		model.addAllAttributes(generateShareMoveSamplesContent(project, ids));
		model.addAttribute("projectId", projectId);
		model.addAttribute("type", move ? "move" : "copy");
		model.addAttribute("isRemoteProject", project.isRemote());
		return PROJECT_TEMPLATE_DIR + "copy-move-modal.tmpl";
	}

	/**
	 * Get the modal window for filtering project samples
	 *
	 * @param projectId  {@link Long} identifier for the current {@link Project}
	 * @param associated Which associated projects are enabled
	 * @param filter     {@link UISampleFilter} Current filter parameters.
	 * @param model      UI Model
	 * @return {@link String} path to the modal template
	 */
	@RequestMapping(value = "/projects/{projectId}/template/samples-filter-modal", produces = MediaType.TEXT_HTML_VALUE)
	public String getProjectSamplesFilterModal(@PathVariable Long projectId,
			@RequestParam(required = false, name = "associated[]", defaultValue = "") List<Long> associated,
			UISampleFilter filter, Model model) {
		model.addAttribute("filter", filter);

		/*
		Add the current project to the list of project ids to ensure that we get the organisms
		that are associated with the current project.
		 */
		associated.add(projectId);

		/*
		Create an alphabetically organized list of unique values of all the different organisms
		that are in the current and all associated projects.
		 */
		Set<String> organismSet = new HashSet<>();
		for (Long id : associated) {
			Project project = projectService.read(id);
			organismSet.addAll(sampleService.getSampleOrganismsForProject(project));
		}
		List<String> organisms = new ArrayList<>(organismSet);
		organisms.sort((o1, o2) -> {
			if (Strings.isNullOrEmpty(o1)) {
				o1 = "";
			}
			if (Strings.isNullOrEmpty(o2)) {
				o2 = "";
			}
			return o1.compareToIgnoreCase(o2);
		});
		model.addAttribute("organisms", organisms);
		return PROJECT_TEMPLATE_DIR + "filter-modal.tmpl";
	}

	/**
	 * Generate a {@link Map} of {@link Sample} to move or share.
	 *
	 * @param project  The {@link Project}.
	 * @param ids
	 * 		{@link Long} of ids for {@link Sample}
	 *
	 * @return {@link Map} of samples to be moved or shared.
	 */
	private Map<String, List<Sample>> generateShareMoveSamplesContent(Project project, List<Long> ids) {
		Map<String, List<Sample>> model = new HashMap<>();
		List<Sample> samples = new ArrayList<>();
		List<Sample> extraSamples = new ArrayList<>();
		List<Sample> lockedSamples = new ArrayList<>();

		ids.stream().map(i -> sampleService.getSampleForProject(project, i)).forEach(j -> {
			samples.add(j.getObject());

			if (!j.isOwner()) {
				lockedSamples.add(j.getObject());
			}
		});

		// Only initially need to display the first 10 samples.
		int end = samples.size();
		if (end > 9) {
			end = 9;
			extraSamples = samples.subList(end, samples.size());
		}

		model.put("samples", samples.subList(0, end));
		model.put("extraSamples", extraSamples);
		model.put("lockedSamples", lockedSamples);
		return model;
	}

	/**
	 * Get a listing of sample names not found in the current project based on a list.
	 *
	 * @param projectId   {@link Project} identifier for project
	 * @param sampleNames {@link List} of sample names
	 * @param projects    List of associated {@link Project} identifiers
	 * @param locale      {@link Locale} local of current user
	 * @return {@link Map} of Samples not in the current project
	 */
	@RequestMapping("/projects/{projectId}/ajax/samples/missing")
	@ResponseBody
	public Map<String, Object> getSampleNamesNotInProject(@PathVariable Long projectId,
			@RequestParam(value = "projects[]", defaultValue = "") List<Long> projects,
			@RequestParam(value = "sampleNames[]") List<String> sampleNames, Locale locale) {
		// Need to keep the count for comparison after.
		int originalCount = sampleNames.size();

		// Get a list of all samples for all projects
		projects.add(0, projectId);
		for (Long id : projects) {
			List<Join<Project, Sample>> psj = sampleService.getSamplesForProject(projectService.read(id));
			// See if the name is there
			for (Join<Project, Sample> join : psj) {
				Sample sample = join.getObject();
				if (sampleNames.contains(sample.getLabel())) {
					sampleNames.remove(sample.getLabel());
				}
				if (sampleNames.size() == 0) {
					break;
				}
			}
			if (sampleNames.size() == 0) {
				break;
			}
		}


		Map<String, Object> result = new HashMap<>();
		if (sampleNames.size() > 0) {
			result.put("missingNames", sampleNames);
			result.put("message", messageSource.getMessage("project.sample.filterByFile.error", new Object[] {
					originalCount - sampleNames.size(),
					originalCount
			}, locale));
		} else {
			result.put("success",
					messageSource.getMessage("project.sample.filterByFile.success", new Object[] {}, locale));
		}

		return result;
	}

	/**
	 * Generate the {@link Sample}s for the {@link Project} table based on the filter criteria.
	 *
	 * @param projectId   identifier for the current {@link Project}
	 * @param params      for the current DataTable.
	 * @param sampleNames List of {@link Sample} names to filter by.
	 * @param associated  List of associated {@link Project} identifiers currently displayed in the table.
	 * @param filter      for specific {@link Sample} attributes.
	 * @param locale      for the current user.
	 * @return {@link DTProjectSamples} that meet the requirements
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public DataTablesResponse getProjectSamples(@PathVariable Long projectId,
			@DataTablesRequest DataTablesParams params,
			@RequestParam(required = false, name = "sampleNames[]", defaultValue = "") List<String> sampleNames,
			@RequestParam(required = false, name = "associated[]", defaultValue = "") List<Long> associated,
			UISampleFilter filter, Locale locale) {
		List<Project> projects = new ArrayList<>();
		// Check to see if any associated projects need to be added to the query.
		if (!associated.isEmpty()) {
			projects = (List<Project>) projectService.readMultiple(associated);
		}
		// This project is always in the query.
		projects.add(projectService.read(projectId));

		final Page<ProjectSampleJoin> page = sampleService.getFilteredSamplesForProjects(projects, sampleNames,
				filter.getName(), params.getSearchValue(), filter.getOrganism(), filter.getStartDate(),
				filter.getEndDate(), params.getCurrentPage(), params.getLength(), params.getSort());

		// Create DataTables representation of the page.
		List<DataTablesResponseModel> models = new ArrayList<>();
		for (ProjectSampleJoin psj : page.getContent()) {
			models.add(buildProjectSampleDataTablesModel(psj, locale));
		}
		return new DataTablesResponse(params, page, models);
	}

	/**
	 * Build a {@link ProjectSampleModel} object for a given {@link Sample}
	 *
	 * @param sso    a {@link ProjectSampleJoin} to build the {@link ProjectSampleModel} from
	 * @param locale of the current user.
	 * @return a newly constructed {@link ProjectSampleModel}
	 */
	private DTProjectSamples buildProjectSampleDataTablesModel(ProjectSampleJoin sso, Locale locale) {
		Project project = sso.getSubject();
		Long genomeSize = project.getGenomeSize();
		Sample sample = sso.getObject();
		Double coverage = null;

		if (genomeSize != null) {
			try {
				coverage = sampleService.estimateCoverageForSample(sample, genomeSize);
			} catch (SequenceFileAnalysisException e) {
				// Don't need to do anything here.
			}
		}
		List<QCEntry> qcEntriesForSample = sampleService.getQCEntriesForSample(sample);
		List<String> list = new ArrayList<>();
		for (QCEntry q : qcEntriesForSample) {
			q.addProjectSettings(project);
			String status = q.getStatus()
					.toString();
			if (q.getStatus() == QCEntry.QCEntryStatus.NEGATIVE) {
				list.add(
						messageSource.getMessage("sample.files.qc." + q.getType(), new Object[] { q.getMessage() }, locale));
			}
		}
		return new DTProjectSamples(sso, list, coverage);
	}

	/**
	 * Get a list of all {@link Sample} ids in a {@link Project}
	 *
	 * @param projectId            Identifier for the current project
	 * @param params               {@link DataTablesParams}
	 * @param sampleNames          {@link List} of sample names that the {@link Project} {@link Sample}s is currently filtered by.
	 * @param associatedProjectIds {@link List} of associated {@link Project} identifiers
	 * @param search               The global filter for the table
	 * @param filter               The specific attribute filters applied to the table.
	 * @return {@link Map} of {@link Project} identifiers and associated {@link Sample} identifiers available.
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/sampleIds", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<String>> getAllProjectSampleIds(@PathVariable Long projectId,
			@DataTablesRequest DataTablesParams params,
			@RequestParam(required = false, defaultValue = "", value = "sampleNames[]") List<String> sampleNames,
			@RequestParam(value = "associated[]", required = false, defaultValue = "") List<Long> associatedProjectIds,
			@RequestParam(required = false, defaultValue = "") String search, UISampleFilter filter) {
		// Add the current project to the associatedProjectIds list.
		associatedProjectIds.add(projectId);

		// Get the actual projects.
		List<Project> projects = new ArrayList<>(
				(Collection<? extends Project>) projectService.readMultiple(associatedProjectIds));

		Sort sort = new Sort(Direction.ASC, "id");
		final Page<ProjectSampleJoin> page = sampleService.getFilteredSamplesForProjects(projects, sampleNames,
				filter.getName(), params.getSearchValue(), filter.getOrganism(), filter.getStartDate(),
				filter.getEndDate(), 0, Integer.MAX_VALUE, params.getSort());

		// Converting everything to a string for consumption by the UI.
		Map<String, List<String>> result = new HashMap<>();
		for (ProjectSampleJoin join : page) {
			String pId = join.getSubject().getId().toString();
			if (!result.containsKey(pId)) {
				result.put(pId, new ArrayList<>());
			}
			result.get(pId).add(join.getObject().getId().toString());
		}

		return result;
	}

	/**
	 * Search for projects available for a user to copy samples to. If the user is an admin it will show all projects.
	 *
	 * @param projectId identifier for the current {@link Project}
	 * @param term      A search term
	 * @param pageSize  The size of the page requests
	 * @param page      The page number (0 based)
	 * @return a {@code Map<String,Object>} containing: total: total number of elements results: A {@code
	 * Map<Long,String>} of project IDs and project names.
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/available_projects")
	@ResponseBody
	public Map<String, Object> getProjectsAvailableToCopySamples(final @PathVariable Long projectId,
			@RequestParam String term, @RequestParam(required = false, defaultValue = "10") int pageSize,
			@RequestParam int page) {
		final Project projectToExclude = projectService.read(projectId);
		List<Map<String, String>> projectMap = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();
		final Page<Project> projects = projectService
				.getUnassociatedProjects(projectToExclude, term, page, pageSize, Direction.ASC, PROJECT_NAME_PROPERTY);

		for (Project p : projects) {
			Map<String, String> map = new HashMap<>();
			map.put("id", p.getId().toString());
			map.put("text", p.getName());
			projectMap.add(map);
		}
		response.put("total", projects.getTotalElements());

		response.put("projects", projectMap);

		return response;
	}

	/**
	 * Share or move samples from one project to another
	 *
	 * @param projectId    The original project id
	 * @param sampleIds    the sample identifiers to share
	 * @param newProjectId The new project id
	 * @param remove       true/false whether to remove the samples from the original  project
	 * @param giveOwner    whether to give ownership of the sample to the new project
	 * @param locale       the locale specified by the browser.
	 * @return A list of warnings
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/copy", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> shareSampleToProject(@PathVariable Long projectId,
			@RequestParam(value = "sampleIds[]") List<Long> sampleIds, @RequestParam Long newProjectId,
			@RequestParam(required = false) boolean remove,
			@RequestParam(required = false, defaultValue = "false") boolean giveOwner, Locale locale) {
		Project originalProject = projectService.read(projectId);
		Project newProject = projectService.read(newProjectId);

		Map<String, Object> response = new HashMap<>();
		List<String> warnings = new ArrayList<>();

		Iterable<Sample> samples = sampleService.readMultiple(sampleIds);

		List<ProjectSampleJoin> successful = new ArrayList<>();
		try {

			if (remove) {
				successful = projectService.moveSamples(originalProject, newProject, Lists.newArrayList(samples));
			} else {
				successful = projectService.shareSamples(originalProject, newProject, Lists.newArrayList(samples), giveOwner);
			}

		} catch (EntityExistsException ex) {
			logger.warn("Attempt to add project to sample failed", ex);
			warnings.add(ex.getLocalizedMessage());
		} catch (AccessDeniedException ex) {
			logger.warn("Access denied adding samples to project " + newProjectId, ex);
			String msg = remove ? "project.samples.move.sample-denied" : "project.samples.copy.sample-denied";
			warnings.add(
					messageSource.getMessage(msg, new Object[] { newProject.getName() }, locale));
		}

		if (!warnings.isEmpty() || successful.size() == 0) {
			response.put("result", "warning");
			response.put("warnings", warnings);
		} else {
			response.put("result", "success");
		}
		// 1. Only one sample copied
		// 2. Only one sample moved
		if (successful.size() == 1) {
			if (remove) {
				response.put("message", messageSource.getMessage("project.samples.move-single-success-message",
						new Object[] { successful.get(0).getObject().getSampleName(), newProject.getName() }, locale));
			} else {
				response.put("message", messageSource.getMessage("project.samples.copy-single-success-message",
						new Object[] { successful.get(0).getObject().getSampleName(), newProject.getName() }, locale));
			}
		}
		// 3. Multiple samples copied
		// 4. Multiple samples moved
		else if (successful.size() > 1) {
			if (remove) {
				response.put("message", messageSource.getMessage("project.samples.move-multiple-success-message",
						new Object[] { successful.size(), newProject.getName() }, locale));
			} else {
				response.put("message", messageSource.getMessage("project.samples.copy-multiple-success-message",
						new Object[] { successful.size(), newProject.getName() }, locale));
			}
		}

		response.put("successful", successful.stream()
				.map(ProjectSampleJoin::getId)
				.collect(Collectors.toList()));

		return response;
	}

	/**
	 * Remove a list of samples from a a Project.
	 *
	 * @param projectId
	 * 		Id of the project to remove the samples from
	 * @param sampleIds
	 * 		An array of samples to remove from a project
	 * @param locale
	 * 		The locale of the web browser.
	 *
	 * @return Map containing either success or errors.
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/delete", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> deleteProjectSamples(@PathVariable Long projectId,
			@RequestParam(value = "sampleIds[]") List<Long> sampleIds, Locale locale) {
		Project project = projectService.read(projectId);

		// Creating the message before removing the samples so that if the sample is only in one project it does not get removed
		// before its name can be used to create the message.
		Map<String, Object> result = new HashMap<>();
		if (sampleIds.size() == 1) {
			Sample sample = sampleService.read(sampleIds.get(0));
			result.put("message",
					messageSource.getMessage("project.samples.remove-success-singular",
							new Object[] { sample.getSampleName(), project.getLabel() }, locale));
		} else {
			result.put("message",
					messageSource.getMessage("project.samples.remove-success-plural",
							new Object[] { sampleIds.size(), project.getLabel() }, locale));
		}

		for (Long id : sampleIds) {
			try {
				Sample sample = sampleService.read(id);
				projectService.removeSampleFromProject(project, sample);
			} catch (EntityNotFoundException e) {
				result.put("error", "Cannot find sample with id: " + id);
			}

		}

		result.put("result", "success");
		return result;
	}

	/**
	 * Merges a list of samples into either the first sample in the list with a new name if provided, or into the
	 * selected sample based on the id.
	 *
	 * @param projectId
	 * 		The id for the current {@link Project}
	 * @param mergeSampleId
	 * 		An id for a {@link Sample} to merge the other samples into.
	 * @param sampleIds
	 * 		A list of ids for {@link Sample} to merge together.
	 * @param sampleName
	 * 		An optional new name for the {@link Sample}.
	 * @param locale
	 * 		The {@link Locale} of the current user.
	 *
	 * @return a map of {@link Sample} properties representing the merged sample.
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/merge", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> ajaxSamplesMerge(@PathVariable Long projectId,
			@RequestParam Long mergeSampleId, @RequestParam(value = "sampleIds[]") List<Long> sampleIds,
			@RequestParam String sampleName, Locale locale) {
		Map<String, Object> result = new HashMap<>();
		int samplesMergeCount = sampleIds.size();
		Project project = projectService.read(projectId);
		// Determine which sample to merge into
		Sample mergeIntoSample = sampleService.read(mergeSampleId);
		sampleIds.remove(mergeSampleId);

		if (!Strings.isNullOrEmpty(sampleName)) {
			try {
				mergeIntoSample.setSampleName(sampleName);
				mergeIntoSample = sampleService.update(mergeIntoSample);
			} catch (ConstraintViolationException e) {
				logger.error(e.getLocalizedMessage());
				result.put("result", "error");
				result.put("warnings", getErrorsFromViolationException(e));
				return result;
			}
		}

		// Create an update map
		List<Sample> mergeSamples = sampleIds.stream().map(sampleService::read).collect(Collectors.toList());

		// Merge the samples
		sampleService.mergeSamples(project, mergeIntoSample, mergeSamples);

		result.put("result", "success");
		result.put(
				"message",
				messageSource.getMessage("project.samples.combine-success", new Object[] { samplesMergeCount,
						mergeIntoSample.getSampleName() }, locale));
		return result;
	}

	/**
	 * Remove the given {@link Sample}s from the given {@link Project}
	 *
	 * @param projectId ID of the project to remove from
	 * @param samples   {@link Sample} ids to remove
	 * @param locale    User's locale
	 * @return Map with success message
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> removeSamplesFromProject(@PathVariable Long projectId,
			@RequestParam(value = "sampleIds[]") List<Long> samples, Locale locale) {
		Map<String, Object> result = new HashMap<>();

		// read the project
		Project project = projectService.read(projectId);

		// get the samples
		Iterable<Sample> readMultiple = sampleService.readMultiple(samples);

		// remove all samples
		projectService.removeSamplesFromProject(project, readMultiple);

		// build success message
		result.put("result", "success");
		result.put(
				"message", messageSource.getMessage(samples.size() == 1 ?
								"project.samples.remove-success-singular" :
								"project.samples.remove-success-plural",
						new Object[] { samples.size(), project.getLabel() }, locale));

		return result;
	}

	/**
	 * Download a set of sequence files from selected samples within a project
	 *
	 * @param projectId Id for a {@link Project}
	 * @param ids       List of ids ofr {@link Sample} within the project
	 * @param response  {@link HttpServletResponse}
	 * @throws IOException if we fail to read a file from the filesystem.
	 */
	@RequestMapping(value = "/projects/{projectId}/download/files")
	public void downloadSamples(@PathVariable Long projectId, @RequestParam(value = "ids[]") List<Long> ids,
			HttpServletResponse response)
			throws IOException {
		Project project = projectService.read(projectId);
		List<Sample> samples = (List<Sample>) sampleService.readMultiple(ids);

		// Add the appropriate headers
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + project.getName() + ".zip\"");
		response.setHeader("Transfer-Encoding", "chunked");

		// storing used file names to ensure we don't have a conflict
		Set<String> usedFileNames = new HashSet<>();

		try (ZipOutputStream outputStream = new ZipOutputStream(response.getOutputStream())) {
			for (Sample sample : samples) {
				Collection<SampleSequencingObjectJoin> sequencingObjectsForSample = sequencingObjectService
						.getSequencingObjectsForSample(sample);

				for (SampleSequencingObjectJoin join : sequencingObjectsForSample) {
					for (SequenceFile file : join.getObject().getFiles()) {
						Path path = file.getFile();

						String fileName = project.getName() + "/" + sample.getSampleName() + "/" + path.getFileName()
								.toString();
						if (usedFileNames.contains(fileName)) {
							fileName = handleDuplicate(fileName, usedFileNames);
						}
						final ZipEntry entry = new ZipEntry(fileName);
						// set the file creation time on the zip entry to be
						// whatever the creation time is on the filesystem
						final BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
						entry.setCreationTime(attr.creationTime());
						entry.setLastModifiedTime(attr.creationTime());

						outputStream.putNextEntry(entry);

						usedFileNames.add(fileName);

						Files.copy(path, outputStream);

						outputStream.closeEntry();
					}
				}
			}
			outputStream.finish();
		} catch (IOException e) {
			// this generally means that the user has cancelled the download
			// from their web browser; we can safely ignore this
			logger.debug("This *probably* means that the user cancelled the download, "
					+ "but it might be something else, see the stack trace below for more information.", e);
		} catch (Exception e) {
			logger.error("Download failed...", e);
		} finally {
			// close the response outputStream so that we're not leaking
			// streams.
			response.getOutputStream().close();
		}
	}

	/**
	 * Rename a filename {@code original} and ensure it doesn't exist in {@code usedNames}. Uses the windows style of
	 * renaming file.ext to file (1).ext
	 *
	 * @param original  original file name
	 * @param usedNames names that original must not conflict with
	 * @return modified name
	 */
	private String handleDuplicate(String original, Set<String> usedNames) {
		int lastDot = original.lastIndexOf('.');

		int index = 0;
		String result;
		do {
			index++;
			result = original.substring(0, lastDot) + " (" + index + ")" + original.substring(lastDot);
		} while (usedNames.contains(result));

		return result;
	}

	/**
	 * Export {@link Sample} from a {@link Project} as either Excel or CSV formatted.
	 *
	 * @param projectId   identifier for the current {@link Project}
	 * @param type        of file to export (.csv or .xlsx)
	 * @param params      DataTable parameters.
	 * @param sampleNames List of {@link Sample} names the {@link Project} is filtered on
	 * @param associated  List of acitve associated {@link Project} identifiers.
	 * @param filter      {@link Sample} attribute filters applied.
	 * @param request     {@link HttpServletRequest}
	 * @param response    {@link HttpServletResponse}
	 * @param locale      of the current user.
	 * @throws IOException if the exported file cannot be written
	 */
	@RequestMapping(value = "/projects/{projectId}/samples/export")
	public void exportProjectSamplesTable(
			@PathVariable Long projectId,
			@RequestParam DataTablesExportTypes type,
			@DataTablesRequest DataTablesParams params,
			@RequestParam(required = false, defaultValue = "") List<String> sampleNames,
			@RequestParam(required = false, defaultValue = "") List<Long> associated,
			UISampleFilter filter,
			HttpServletRequest request,
			HttpServletResponse response,
			Locale locale) throws IOException {

		Project project = projectService.read(projectId);
		List<Project> projects = new ArrayList<>();

		if (!associated.isEmpty()) {
			projects = (List<Project>) projectService.readMultiple(associated);
		}
		projects.add(project);

		final Page<ProjectSampleJoin> page = sampleService
				.getFilteredSamplesForProjects(projects, sampleNames, filter.getName(), params.getSearchValue(), filter.getOrganism(), filter.getStartDate(),
						filter.getEndDate(), 0, Integer.MAX_VALUE, params.getSort());

		// Create DataTables representation of the page.
		List<DTProjectSamples> models = new ArrayList<>();
		for (ProjectSampleJoin psj : page.getContent()) {
			models.add(buildProjectSampleDataTablesModel(psj, locale));
		}
		List<String> headers = models.get(0)
				.getExportableTableHeaders(messageSource, locale);
		DataTablesExportToFile.writeFile(type, response, project.getLabel()
				.replace(" ", "_"), models, headers);
	}

	/**
	 * Valid the name for a new {@link Sample} label.  This checks against existing sample names within the current
	 * project to ensure that it is not a duplicate.
	 *
	 * @param projectId  Identifier for the current project
	 * @param sampleName {@link String} name to validate.
	 * @return {@link Boolean} true if the name is unique.
	 */
	@RequestMapping("/projects/{projectId}/validate-sample-name")
	@ResponseBody
	public boolean validateNewSampleName(@PathVariable Long projectId, @RequestParam String sampleName) {
		Project project = projectService.read(projectId);
		try {
			sampleService.getSampleBySampleName(project, sampleName);
			return false;
		} catch (EntityNotFoundException e) {
			// If the sample is not found, then the name is good to go!
			return true;
		}

	}

	/**
	 * Changes a {@link ConstraintViolationException} to a usable map of strings for disabling in the UI.
	 *
	 * @param e
	 * 		{@link ConstraintViolationException} for the form submitted.
	 *
	 * @return Map of string {fieldName, error}
	 */
	private Map<String, String> getErrorsFromViolationException(ConstraintViolationException e) {
		Map<String, String> errors = new HashMap<>();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			String message = violation.getMessage();
			String field = violation.getPropertyPath().toString();
			errors.put(field, message);
		}
		return errors;
	}
}
