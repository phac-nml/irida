package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProject;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.util.TreeNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Controller for project related views
 */
@Controller
@Scope("session")
public class ProjectsController {
	// Page Names
	public static final String PROJECTS_DIR = "projects/";
	public static final String LIST_PROJECTS_PAGE = PROJECTS_DIR + "projects";
	public static final String SYNC_NEW_PROJECT_PAGE = PROJECTS_DIR + "project_sync";
	public static final String CREATE_NEW_PROJECT_PAGE = PROJECTS_DIR + "project_new";

	// Services
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final UserService userService;
	private final ProjectControllerUtils projectControllerUtils;
	private final TaxonomyService taxonomyService;
	private final MessageSource messageSource;

	/*
	 * Converters
	 */ Formatter<Date> dateFormatter;
	FileSizeConverter fileSizeConverter;

	// CONSTANTS
	private final List<Map<String, String>> EXPORT_TYPES = ImmutableList
			.of(ImmutableMap.of("format", "xlsx", "name", "Excel"), ImmutableMap.of("format", "csv", "name", "CSV"));

	@Autowired
	public ProjectsController(ProjectService projectService, SampleService sampleService, UserService userService,
			ProjectControllerUtils projectControllerUtils, TaxonomyService taxonomyService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
		this.projectControllerUtils = projectControllerUtils;
		this.taxonomyService = taxonomyService;
		this.dateFormatter = new DateFormatter();
		this.messageSource = messageSource;
		this.fileSizeConverter = new FileSizeConverter();
	}

	/**
	 * Request for the page to display a list of all projects available to the
	 * currently logged in user.
	 *
	 * @param model
	 *            The model to add attributes to for the template.
	 * @return The name of the page.
	 */
	@RequestMapping("/projects")
	public String getProjectsPage(Model model) {
		model.addAttribute("ajaxURL", "/projects/ajax/list");
		model.addAttribute("exportTypes", EXPORT_TYPES);
		model.addAttribute("isAdmin", false);

		return LIST_PROJECTS_PAGE;
	}

	/**
	 * Get the admin projects page.
	 *
	 * @param model
	 *            {@link Model}
	 * @return The name of the page
	 */
	@RequestMapping("/projects/all")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public String getAllProjectsPage(Model model) {
		model.addAttribute("ajaxURL", "/projects/admin/ajax/list");
		model.addAttribute("isAdmin", true);
		model.addAttribute("exportTypes", EXPORT_TYPES);
		return LIST_PROJECTS_PAGE;
	}

	/**
	 * Get the samples for a given project
	 *
	 * @param model     A model for the sample list view
	 * @param principal The user reading the project
	 * @param projectId The ID of the project
	 * @return Name of the project samples list view
	 */
	@RequestMapping(value = {
			"/projects/{projectId}",
			"/projects/{projectId}/samples", })
	public String getProjectSamplesPage(final Model model, final Principal principal, @PathVariable long projectId) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		// Set up the template information
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/project_samples";
	}

	/**
	 * Request for a specific project details page.
	 *
	 * @param projectId
	 *            The id for the project to show details for.
	 * @param model
	 *            Spring model to populate the html page.
	 * @param principal
	 *            a reference to the logged in user.
	 * @return The name of the project details page.
	 */
	@RequestMapping(value = "/projects/{projectId}/activity")
	public String getProjectActivityPage(@PathVariable Long projectId, final Model model, final Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/project_activity";
	}

	/**
	 * Get the page to synchronize remote projects
	 *
	 * @return Name of the project sync page
	 */
	@RequestMapping(value = "/projects/synchronize", method = RequestMethod.GET)
	public String getSynchronizeProjectPage() {
		return SYNC_NEW_PROJECT_PAGE;
	}

	/**
	 * Get the page to share samples between projects
	 *
	 * @param projectId
	 *            Identifier for the current project
	 * @param model
	 *            Spring model for template variables
	 * @param principal
	 *            Currently logged in user
	 * @return Path to the template for sharing samples
	 */
	@RequestMapping("/projects/{projectId}/share")
	public String getProjectsSharePage(@PathVariable Long projectId, final Model model, final Principal principal) {
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/project_share";
	}

	/**
	 * Get the page for analyses shared with a given {@link Project}
	 *
	 * @param projectId
	 *            the ID of the {@link Project}
	 * @param principal
	 *            the logged in user
	 * @param model
	 *            model for view variables
	 * @return name of the analysis view page
	 */
	@RequestMapping("/projects/{projectId}/analyses/**")
	public String getProjectAnalysisList(@PathVariable Long projectId, Principal principal, Model model) {
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/project_analyses";
	}

	/**
	 * Get the project settings page
	 *
	 * @param projectId
	 *            - identifier for the {@link Project} currently being viewed
	 * @param principal
	 *            - Currently logged in used
	 * @param model
	 *            Spring UI model
	 * @return path to the html settings page
	 */
	@GetMapping("/projects/{projectId}/settings/**")
	public String getProjectSettingsPage(@PathVariable Long projectId, Principal principal, Model model) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		model.addAttribute("page", "details");
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/project_settings";
	}

	/**
	 * Search for taxonomy terms. This method will return a map of found
	 * taxonomy terms and their child nodes.
	 * <p>
	 * Note: If the search term was not included in the results, it will be
	 * added as an option
	 *
	 * @param searchTerm
	 *            The term to find taxa for
	 * @return A {@code List<Map<String,Object>>} which will contain a taxonomic
	 *         tree of matching terms
	 */
	@RequestMapping("/projects/ajax/taxonomy/search")
	@ResponseBody
	public List<Map<String, Object>> searchTaxonomy(@RequestParam String searchTerm) {
		Collection<TreeNode<String>> search = taxonomyService.search(searchTerm);

		TreeNode<String> searchTermNode = new TreeNode<>(searchTerm);
		// add a property to this node to indicate that it's the search term
		searchTermNode.addProperty("searchTerm", true);

		List<Map<String, Object>> elements = new ArrayList<>();

		// get the search term in first if it's not there yet
		if (!search.contains(searchTermNode)) {
			elements.add(transformTreeNode(searchTermNode));
		}

		for (TreeNode<String> node : search) {
			Map<String, Object> transformTreeNode = transformTreeNode(node);
			elements.add(transformTreeNode);
		}
		return elements;
	}

	/**
	 * Export Projects table as either an excel file or CSV
	 *
	 * @param type
	 *            of file to export (csv or excel)
	 * @param isAdmin
	 *            if the currently logged in user is an administrator
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param principal
	 *            {@link Principal}
	 * @param locale
	 *            {@link Locale}
	 * @throws IOException
	 *             thrown if cannot open the {@link HttpServletResponse}
	 *             {@link OutputStream}
	 */
	@RequestMapping("/projects/ajax/export")
	public void exportProjectsToFile(@RequestParam(value = "dtf") String type,
			@RequestParam(required = false, defaultValue = "false", value = "admin") Boolean isAdmin,
			HttpServletResponse response, Principal principal, Locale locale) throws IOException {
		// Let's make sure the export type is set properly
		if (!(type.equalsIgnoreCase("xlsx") || type.equalsIgnoreCase("csv"))) {
			throw new IllegalArgumentException(
					"No file type sent for downloading all projects.  Expecting parameter 'dtf=' xlsx or csv");
		}

		List<Project> projects;
		// If viewing the admin projects page give the user all the projects.
		if (isAdmin) {
			projects = (List<Project>) projectService.findAll();
		}
		// If on the users projects page, give the user their projects.
		else {
			User user = userService.getUserByUsername(principal.getName());
			projects = projectService.getProjectsForUser(user).stream().map(Join::getSubject)
					.collect(Collectors.toList());
		}

		List<DTProject> dtProjects = projects.stream().map(this::createDataTablesProject).collect(Collectors.toList());
		List<String> headers = ImmutableList
				.of("ProjectsTable_th_id", "ProjectsTable_th_name", "ProjectsTable_th_organism",
						"ProjectsTable_th_samples", "ProjectsTable_th_created_date", "ProjectsTable_th_modified_date")
				.stream().map(h -> messageSource.getMessage(h, new Object[] {}, locale)).collect(Collectors.toList());

		// Create the filename
		Date date = new Date();
		DateFormat fileDateFormat = new SimpleDateFormat(messageSource.getMessage("date.iso-8601", null, locale));
		String filename = "IRIDA_projects_" + fileDateFormat.format(date);

		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "." + type + "\"");
		if (type.equals("xlsx")) {
			writeProjectsToExcelFile(headers, dtProjects, locale, response);
		} else {
			writeProjectsToCsvFile(headers, dtProjects, locale, response);
		}
	}

	/**
	 * Handle the page request to upload {@link Sample} metadata
	 *
	 * @param model
	 *            {@link Model}
	 * @param projectId
	 *            {@link Long} identifier for the current {@link Project}
	 * @param principal
	 *            {@link Principal} currently logged in use
	 * @return {@link String} the path to the metadata import page
	 */
	@GetMapping("/projects/{projectId}/sample-metadata/upload/*")
	public String getProjectSamplesMetadataUploadPage(final Model model, @PathVariable Long projectId,
			Principal principal) {
		projectControllerUtils.getProjectTemplateDetails(model, principal, projectService.read(projectId));
		return "projects/project_samples_metadata_upload";
	}

	/**
	 * Write the projects as a CSV file
	 *
	 * @param headers
	 *            {@link List} for {@link String} headers for the information.
	 * @param projects
	 *            {@link List} of {@link DTProject} to export
	 * @param locale
	 *            {@link Locale}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @throws IOException
	 *             Thrown if cannot get the {@link PrintWriter} for the response
	 */
	private void writeProjectsToCsvFile(List<String> headers, List<DTProject> projects, Locale locale,
			HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();
		try (CSVPrinter printer = new CSVPrinter(writer,
				CSVFormat.DEFAULT.withRecordSeparator(System.lineSeparator()))) {
			printer.printRecord(headers);

			DateFormat dateFormat = new SimpleDateFormat(messageSource.getMessage("locale.date.long", null, locale));
			for (DTProject p : projects) {
				List<String> record = new ArrayList<>();
				record.add(String.valueOf(p.getId()));
				record.add(p.getName());
				record.add(p.getOrganism());
				record.add(String.valueOf(p.getSamples()));
				record.add(dateFormat.format(p.getCreatedDate()));
				record.add(dateFormat.format(p.getModifiedDate()));
				printer.printRecord(record);
			}
			printer.flush();
		}
	}

	/**
	 * Write the projects as a Excel file
	 *
	 * @param headers
	 *            {@link List} for {@link String} headers for the information.
	 * @param projects
	 *            {@link List} of {@link DTProject} to export
	 * @param locale
	 *            {@link Locale}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @throws IOException
	 *             Thrown if cannot get the {@link OutputStream} for the
	 *             response
	 */
	private void writeProjectsToExcelFile(List<String> headers, List<DTProject> projects, Locale locale,
			HttpServletResponse response) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		int rowCount = 0;

		// Create the headers
		Row headerRow = sheet.createRow(rowCount++);
		for (int cellCount = 0; cellCount < headers.size(); cellCount++) {
			Cell cell = headerRow.createCell(cellCount);
			cell.setCellValue(headers.get(cellCount));
		}

		// Create the rest of the sheet
		DateFormat dateFormat = new SimpleDateFormat(messageSource.getMessage("locale.date.long", null, locale));
		for (DTProject p : projects) {
			Row row = sheet.createRow(rowCount++);
			int cellCount = 0;
			row.createCell(cellCount++).setCellValue(String.valueOf(p.getId()));
			row.createCell(cellCount++).setCellValue(p.getName());
			row.createCell(cellCount++).setCellValue(p.getOrganism());
			row.createCell(cellCount++).setCellValue(String.valueOf(p.getSamples()));
			row.createCell(cellCount++).setCellValue(dateFormat.format(p.getCreatedDate()));
			row.createCell(cellCount).setCellValue(dateFormat.format(p.getModifiedDate()));
		}

		// Write the file
		try (OutputStream stream = response.getOutputStream()) {
			workbook.write(stream);
			stream.flush();
		}

		workbook.close();
	}

	/**
	 * Handle a {@link ProjectWithoutOwnerException} error. Returns a forbidden
	 * error
	 *
	 * @param ex
	 *            the exception to handle.
	 * @return response entity with FORBIDDEN error
	 */
	@ExceptionHandler(ProjectWithoutOwnerException.class)
	@ResponseBody
	public ResponseEntity<String> roleChangeErrorHandler(Exception ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	/**
	 * }
	 * <p>
	 * /** Recursively transform a {@link TreeNode} into a json parsable map
	 * object
	 *
	 * @param node
	 *            The node to transform
	 * @return A Map<String,Object> which may contain more children
	 */
	private Map<String, Object> transformTreeNode(TreeNode<String> node) {
		Map<String, Object> current = new HashMap<>();

		// add the node properties to the map
		for (Entry<String, Object> property : node.getProperties().entrySet()) {
			current.put(property.getKey(), property.getValue());
		}

		current.put("id", node.getValue());
		current.put("text", node.getValue());

		List<Object> children = new ArrayList<>();
		for (TreeNode<String> child : node.getChildren()) {
			Map<String, Object> transformTreeNode = transformTreeNode(child);
			children.add(transformTreeNode);
		}

		if (!children.isEmpty()) {
			current.put("children", children);
		}

		return current;
	}

	/**
	 * Extract the details of the a {@link Project} into a {@link DTProject}
	 * which is consumable by the UI
	 *
	 * @param project
	 *            {@link Project}
	 * @return {@link DTProject}
	 */
	private DTProject createDataTablesProject(Project project) {
		return new DTProject(project, sampleService.getNumberOfSamplesForProject(project));
	}
}
