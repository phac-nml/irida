package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
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
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectUserJoinSpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.upload.galaxy.GalaxyUploadService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

@Controller
public class ProjectSamplesController {
	// From configuration.properties
	private @Value("${ngsarchive.linker.available}") Boolean LINKER_AVAILABLE;
	private @Value("${ngsarchive.linker.script}") String LINKER_SCRIPT;

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
	private final GalaxyUploadService galaxyUploadService;
	private final SequenceFileService sequenceFileService;
	private final ProjectControllerUtils projectControllerUtils;
	private MessageSource messageSource;

	/*
	 * Converters
	 */
	Formatter<Date> dateFormatter;
	FileSizeConverter fileSizeConverter;

	@Autowired
	public ProjectSamplesController(ProjectService projectService, SampleService sampleService,
			UserService userService, GalaxyUploadService galaxyUploadService, SequenceFileService sequenceFileService,
			ProjectControllerUtils projectControllerUtils, MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
		this.galaxyUploadService = galaxyUploadService;
		this.sequenceFileService = sequenceFileService;
		this.projectControllerUtils = projectControllerUtils;
		this.dateFormatter = new DateFormatter();
		this.fileSizeConverter = new FileSizeConverter();
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
	 *
	 * @return Name of the project samples list view
	 */
	@RequestMapping("/projects/{projectId}/samples")
	public String getProjectSamplesPage(final Model model, final Principal principal, @PathVariable long projectId) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		// Set up the template information
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		// Exporting functionality
		model.addAttribute("linkerAvailable", LINKER_AVAILABLE);
		model.addAttribute("galaxyConfigured", galaxyUploadService.isConfigured());
		model.addAttribute("galaxyConnected", galaxyUploadService.isConnected());

		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_SAMPLES);
		return PROJECT_SAMPLES_PAGE;
	}

	/**
	 * Special method to add the correct linker script name to the modal template
	 *
	 * @param model
	 * 		{@link Model}
	 *
	 * @return Location of the modal template
	 */
	@RequestMapping("/projects/templates/samples/linker")
	public String getLinkerModal(Model model) {
		model.addAttribute("scriptName", LINKER_SCRIPT);
		return PROJECT_TEMPLATE_DIR + "linker.tmpl";
	}

	/**
	 * Special method to add Galaxy specific details to the modal template
	 *
	 * @param model
	 * 		{@link Model}
	 * @param principal
	 * 		Current User
	 * @param projectId
	 * 		Id for the current {@link Project}
	 *
	 * @return Location of the modal template
	 */
	@RequestMapping("/projects/{projectId}/templates/samples/galaxy")
	public String getGalaxyModal(Model model, Principal principal, @PathVariable Long projectId) {
		model.addAttribute("email", userService.getUserByUsername(principal.getName()).getEmail());
		model.addAttribute("name", projectService.read(projectId).getName() + "-" + principal.getName());
		return PROJECT_TEMPLATE_DIR + "galaxy.tmpl";
	}

	/**
	 * Get a list of all samples within the project
	 *
	 * @param projectId
	 * 		The id for the current {@link Project}
	 *
	 * @return A list of {@link Sample} in the current project
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getProjectSamples(@PathVariable Long projectId) {
		Map<String, Object> result = new HashMap<>();
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> joinList = sampleService.getSamplesForProject(project);
		List<Map<String,Object>> samples = new ArrayList<>(joinList.size());
		for (Join<Project, Sample> join : joinList) {
			samples.add(getSampleMap(join.getObject(), join.getSubject(), SampleType.LOCAL, join.getObject().getId()));
		}
		result.put("samples", samples);
		return result;
	}

	/**
	 * Search for projects available for a user to copy samples to. If the user is an admin it will show all projects.
	 *
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
	@RequestMapping(value = "/projects/ajax/samples/available_projects")
	@ResponseBody
	public Map<String, Object> getProjectsAvailableToCopySamples(@RequestParam String term, @RequestParam int pageSize,
			@RequestParam int page, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());

		List<Map<String, String>> projectMap = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();
		if (user.getAuthorities().contains(Role.ROLE_ADMIN)) {
			Page<Project> projects = projectService.search(ProjectSpecification.searchProjectName(term), page,
					pageSize, Direction.ASC, PROJECT_NAME_PROPERTY);
			for (Project p : projects) {
				Map<String, String> map = new HashMap<>();
				map.put("id", p.getId().toString());
				map.put("text", p.getName());
				projectMap.add(map);
			}
			response.put("total", projects.getTotalElements());
		} else {
			// search for projects with a given name where the user is an owner
			Specification<ProjectUserJoin> spec = Specifications.where(
					ProjectUserJoinSpecification.searchProjectNameWithUser(term, user)).and(
					ProjectUserJoinSpecification.getProjectJoinsWithRole(user, ProjectRole.PROJECT_OWNER));
			Page<ProjectUserJoin> projects = projectService.searchProjectUsers(spec, page, pageSize, Direction.ASC);
			for (ProjectUserJoin projectUserJoin : projects) {
				Project p = projectUserJoin.getSubject();
				Map<String, String> map = new HashMap<>();
				map.put("id", p.getId().toString());
				map.put("text", p.getName());
				projectMap.add(map);
			}
			response.put("total", projects.getTotalElements());
		}

		response.put("projects", projectMap);

		return response;
	}

	/**
	 * Copy or move samples from one project to another
	 *
	 * @param projectId
	 * 		The original project id
	 * @param newProjectId
	 * 		The new project id
	 * @param removeFromOriginal
	 * 		true/false whether to remove the samples from the original project
	 *
	 * @return A list of warnings
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/copy", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> copySampleToProject(@PathVariable Long projectId,
			@RequestParam(value = "sampleIds[]") List<Long> sampleIds,
			@RequestParam Long newProjectId, @RequestParam boolean removeFromOriginal, Locale locale) {
		Project originalProject = projectService.read(projectId);
		Project newProject = projectService.read(newProjectId);

		Map<String, Object> response = new HashMap<>();
		List<String> warnings = new ArrayList<>();
		List<String> successNames = new ArrayList<>();

		for (Long sampleId : sampleIds) {
			Sample sample = sampleService.read(sampleId);
			try {
				projectService.addSampleToProject(newProject, sample);
				logger.trace("Copied sample " + sampleId + " to project " + newProjectId);
				successNames.add(sample.getSampleName());
			} catch (EntityExistsException ex) {
				logger.warn("Attempted to add sample " + sampleId + " to project " + newProjectId
						+ " where it already exists.");

				warnings.add(messageSource.getMessage("project.samples.copy-error-message",
						new Object[] { sample.getSampleName(), newProject.getName() }, locale));
			}

			if (removeFromOriginal) {
				projectService.removeSampleFromProject(originalProject, sample);
				logger.trace("Removed sample " + sampleId + " from original project " + projectId);

			}
		}

		if (!warnings.isEmpty() || successNames.size() == 0) {
			response.put("result", "warning");
			response.put("warnings", warnings);
		} else {
			response.put("result", "success");
		}
		// 1. Only one sample copied
		// 2. Only one sample moved
		if (successNames.size() == 1) {
			if (removeFromOriginal) {
				response.put("message", messageSource.getMessage("project.samples.move-single-success-message",
						new Object[] { successNames.get(0), newProject.getName() }, locale));
			} else {
				response.put("message", messageSource.getMessage("project.samples.copy-single-success-message",
						new Object[] { successNames.get(0), newProject.getName() }, locale));
			}
		}
		// 3. Multiple samples copied
		// 4. Multiple samples moved
		else if (successNames.size() > 1) {
			if (removeFromOriginal) {
				response.put("message", messageSource.getMessage("project.samples.move-multiple-success-message",
						new Object[] { successNames.size(), newProject.getName() }, locale));
			} else {
				response.put("message", messageSource.getMessage("project.samples.copy-multiple-success-message",
						new Object[] { successNames.size(), newProject.getName() }, locale));
			}
		}
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
	@RequestMapping(value = "/projects/ajax/{projectId}/samples/delete", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
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
	 * @param projectId
	 * 		The id for the current {@link Project}
	 * @param mergeSampleId
	 * 		An id for a {@link Sample} to merge the other samples into.
	 * @param sampleIds
	 * 		A list of ids for {@link Sample} to merge together.
	 * @param newName
	 * 		An optional new name for the {@link Sample}.
	 * @param locale
	 * 		The {@link Locale} of the current user.
	 *
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/merge", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> ajaxSamplesMerge(@PathVariable Long projectId,
			@RequestParam Long mergeSampleId,
			@RequestParam(value = "sampleIds[]") List<Long> sampleIds,
			@RequestParam String newName, Locale locale) {
		Map<String, Object> result = new HashMap<>();
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
		return result;
	}

	/**
	 * Remove the given {@link Sample}s from the given {@link Project}
	 * 
	 * @param projectId
	 *            ID of the project to remove from
	 * @param sampleIds
	 *            {@link Sample} ids to remove
	 * @param locale
	 *            User's locale
	 * @return Map with success message
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> removeSamplesFromProject(@PathVariable Long projectId,
			@RequestParam(value = "samples[]") List<Long> samples, Locale locale) {
		Map<String, Object> result = new HashMap<>();
		
		//read the project
		Project project = projectService.read(projectId);
		
		//get the samples
		Iterable<Sample> readMultiple = sampleService.readMultiple(samples);
		
		//remove all samples
		projectService.removeSamplesFromProject(project, readMultiple);

		//build success message
		result.put("result", "success");
		result.put(
				"message",
				messageSource.getMessage("project.samples.remove.success",
						new Object[] { samples.size(), project.getLabel() }, locale));

		return result;
	}

	/**
	 * Download a set of sequence files from selected samples within a project
	 *
	 * @param projectId
	 * 		Id for a {@link Project}
	 * @param ids
	 * 		List of ids ofr {@link Sample} within the project
	 * @param response
	 * 		{@link HttpServletResponse}
	 *
	 * @throws IOException
	 */
	@RequestMapping(value = "/projects/{projectId}/download/files")
	public void downloadSamples(@PathVariable Long projectId, @RequestParam List<Long> ids,
			HttpServletResponse response) throws IOException {
		Project project = projectService.read(projectId);
		List<Sample> samples = (List<Sample>) sampleService.readMultiple(ids);

		// Add the appropriate headers
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + project.getName() + ".zip\"");
		response.setHeader("Transfer-Encoding", "chunked");

		try (ZipOutputStream outputStream = new ZipOutputStream(response.getOutputStream())) {
			for (Sample sample : samples) {
				List<Join<Sample, SequenceFile>> sequenceFilesForSample = sequenceFileService
						.getSequenceFilesForSample(sample);
				for (Join<Sample, SequenceFile> join : sequenceFilesForSample) {
					Path path = join.getObject().getFile();
					StringBuilder name = new StringBuilder(project.getName());
					name.append("/").append(sample.getSampleName());
					name.append("/").append(path.getFileName().toString());

					outputStream.putNextEntry(new ZipEntry(name.toString()));

					Files.copy(path, outputStream);

					outputStream.closeEntry();
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
	 * Export samples to the local instance of galaxy
	 *
	 * @param projectId
	 * 		Id for the current {@link Project}
	 * @param email
	 * 		Email address for the current user
	 * @param name
	 * 		Name of the current user
	 * @param request
	 * 		{@link HttpServletRequest}
	 *
	 * @return A JSON object containing the current completion status (TODO: Include a way to get an updated status.)
	 */
	@RequestMapping(value = "/{projectId}/ajax/samples/galaxy/upload", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> postUploadSampleToGalaxy(@PathVariable Long projectId,
			@RequestParam String email, @RequestParam String name,
			@RequestParam(value = "sampleIds[]") List<Long> sampleIds, HttpServletRequest request, Locale locale) {

		List<Sample> samples = (List<Sample>) sampleService.readMultiple(sampleIds);
		UploadWorker worker = null;
		Map<String, Object> result = new HashMap<>();
		try {
			worker = galaxyUploadService
					.performUploadSelectedSamples(new HashSet<>(samples), new GalaxyProjectName(name),
							new GalaxyAccountEmail(email));
			String sessionAttr = "gw-" + UUID.randomUUID();
			request.getSession().setAttribute(sessionAttr, worker);
			result.put("result", "success");
			result.put("sessionAttr", sessionAttr);
			result.put("msg", messageSource.getMessage("galaxy.success", new Object[] { samples.size() }, locale));
		} catch (ConstraintViolationException e) {
			result.put("result", "errors");
			result.put("errors", messageSource.getMessage("galaxy.error", new Object[] { }, locale));
		} catch (RuntimeException e) {
			// This should only occur if there is no instance of Galaxy
			result.put("result", "errors");
			result.put("errors", messageSource.getMessage("galaxy.not-configured", new Object[]{}, locale));
		}
		return result;
	}
	
	/**
	 * Get the Map format of {@link Sample}s to return for the project/samples
	 * page
	 * 
	 * @param sample
	 *            The sample to display
	 * @param project
	 *            The originating project
	 * @param type
	 *            The {@link SampleType} of the sample (LOCAL, ASSOCIATED)
	 * @param identifier
	 *            Number to identify the {@link Sample}. NOTE: This will be
	 *            different for remote samples
	 * @return
	 */
	public static Map<String, Object> getSampleMap(Sample sample, Project project, SampleType type, Number identifier) {
		Map<String, Object> sampleMap = new HashMap<>();
		sampleMap.put("sample", sample);
		sampleMap.put("project", project);
		sampleMap.put("sampleType", type);
		sampleMap.put("id", identifier);

		return sampleMap;
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
	 * Type of sample being returned
	 * 
	 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
	 *
	 */
	public enum SampleType {
		// samples in the local project
		LOCAL,
		// samples in associated projects
		ASSOCIATED;
	}
}
