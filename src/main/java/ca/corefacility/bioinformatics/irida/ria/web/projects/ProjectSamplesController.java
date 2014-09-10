package ca.corefacility.bioinformatics.irida.ria.web.projects;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectUserJoinSpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.Formats;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectSamplesDataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.base.Strings;

@Controller
@RequestMapping(value = "/projects")
public class ProjectSamplesController {
	// Sub Navigation Strings
	private static final String ACTIVE_NAV = "activeNav";
	private static final String ACTIVE_NAV_SAMPLES = "samples";

	private static final String PROJECT_NAME_PROPERTY = "name";

	// private static final String ACTIVE_NAV_ANALYSIS = "analysis";

	// Page Names
	private static final String PROJECTS_DIR = "projects/";
	public static final String LIST_PROJECTS_PAGE = PROJECTS_DIR + "projects";
	public static final String PROJECT_MEMBERS_PAGE = PROJECTS_DIR + "project_members";
	public static final String SPECIFIC_PROJECT_PAGE = PROJECTS_DIR + "project_details";
	public static final String CREATE_NEW_PROJECT_PAGE = PROJECTS_DIR + "project_new";
	public static final String PROJECT_METADATA_PAGE = PROJECTS_DIR + "project_metadata";
	public static final String PROJECT_METADATA_EDIT_PAGE = PROJECTS_DIR + "project_metadata_edit";
	public static final String PROJECT_SAMPLES_PAGE = PROJECTS_DIR + "project_samples";
	private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

	// Services
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final UserService userService;
	private final SequenceFileService sequenceFileService;
	private final ProjectControllerUtils projectControllerUtils;

	/*
	 * Converters
	 */
	Formatter<Date> dateFormatter;
	FileSizeConverter fileSizeConverter;

	@Autowired
	public ProjectSamplesController(ProjectService projectService, SampleService sampleService,
			UserService userService, SequenceFileService sequenceFileService,
			ProjectControllerUtils projectControllerUtils, ReferenceFileService referenceFileService,
			TaxonomyService taxonomyService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
		this.sequenceFileService = sequenceFileService;
		this.projectControllerUtils = projectControllerUtils;
		this.dateFormatter = new DateFormatter();
		this.fileSizeConverter = new FileSizeConverter();
	}

	@RequestMapping("/{projectId}/samples")
	public String getProjectSamplesPage(final Model model, final Principal principal, @PathVariable long projectId) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		// Set up the template information
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_SAMPLES);
		return PROJECT_SAMPLES_PAGE;
	}

	@RequestMapping(value = "/ajax/{projectId}/samples", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxProjectSamplesMap(
			@PathVariable Long projectId,
			@RequestParam(ProjectSamplesDataTable.REQUEST_PARAM_START) Integer start,
			@RequestParam(ProjectSamplesDataTable.REQUEST_PARAM_LENGTH) Integer length,
			@RequestParam(ProjectSamplesDataTable.REQUEST_PARAM_DRAW) Integer draw,
			@RequestParam(value = ProjectSamplesDataTable.REQUEST_PARAM_SORT_COLUMN, defaultValue = ProjectSamplesDataTable.SORT_DEFAULT_COLUMN) Integer sortColumn,
			@RequestParam(value = ProjectSamplesDataTable.REQUEST_PARAM_SORT_DIRECTION, defaultValue = ProjectSamplesDataTable.SORT_DEFAULT_DIRECTION) String direction,
			@RequestParam(ProjectSamplesDataTable.REQUEST_PARAM_SEARCH_VALUE) String searchValue) {
		Map<String, Object> response = new HashMap<>();
		Sort.Direction sortDirection = ProjectSamplesDataTable.getSortDirection(direction);
		String sortString = ProjectSamplesDataTable.getSortStringFromColumnID(sortColumn);

		int pageNum = ProjectSamplesDataTable.getPageNumber(start, length);
		try {
			Project project = projectService.read(projectId);
			Page<ProjectSampleJoin> page = sampleService.getSamplesForProjectWithName(project, searchValue, pageNum,
					length, sortDirection, sortString);
			List<Map<String, String>> samplesList = new ArrayList<>();
			for (Join<Project, Sample> join : page.getContent()) {
				Map<String, String> sMap = new HashMap<>();
				Sample s = join.getObject();
				sMap.put(ProjectSamplesDataTable.ID, s.getId().toString());
				sMap.put(ProjectSamplesDataTable.NAME, s.getSampleName());
				sMap.put(ProjectSamplesDataTable.NUM_FILES,
						String.valueOf(sequenceFileService.getSequenceFilesForSample(s).size()));
				sMap.put(ProjectSamplesDataTable.CREATED_DATE, Formats.DATE.format(join.getTimestamp()));
				samplesList.add(sMap);
			}
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_DATA, samplesList);
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_DRAW, draw);
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_RECORDS_FILTERED, page.getTotalElements());
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_RECORDS_TOTAL, page.getTotalElements());
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_SORT_COLUMN, sortColumn);
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_SORT_DIRECTION, sortDirection);
		} catch (Exception e) {
			logger.error("Error retrieving project sample information :" + e.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/ajax/{projectId}/samples/getids", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, List<String>> getAllProjectIds(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		List<String> sampleIdList = new ArrayList<>();
		List<Join<Project, Sample>> psj = sampleService.getSamplesForProject(project);
		for (Join<Project, Sample> join : psj) {
			sampleIdList.add(join.getObject().getId().toString());
		}
		Map<String, List<String>> result = new HashMap<>();
		result.put("ids", sampleIdList);
		return result;
	}

	/**
	 * Search for projects available for a user to copy samples to. If the user
	 * is an admin it will show all projects.
	 *
	 * @param projectId
	 *            The current project id
	 * @param term
	 *            A search term
	 * @param pageSize
	 *            The size of the page requests
	 * @param page
	 *            The page number (0 based)
	 * @param principal
	 *            The logged in user.
	 * @return a Map<String,Object> containing: total: total number of elements
	 *         results: A Map<Long,String> of project IDs and project names.
	 */
	@RequestMapping(value = "/ajax/{projectId}/samples/available_projects")
	@ResponseBody
	public Map<String, Object> getProjectsAvailableToCopySamples(@PathVariable Long projectId,
			@RequestParam String term, @RequestParam int pageSize, @RequestParam int page, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());

		Map<Long, String> vals = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		if (user.getAuthorities().contains(Role.ROLE_ADMIN)) {
			Page<Project> projects = projectService.search(ProjectSpecification.searchProjectName(term), page,
					pageSize, Direction.ASC, PROJECT_NAME_PROPERTY);
			for (Project p : projects) {
				vals.put(p.getId(), p.getName());
			}
			response.put("total", projects.getTotalElements());
		} else {
			// search for projects with a given name where the user is an owner
			Specification<ProjectUserJoin> spec = where(
					ProjectUserJoinSpecification.searchProjectNameWithUser(term, user)).and(
					ProjectUserJoinSpecification.getProjectJoinsWithRole(user, ProjectRole.PROJECT_OWNER));
			Page<ProjectUserJoin> projects = projectService.searchProjectUsers(spec, page, pageSize, Direction.ASC);
			for (ProjectUserJoin p : projects) {
				vals.put(p.getSubject().getId(), p.getSubject().getName());
			}
			response.put("total", projects.getTotalElements());
		}

		response.put("results", vals);

		return response;
	}

	/**
	 * Copy or move samples from one project to another
	 *
	 * @param projectId
	 *            The original project id
	 * @param sampleIds
	 *            The sample ids to move
	 * @param newProjectId
	 *            The new project id
	 * @param removeFromOriginal
	 *            true/false whether to remove the samples from the original
	 *            project
	 * @return A list of warnings
	 */
	@RequestMapping(value = "/ajax/{projectId}/samples/copy")
	@ResponseBody
	public Map<String, Object> copySampleToProject(@PathVariable Long projectId, @RequestParam List<Long> sampleIds,
			@RequestParam Long newProjectId, @RequestParam boolean removeFromOriginal) {
		Project originalProject = projectService.read(projectId);
		Project newProject = projectService.read(newProjectId);

		Map<String, Object> response = new HashMap<>();
		List<String> warnings = new ArrayList<>();

		int totalCopied = 0;

		for (Long sampleId : sampleIds) {
			Sample sample = sampleService.read(sampleId);
			try {
				projectService.addSampleToProject(newProject, sample);
				logger.trace("Copied sample " + sampleId + " to project " + newProjectId);
				totalCopied++;

			} catch (EntityExistsException ex) {
				logger.warn("Attempted to add sample " + sampleId + " to project " + newProjectId
						+ " where it already exists.", ex);

				warnings.add(sample.getLabel());
			}

			if (removeFromOriginal) {
				projectService.removeSampleFromProject(originalProject, sample);
				logger.trace("Removed sample " + sampleId + " from original project " + projectId);
			}
		}

		if (!warnings.isEmpty()) {
			response.put("warnings", warnings);
		}
		response.put("totalCopied", totalCopied);

		return response;
	}

	/**
	 * Remove a list of samples from a a Project.
	 *
	 * @param projectId
	 *            Id of the project to remove the samples from
	 * @param sampleIds
	 *            An array of samples to remove from a project
	 * @return Map containing either success or errors.
	 */
	@RequestMapping(value = "/ajax/{projectId}/samples/delete", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> deleteProjectSamples(@PathVariable Long projectId,
			@RequestParam List<Long> sampleIds) {
		Project project = projectService.read(projectId);
		Map<String, Object> result = new HashMap<>();
		for (Long id : sampleIds) {
			try {
				Sample sample = sampleService.read(id);
				projectService.removeSampleFromProject(project, sample);
			} catch (EntityNotFoundException e) {
				result.put("error", "Cannot find sample with id: " + id);
			}

		}
		result.put("success", "DONE!");
		return result;
	}

	/**
	 * For a list of sample ids, this function will generate a map of {id, name}
	 *
	 * @param sampleIds
	 *            A list of sample ids.
	 * @return A list of map of {id, name}
	 */
	@RequestMapping(value = "/ajax/getNamesFromIds", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, String>> ajaxGetSampleNamesFromIds(@RequestParam List<Long> sampleIds) {
		List<Map<String, String>> resultList = new ArrayList<>();
		for (Long id : sampleIds) {
			Map<String, String> results = new HashMap<>();
			Sample sample = sampleService.read(id);
			results.put("id", id.toString());
			results.put("text", sample.getSampleName());
			resultList.add(results);
		}
		return resultList;
	}

	/**
	 * Merges a list of samples into either the first sample in the list with a
	 * new name if provided, or into the selected sample based on the id.
	 *
	 * @param projectId
	 *            The id for the project the samples belong to.
	 * @param sampleIds
	 *            A list of sample ids for samples to merge.
	 * @param mergeSampleId
	 *            (Optional) The id of the sample to merge the other into.
	 * @param newName
	 *            (Optional) The new name for the final sample.
	 * @return
	 */
	@RequestMapping(value = "/ajax/{projectId}/samples/merge", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> ajaxSamplesMerge(@PathVariable Long projectId,
			@RequestParam List<Long> sampleIds, @RequestParam(required = false) Long mergeSampleId,
			@RequestParam(required = false) String newName) {
		Map<String, Object> result = new HashMap<>();
		Project project = projectService.read(projectId);
		Sample mergeIntoSample = null;
		// Determine if it is a new name or and existing sample
		try {
			if (sampleIds.contains(mergeSampleId)) {
				mergeIntoSample = sampleService.read(mergeSampleId);
				sampleIds.remove(mergeSampleId);
			} else {
				mergeIntoSample = sampleService.read(sampleIds.remove(0));
			}
		} catch (EntityNotFoundException e) {
			result.put("error", e.getLocalizedMessage());

		}
		// Rename if a new name is given
		if (!Strings.isNullOrEmpty(newName)) {
			Map<String, Object> updateMap = new HashMap<>();
			updateMap.put("sampleName", newName);
			try {
				mergeIntoSample = sampleService.update(mergeIntoSample.getId(), updateMap);
			} catch (ConstraintViolationException e) {
				result.put("error", getErrorsFromViolationException(e));
			}
		}
		if (!result.containsKey("error")) {
			Sample[] mergeSamples = new Sample[sampleIds.size()];
			for (int i = 0; i < sampleIds.size(); i++) {
				mergeSamples[i] = sampleService.read(sampleIds.get(i));
			}
			sampleService.mergeSamples(project, mergeIntoSample, mergeSamples);
			result.put("success", mergeIntoSample.getSampleName());
		}
		return result;
	}

	/**
	 * Changes a {@link ConstraintViolationException} to a usable map of strings
	 * for displaing in the UI.
	 *
	 * @param e
	 *            {@link ConstraintViolationException} for the form submitted.
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
