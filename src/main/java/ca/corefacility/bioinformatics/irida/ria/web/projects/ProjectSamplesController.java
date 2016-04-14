package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.ProjectSamplesDatatableUtils;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.ProjectSamplesFilterCriteria;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
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

	public static final String PROJECT_NAME_PROPERTY = "name";

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
			SequenceFileService sequenceFileService, ProjectControllerUtils projectControllerUtils,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
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
	 *            A model for the sample list view
	 * @param principal
	 *            The user reading the project
	 * @param projectId
	 *            The ID of the project
	 * @param httpSession
	 *            The user's session
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
		boolean haveGalaxyCallbackURL = (httpSession.getAttribute(ProjectsController.GALAXY_CALLBACK_VARIABLE_NAME) != null);
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
	 *            Id for the {@link Project} the sample will belong to.
	 * @param model
	 *            {@link Model}
	 * @return Name of the add sample page.
	 */
	@RequestMapping("/projects/{projectId}/samples/new")
	public String getCreateNewSamplePage(@PathVariable Long projectId, Model model) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		return "projects/project_add_sample";
	}

	/**
	 * Special method to add the correct linker script name to the modal
	 * template
	 *
	 * @param model
	 *            {@link Model}
	 *
	 * @return Location of the modal template
	 */
	@RequestMapping("/projects/templates/samples/linker")
	public String getLinkerModal(Model model) {
		model.addAttribute("scriptName", LINKER_SCRIPT);
		return PROJECT_TEMPLATE_DIR + "linker.tmpl";
	}

	/**
	 * Creates the modal to remove samples from a project.
	 *
	 * @param ids
	 * 		{@link List} of sample names to remove.
	 * @param model
	 * 		{@link Model}
	 *
	 * @return
	 */
	@RequestMapping("/projects/templates/remove-modal")
	public String getRemoveSamplesFromProjectModal(@RequestParam(name = "sampleIds[]") List<Long> ids, Model model) {
		List<Sample> samplesThatAreInMultiple = new ArrayList<>();
		List<Sample> samplesThatAreInOne = new ArrayList<>();
		List<Sample> extraMultiple = new ArrayList<>();
		List<Sample> extraSingle = new ArrayList<>();

		for (Long id : ids) {
			Sample sample = sampleService.read(id);
			List<Join<Project, Sample>> join = projectService.getProjectsForSample(sample);

			if (join.size() > 1) {
				if (samplesThatAreInMultiple.size() < 10) {
					samplesThatAreInMultiple.add(sample);
				} else {
					extraMultiple.add(sample);
				}
			} else {
				if (samplesThatAreInOne.size() < 10) {
					samplesThatAreInOne.add(sample);
				} else {
					extraSingle.add(sample);
				}
			}
		}

		model.addAttribute("samplesThatAreInMultiple", samplesThatAreInMultiple);
		model.addAttribute("samplesThatAreInOne", samplesThatAreInOne);
		model.addAttribute("extraMultiple", extraMultiple);
		model.addAttribute("extraSingle", extraSingle);
		return PROJECT_TEMPLATE_DIR + "remove-modal.tmpl";
	}

	/**
	 * Create a modal dialog to merge samples in a project.
	 *
	 * @param ids
	 * 		{@link List} List of {@link Long} identifiers for {@link Sample} to merge.
	 * @param model
	 * 		{@link Model}
	 *
	 * @return
	 */
	@RequestMapping("/projects/templates/merge-modal")
	public String getMergeSamplesInProjectModal(@RequestParam(name = "sampleIds[]") List<Long> ids, Model model) {
		List<Sample> samples = (List<Sample>) sampleService.readMultiple(ids);
		model.addAttribute("samples", samples);
		return PROJECT_TEMPLATE_DIR + "merge-modal.tmpl";
	}

	/**
	 * Create a modal dialog to copy samples to another project.
	 *
	 * @param ids
	 * 		{@link List} List of {@link Long} identifiers for {@link Sample} to merge.
	 * @param model
	 * 		{@link Model}
	 *
	 * @return
	 */
	@RequestMapping("/projects/templates/copy-modal")
	public String getCopySamplesModal(@RequestParam(name = "sampleIds[]") List<Long> ids, @RequestParam Long projectId, Model model) {
		model.addAllAttributes(generateCopyMoveSamplesContent(ids));
		model.addAttribute("projectId", projectId);
		return PROJECT_TEMPLATE_DIR + "copy-modal.tmpl";
	}

	/**
	 * Create a modal dialog to move samples to another project.
	 *
	 * @param ids
	 * 		{@link List} List of {@link Long} identifiers for {@link Sample} to merge.
	 * @param model
	 * 		{@link Model}
	 *
	 * @return
	 */
	@RequestMapping("/projects/templates/move-modal")
	public String getMoveSamplesModal(@RequestParam(name = "sampleIds[]") List<Long> ids, @RequestParam Long projectId, Model model) {
		model.addAllAttributes(generateCopyMoveSamplesContent(ids));
		model.addAttribute("projectId", projectId);
		return PROJECT_TEMPLATE_DIR + "move-modal.tmpl";
	}

	/**
	 * Generate a {@link Map} of {@link Sample} to move or copy.
	 * @param ids {@link Long} of ids for {@link Sample}
	 * @return
	 */
	public Map<String, List<Sample>> generateCopyMoveSamplesContent(List<Long> ids) {
		Map<String, List<Sample>> model = new HashMap<>();
		List<Sample> samples = (List<Sample>) sampleService.readMultiple(ids);
		List<Sample> extraSamples = new ArrayList<>();

		// Only initially need to display the first 10 samples.
		int end = samples.size();
		if (end > 9) {
			end = 9;
			extraSamples = samples.subList(end, samples.size());
		}

		model.put("samples", samples.subList(0, end));
		model.put("extraSamples", extraSamples);
		return model;
	}

	/**
	 * Get a list of all samples within the project
	 *
	 * @param projectId
	 *            The id for the current {@link Project}
	 *
	 * @return A list of {@link Sample} in the current project
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse<Map<String, Object>> getProjectSamples(@PathVariable Long projectId, @DatatablesParams DatatablesCriterias criterias, @RequestParam(required = false, defaultValue = "") List<Long> associated) {
		Map<String, Object> result = new HashMap<>();
		List<Project> projects = new ArrayList<>();

		// This project is always in the query.
		projects.add(projectService.read(projectId));

		// Check to see if any associated projects need to be added to the query.
		if (!associated.isEmpty()) {
			projects.addAll(associated.stream().map(projectService::read).collect(Collectors.toList()));
		}

		// Convert the criterias into a more usable format.
		ProjectSamplesDatatableUtils utils = new ProjectSamplesDatatableUtils(criterias);

		final Page<ProjectSampleJoin> page;
		if (Strings.isNullOrEmpty(utils.getSearch())) {
			// No search term, therefore filter on the attributes
			ProjectSamplesFilterCriteria filter = utils.getFilter();
			page = sampleService
					.getFilteredSamplesForProjects(projects, filter.getName(), null, null, utils.getCurrentPage(),
							utils.getPageSize(), utils.getSortDirection(), utils.getSortProperty());
		} else {
			// Generic search required.
			page = sampleService.getSearchedSamplesForProjects(projects, utils.getSearch(), utils.getCurrentPage(),
					utils.getPageSize(), utils.getSortDirection(), utils.getSortProperty());
		}

		// Create a more usable Map of the sample data.
		List<Map<String, Object>> sampleMap = new ArrayList<>();
		for (ProjectSampleJoin join : page.getContent()) {
			Project project = join.getSubject();
			SampleType type = projectId == project.getId() ? SampleType.LOCAL : SampleType.ASSOCIATED;
			Map<String, Object> map = getSampleMap(join.getObject(), project, type,
					join.getObject().getId());
			sampleMap.add(map);
		}

		DataSet<Map<String, Object>> dataSet = new DataSet<>(sampleMap, page.getTotalElements(), page.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}

	/**
	 * Search for projects available for a user to copy samples to. If the user
	 * is an admin it will show all projects.
	 *
	 * @param term
	 *            A search term
	 * @param pageSize
	 *            The size of the page requests
	 * @param page
	 *            The page number (0 based)
	 * @param principal
	 *            The logged in user.
	 *
	 * @return a {@code Map<String,Object>} containing: total: total number of
	 *         elements results: A {@code Map<Long,String>} of project IDs and
	 *         project names.
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/available_projects")
	@ResponseBody
	public Map<String, Object> getProjectsAvailableToCopySamples(final @PathVariable Long projectId, @RequestParam String term, @RequestParam int pageSize,
			@RequestParam int page, Principal principal) {
		final Project projectToExclude = projectService.read(projectId);
		List<Map<String, String>> projectMap = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();
		final Page<Project> projects = projectService.getUnassociatedProjects(projectToExclude, term, page, pageSize, Direction.ASC, PROJECT_NAME_PROPERTY);

		for (Project p : projects) {
			Map<String, String> map = new HashMap<>();
			map.put("identifier", p.getId().toString());
			map.put("text", p.getName());
			projectMap.add(map);
		}
		response.put("total", projects.getTotalElements());


		response.put("projects", projectMap);

		return response;
	}

	/**
	 * Copy or move samples from one project to another
	 *
	 * @param projectId
	 *            The original project id
	 * @param sampleIds
	 *            the sample identifiers to copy
	 * @param newProjectId
	 *            The new project id
	 * @param removeFromOriginal
	 *            true/false whether to remove the samples from the original
	 *            project
	 * @param locale
	 *            the locale specified by the browser.
	 *
	 * @return A list of warnings
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/copy", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> copySampleToProject(@PathVariable Long projectId,
			@RequestParam(value = "sampleIds[]") List<Long> sampleIds, @RequestParam Long newProjectId,
			@RequestParam(required = false) boolean remove, Locale locale) {
		Project originalProject = projectService.read(projectId);
		Project newProject = projectService.read(newProjectId);

		Map<String, Object> response = new HashMap<>();
		List<String> warnings = new ArrayList<>();
		List<Sample> successful = new ArrayList<>();

		for (Long sampleId : sampleIds) {
			Sample sample = sampleService.read(sampleId);
			try {

				if (remove) {
					projectService.moveSampleBetweenProjects(originalProject, newProject, sample);
				} else {
					projectService.addSampleToProject(newProject, sample);
				}

				logger.trace("Copied sample " + sampleId + " to project " + newProjectId);
				successful.add(sample);
			} catch (EntityExistsException ex) {
				logger.warn("Attempted to add sample " + sampleId + " to project " + newProjectId
						+ " where it already exists.");
				String msg = remove ? "project.samples.move.sample-exists" : "project.samples.copy.sample-exists";
				warnings.add(messageSource.getMessage(msg,
						new Object[] { sample.getSampleName(), newProject.getName() }, locale));
			}
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
				response.put(
						"message",
						messageSource.getMessage("project.samples.move-single-success-message", new Object[] {
								successful.get(0).getSampleName(), newProject.getName() }, locale));
			} else {
				response.put(
						"message",
						messageSource.getMessage("project.samples.copy-single-success-message", new Object[] {
								successful.get(0).getSampleName(), newProject.getName() }, locale));
			}
		}
		// 3. Multiple samples copied
		// 4. Multiple samples moved
		else if (successful.size() > 1) {
			if (remove) {
				response.put(
						"message",
						messageSource.getMessage("project.samples.move-multiple-success-message", new Object[] {
								successful.size(), newProject.getName() }, locale));
			} else {
				response.put(
						"message",
						messageSource.getMessage("project.samples.copy-multiple-success-message", new Object[] {
								successful.size(), newProject.getName() }, locale));
			}
		}

		response.put("successful", successful.stream().map((s) -> s.getId()).collect(Collectors.toList()));

		return response;
	}

	/**
	 * Remove a list of samples from a a Project.
	 *
	 * @param projectId
	 *            Id of the project to remove the samples from
	 * @param sampleIds
	 *            An array of samples to remove from a project
	 * @param locale
	 *            The locale of the web browser.
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
					messageSource.getMessage("project.samples.remove-success-singular", new Object[] { sample.getSampleName(), project.getLabel() }, locale));
		} else {
			result.put("message",
					messageSource.getMessage("project.samples.remove-success-plural", new Object[] { sampleIds.size(), project.getLabel() }, locale));
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
	 * Merges a list of samples into either the first sample in the list with a
	 * new name if provided, or into the selected sample based on the id.
	 *
	 * @param projectId
	 *            The id for the current {@link Project}
	 * @param mergeSampleId
	 *            An id for a {@link Sample} to merge the other samples into.
	 * @param sampleIds
	 *            A list of ids for {@link Sample} to merge together.
	 * @param newName
	 *            An optional new name for the {@link Sample}.
	 * @param locale
	 *            The {@link Locale} of the current user.
	 *
	 * @return a map of {@link Sample} properties representing the merged
	 *         sample.
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/merge", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> ajaxSamplesMerge(@PathVariable Long projectId,
			@RequestParam Long mergeSampleId, @RequestParam(value = "sampleIds[]") List<Long> sampleIds,
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
		result.put(
				"message",
				messageSource.getMessage("project.samples.combine-success", new Object[] { samplesMergeCount,
						mergeIntoSample.getSampleName() }, locale));
		return result;
	}

	/**
	 * Remove the given {@link Sample}s from the given {@link Project}
	 *
	 * @param projectId
	 *            ID of the project to remove from
	 * @param samples
	 *            {@link Sample} ids to remove
	 * @param locale
	 *            User's locale
	 *
	 * @return Map with success message
	 */
	@RequestMapping(value = "/projects/{projectId}/ajax/samples/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> removeSamplesFromProject(@PathVariable Long projectId,
			@RequestParam(value = "samples[]") List<Long> samples, Locale locale) {
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
				"message",
				messageSource.getMessage("project.samples.remove.success",
						new Object[] { samples.size(), project.getLabel() }, locale));

		return result;
	}

	/**
	 * Download a set of sequence files from selected samples within a project
	 *
	 * @param projectId
	 *            Id for a {@link Project}
	 * @param ids
	 *            List of ids ofr {@link Sample} within the project
	 * @param response
	 *            {@link HttpServletResponse}
	 *
	 * @throws IOException
	 *             if we fail to read a file from the filesystem.
	 */
	@RequestMapping(value = "/projects/{projectId}/download/files")
	public void downloadSamples(@PathVariable Long projectId, @RequestParam List<Long> ids, HttpServletResponse response)
			throws IOException {
		Project project = projectService.read(projectId);
		List<Sample> samples = (List<Sample>) sampleService.readMultiple(ids);

		// Add the appropriate headers
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + project.getName() + ".zip\"");
		response.setHeader("Transfer-Encoding", "chunked");

		// storing used filenames to ensure we don't have a conflict
		Set<String> usedFileNames = new HashSet<>();

		try (ZipOutputStream outputStream = new ZipOutputStream(response.getOutputStream())) {
			for (Sample sample : samples) {
				List<Join<Sample, SequenceFile>> sequenceFilesForSample = sequenceFileService
						.getSequenceFilesForSample(sample);
				for (Join<Sample, SequenceFile> join : sequenceFilesForSample) {
					Path path = join.getObject().getFile();
					StringBuilder name = new StringBuilder(project.getName());
					name.append("/").append(sample.getSampleName());
					name.append("/").append(path.getFileName().toString());

					String fileName = name.toString();
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
	 * Rename a filename {@code original} and ensure it doesn't exist in
	 * {@code usedNames}. Uses the windows style of renaming file.ext to file
	 * (1).ext
	 * 
	 * @param original
	 *            original file name
	 * @param usedNames
	 *            names that original must not conflict with
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
	 * Create a new {@link Sample} in a {@link Project}
	 *
	 * @param projectId
	 *            the ID of the {@link Project} to add to
	 * @param sample
	 *            The {@link Sample} to create
	 * @param response
	 *            {@link HttpServletResponse}
	 *
	 * @return Success status and id if successful, errors if not
	 */
	@RequestMapping(value = "/projects/{projectId}/samples", method = RequestMethod.POST)
	public Map<String, Object> createSampleInProject(@PathVariable Long projectId, @ModelAttribute Sample sample,
			HttpServletResponse response) {
		// get the project
		Project project = projectService.read(projectId);

		Map<String, Object> responseBody = new HashMap<>();

		// try to add the sample to the project
		Join<Project, Sample> addSampleToProject = null;
		try {
			addSampleToProject = projectService.addSampleToProject(project, sample);
			Long sampleId = addSampleToProject.getObject().getId();
			responseBody.put("sampleId", sampleId);
			response.setStatus(HttpStatus.CREATED.value());
		} catch (ConstraintViolationException ex) {
			// if errors respond with the errors
			Map<String, String> errorsFromViolationException = getErrorsFromViolationException(ex);
			responseBody.put("errors", errorsFromViolationException);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
		}

		return responseBody;
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
	 *            Object to identify the {@link Sample}. Local samples will be
	 *            the sample id, remote may be a URL
	 * @return a formatted map of {@link Sample} objects.
	 */
	public static Map<String, Object> getSampleMap(Sample sample, Project project, SampleType type, Object identifier) {
		Map<String, Object> sampleMap = new HashMap<>();
		sampleMap.put("sample", sample);
		sampleMap.put("project", project);
		sampleMap.put("sampleType", type);
		sampleMap.put("identifier", identifier);
		return sampleMap;
	}

	/**
	 * Changes a {@link ConstraintViolationException} to a usable map of strings
	 * for displaing in the UI.
	 *
	 * @param e
	 *            {@link ConstraintViolationException} for the form submitted.
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
	 * Type of sample being displayed in the project/samples page. This will be
	 * used to determine how to link to resources and add them to the cart.
	 */
	public enum SampleType {
		// samples in the local project
		LOCAL,
		// samples in associated projects
		ASSOCIATED

	}
}
