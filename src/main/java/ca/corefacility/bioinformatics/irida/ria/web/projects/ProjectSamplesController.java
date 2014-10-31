package ca.corefacility.bioinformatics.irida.ria.web.projects;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.Formatter;
import org.springframework.format.annotation.DateTimeFormat;
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
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleFilterSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectUserJoinSpecification;
import ca.corefacility.bioinformatics.irida.ria.components.ProjectSamplesCart;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

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
	public static final String PROJECT_TEMPLATE_DIR = PROJECTS_DIR + "templates/";
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
	private MessageSource messageSource;

	// Components
	private ProjectSamplesCart cart;

	/*
	 * Converters
	 */
	Formatter<Date> dateFormatter;
	FileSizeConverter fileSizeConverter;

	@Autowired
	public ProjectSamplesController(ProjectService projectService, SampleService sampleService,
			UserService userService, SequenceFileService sequenceFileService,
			ProjectControllerUtils projectControllerUtils, ProjectSamplesCart cart, MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
		this.sequenceFileService = sequenceFileService;
		this.projectControllerUtils = projectControllerUtils;
		this.dateFormatter = new DateFormatter();
		this.fileSizeConverter = new FileSizeConverter();
		this.messageSource = messageSource;
		this.cart = cart;
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
	 *
	 * @return Name of the project samples list view
	 */
	@RequestMapping("/{projectId}/samples")
	public String getProjectSamplesPage(final Model model, final Principal principal, @PathVariable long projectId) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		// Set up the template information
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_SAMPLES);
		return PROJECT_SAMPLES_PAGE;
	}

	/**
	 * Responsible for getting template fragments for the samples page. This is usually used for modal windows.
	 *
	 * @param name
	 * 		name of the template
	 *
	 * @return String path to the template
	 */
	@RequestMapping("/templates/{name}")
	public String getAngularTemplate(@PathVariable String name) {
		return PROJECT_TEMPLATE_DIR + name;
	}

	/**
	 * Get a paged list of samples.
	 *
	 * @param projectId
	 * 		Id for the project the samples are in.
	 * @param count
	 * 		The size of the list to return.
	 * @param page
	 * 		The page number currently viewed.
	 * @param sortDir
	 * 		The direction to sort the samples.
	 * @param sortedBy
	 * 		The column to sort the samples on.
	 * @param name
	 * 		Not required.  An expression to filter the name by.
	 * @param organism
	 * 		Not required. An expression to filter the organism by.
	 * @param minDate
	 * 		Not required.  The minimum date to filter by.
	 * @param maxDate
	 * 		Not required.  The maximum date to filter by.
	 *
	 * @return A map containing a list of pages samples.
	 */
	@RequestMapping(value = "/{projectId}/ajax/samples", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getProjectSamples(@PathVariable Long projectId,
			@RequestParam Integer count,
			@RequestParam Integer page,
			@RequestParam String sortDir,
			@RequestParam String sortedBy,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String organism,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date minDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date maxDate) {
		// Since the UI does not know about the structure of the database on Joins this map
		// is used to convert what the UI has with the actual name required for the specification to work.
		Map<String, String> sortLookUp = ImmutableMap.of(
				"name", "sample.sampleName",
				"organism", "sample.organism",
				"added", "createdDate"
		);
		sortedBy = sortLookUp.containsKey(sortedBy) ? sortLookUp.get(sortedBy) : sortedBy;
		Project project = projectService.read(projectId);
		Sort.Direction direction = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;

		Specification<ProjectSampleJoin> specification = ProjectSampleFilterSpecification
				.searchProjectSamples(project, name, organism, minDate, maxDate);
		Page<ProjectSampleJoin> projectSampleJoinPage = sampleService
				.searchProjectSamples(specification, page, count, direction, sortedBy);

		List<Map<String, Object>> samples = new ArrayList<>();
		int selectedCount = 0;
		for (Join<Project, Sample> join : projectSampleJoinPage.getContent()) {
			Sample sample = join.getObject();
			Map<String, Object> map = _generateUISample(projectId, sample);
			if (map.get("selected").equals(true)) {
				selectedCount++;
			}
			samples.add(map);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("selectCount", selectedCount);
		result.put("samples", samples);
		result.put("totalSamples", projectSampleJoinPage.getTotalElements());
		result.put("count", cart.getSelectedSamples(projectId).size());
		return result;
	}

	/**
	 * Search for projects available for a user to copy samples to. If the user is an admin it will show all projects.
	 *
	 * @param projectId
	 * 		The current project id
	 * @param term
	 * 		A search term
	 * @param pageSize
	 * 		The size of the page requests
	 * @param page
	 * 		The page number (0 based)
	 * @param principal
	 * 		The logged in user.
	 *
	 * @return a Map<String,Object> containing: total: total number of elements results: A Map<Long,String> of project
	 * IDs and project names.
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
	 * 		The original project id
	 * @param sampleIds
	 * 		The sample ids to move
	 * @param newProjectId
	 * 		The new project id
	 * @param removeFromOriginal
	 * 		true/false whether to remove the samples from the original project
	 *
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
	 * 		Id of the project to remove the samples from
	 * @param sampleIds
	 * 		An array of samples to remove from a project
	 *
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
	 * Merges a list of samples into either the first sample in the list with a new name if provided, or into the
	 * selected sample based on the id.
	 *
	 * @param mergeSampleId
	 * 		(Optional) The id of the sample to merge the other into.
	 * @param newName
	 * 		(Optional) The new name for the final sample.
	 *
	 * @return
	 */
	@RequestMapping(value = "/{projectId}/ajax/samples/merge", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> ajaxSamplesMerge(@PathVariable Long projectId,
			@RequestParam Long mergeSampleId,
			@RequestParam String newName, Locale locale) {
		Map<String, Object> result = new HashMap<>();
		Set<Long> sampleIds = cart.getSelectedSampleIds(projectId);
		int samplesMergeCount = sampleIds.size();
		Project project = projectService.read(projectId);
		// Determine which sample to merge into
		Sample mergeIntoSample = sampleService.read(mergeSampleId);
		sampleIds.remove(mergeSampleId);

		if (!Strings.isNullOrEmpty(newName)) {
			try {
				mergeIntoSample = sampleService.update(mergeSampleId, ImmutableMap.of("sampleName", newName));
			} catch (ConstraintViolationException e) {
				logger.error(e.getLocalizedMessage());
				result.put("result", "error");
				result.put("warnings", getErrorsFromViolationException(e));
				return result;
			}
		}

		// Create an update map
		Sample[] mergeSamples = new Sample[sampleIds.size()];
		int count = 0;
		for (Long sampleId : sampleIds) {
			mergeSamples[count++] = sampleService.read(sampleId);
		}

		// Merge the samples
		sampleService.mergeSamples(project, mergeIntoSample, mergeSamples);

		result.put("result", "success");
		result.put("message", messageSource.getMessage("project.samples.combine-success", new Object[] {
				samplesMergeCount,
				mergeIntoSample.getSampleName()
		}, locale));

		// Need to reset the cart
		cart.emptyProjectCart(projectId);
		return result;
	}

	/**
	 * Add a sample to the project sample cart
	 *
	 * @param sampleId
	 * 		Sample id to add to cart.
	 *
	 * @return The updated count for the number of samples in the project and the updated sample.
	 */
	@RequestMapping(value = "/{projectId}/ajax/samples/cart/add/sample", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> addSampleToCart(@PathVariable Long projectId,
			@RequestParam Long sampleId) {
		int count = this.cart.addSampleToCart(projectId, sampleId);
		Map<String, Object> response = new HashMap<>();
		response.put("count", count);
		response.put("sample", _generateUISample(projectId, sampleService.read(sampleId)));
		return response;
	}

	/**
	 * Remove a sample from the project sample cart.
	 *
	 * @param sampleId
	 * 		Id for the sample to remove.
	 *
	 * @return The updated count for the number of samples in the project and the updated sample.
	 */
	@RequestMapping(value = "/{projectId}/ajax/samples/cart/remove/sample", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> removeSampleFromCart(@PathVariable Long projectId,
			@RequestParam Long sampleId) {
		int count = this.cart.removeSampleFromCart(projectId, sampleId);
		Map<String, Object> response = new HashMap<>();
		response.put("count", count);
		response.put("sample", _generateUISample(projectId, sampleService.read(sampleId)));
		return response;
	}

	/**
	 * Add a file to a sample (only within the cart)
	 *
	 * @param sampleId
	 * 		Id for the sample that the file is within
	 * @param fileId
	 * 		Id for the file to omit
	 *
	 * @return The updated count for the number of samples in the project and the updated sample.
	 */
	@RequestMapping(value = "/{projectId}/ajax/samples/cart/add/file", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> addFileToCart(@PathVariable Long projectId, @RequestParam Long sampleId,
			@RequestParam Long fileId) {
		int count = this.cart.addFileToCart(projectId, sampleId, fileId);
		Map<String, Object> response = new HashMap<>();
		response.put("count", count);
		response.put("sample", _generateUISample(projectId, sampleService.read(sampleId)));
		return response;
	}

	/**
	 * Restore an removed file back to a sample
	 *
	 * @param sampleId
	 * 		Id for the sample that the file belong within
	 * @param fileId
	 * 		Id for the file to omit
	 *
	 * @return The updated count for the number of samples in the project and the updated sample.
	 */
	@RequestMapping(value = "/{projectId}/ajax/samples/cart/remove/file", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> removeFileFromCart(@PathVariable Long projectId,
			@RequestParam Long sampleId, @RequestParam Long fileId) {
		int count = this.cart.removeFileFromCart(projectId, sampleId, fileId);
		Map<String, Object> response = new HashMap<>();
		response.put("count", count);
		response.put("sample", _generateUISample(projectId, sampleService.read(sampleId)));
		return response;
	}

	/**
	 * Get a Map of Sample names and ids that are contained within the cart
	 *
	 * @return Map of sample names and ids
	 */
	@RequestMapping(value = "/{projectId}/ajax/samples/cart/names", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getSelectedSampleNamesAndIds(@PathVariable Long projectId) {
		List<Map<String, Object>> sampleList = new ArrayList<>();
		for (Long sampleId : cart.getSelectedSampleIds(projectId)) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", sampleId);
			map.put("name", sampleService.read(sampleId).getSampleName());
			sampleList.add(map);
		}
		return ImmutableMap.of("samples", sampleList);
	}

	/**
	 * Changes a {@link ConstraintViolationException} to a usable map of strings for displaing in the UI.
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

	/**
	 * Private method the create a sample which can be consumed by the UI.
	 *
	 * @param sample
	 * 		A {@link Sample}
	 *
	 * @return
	 */
	private Map<String, Object> _generateUISample(Long projectId, Sample sample) {
		Map<String, Object> map = new HashMap<>();
		boolean sampleSelected = cart.isSampleInCart(projectId, sample.getId());
		map.put("selected", sampleSelected);
		map.put("id", sample.getId().toString());
		map.put("name", sample.getSampleName());
		map.put("organism", sample.getOrganism());
		map.put("created", sample.getCreatedDate());

		List<Join<Sample, SequenceFile>> fileJoin = sequenceFileService.getSequenceFilesForSample(sample);
		List<Map<String, Object>> files = new ArrayList<>();
		int selectCount = 0;
		for (Join<Sample, SequenceFile> join1 : fileJoin) {
			SequenceFile f = join1.getObject();
			Map<String, Object> m = new HashMap<>();
			m.put("id", f.getId());
			boolean selected = f.getId() != null && cart.isFileInCart(projectId, sample.getId(), f.getId());
			if (selected)
				selectCount++;
			m.put("selected", selected);
			m.put("name", f.getLabel());
			Long realSize = 0L;
			Path path = f.getFile();
			if (Files.exists(path)) {
				try {
					realSize = Files.size(path);
				} catch (IOException e) {
					logger.error(e.getMessage());
					realSize = 0L;
				}
			}
			String size = fileSizeConverter.convert(realSize);
			m.put("size", size);
			files.add(m);
		}
		map.put("files", files);
		map.put("indeterminate", sampleSelected && files.size() != 0 && selectCount != files.size());
		return map;
	}
}
