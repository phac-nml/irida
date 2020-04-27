package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProject;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Controller
@RequestMapping("/projects")
public class ProjectListingController {
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final UserService userService;
	private final MessageSource messageSource;
	// CONSTANTS
	private final List<Map<String, String>> EXPORT_TYPES = ImmutableList.of(
			ImmutableMap.of("format", "xlsx", "name", "Excel"), ImmutableMap.of("format", "csv", "name", "CSV"));

	@Autowired
	public ProjectListingController(ProjectService projectService, SampleService sampleService, UserService userService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
		this.messageSource = messageSource;
	}

	/**
	 * Request for the page to display a list of all projects available to the currently logged in user.
	 *
	 * @param model The model to add attributes to for the template.
	 * @return The name of the page.
	 */
	@RequestMapping("")
	public String getProjectsPage(Model model) {
		model.addAttribute("ajaxURL", "/projects/ajax/list");
		model.addAttribute("exportTypes", EXPORT_TYPES);
		model.addAttribute("isAdmin", false);

		return "projects/projects";
	}

	/**
	 * Get the admin projects page.
	 *
	 * @param model {@link Model}
	 * @return The name of the page
	 */
	@RequestMapping("/all")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public String getAllProjectsPage(Model model) {
		model.addAttribute("ajaxURL", "/projects/admin/ajax/list");
		model.addAttribute("isAdmin", true);
		model.addAttribute("exportTypes", EXPORT_TYPES);
		return "projects/projects";
	}

	/**
	 * Export Projects table as either an excel file or CSV
	 *
	 * @param type      of file to export (csv or excel)
	 * @param isAdmin   if the currently logged in user is an administrator
	 * @param response  {@link HttpServletResponse}
	 * @param principal {@link Principal}
	 * @param locale    {@link Locale}
	 * @throws IOException thrown if cannot open the {@link HttpServletResponse} {@link OutputStream}
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
			projects = projectService.getProjectsForUser(user)
					.stream()
					.map(Join::getSubject)
					.collect(Collectors.toList());
		}

		List<DTProject> dtProjects = projects.stream()
				.map(this::createDataTablesProject)
				.collect(Collectors.toList());
		List<String> headers = ImmutableList.of("ProjectsTable_th_id", "ProjectsTable_th_name",
				"ProjectsTable_th_organism", "ProjectsTable_th_samples", "ProjectsTable_th_created_date",
				"ProjectsTable_th_modified_date")
				.stream()
				.map(h -> messageSource.getMessage(h, new Object[] {}, locale))
				.collect(Collectors.toList());

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
	 * Write the projects as a CSV file
	 *
	 * @param headers  {@link List} for {@link String} headers for the information.
	 * @param projects {@link List} of {@link DTProject} to export
	 * @param locale   {@link Locale}
	 * @param response {@link HttpServletResponse}
	 * @throws IOException Thrown if cannot get the {@link PrintWriter} for the response
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
	 * @param headers  {@link List} for {@link String} headers for the information.
	 * @param projects {@link List} of {@link DTProject} to export
	 * @param locale   {@link Locale}
	 * @param response {@link HttpServletResponse}
	 * @throws IOException Thrown if cannot get the {@link OutputStream} for the response
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
			row.createCell(cellCount++)
					.setCellValue(String.valueOf(p.getId()));
			row.createCell(cellCount++)
					.setCellValue(p.getName());
			row.createCell(cellCount++)
					.setCellValue(p.getOrganism());
			row.createCell(cellCount++)
					.setCellValue(String.valueOf(p.getSamples()));
			row.createCell(cellCount++)
					.setCellValue(dateFormat.format(p.getCreatedDate()));
			row.createCell(cellCount)
					.setCellValue(dateFormat.format(p.getModifiedDate()));
		}

		// Write the file
		try (OutputStream stream = response.getOutputStream()) {
			workbook.write(stream);
			stream.flush();
		}
	}

	/**
	 * Extract the details of the a {@link Project} into a {@link DTProject} which is consumable by the UI
	 *
	 * @param project {@link Project}
	 * @return {@link DTProject}
	 */
	private DTProject createDataTablesProject(Project project) {
		return new DTProject(project, sampleService.getNumberOfSamplesForProject(project));
	}
}
