package ca.corefacility.bioinformatics.irida.ria.web.projects.metadata;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.UIMetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

/**
 * Handles requests for {@link MetadataTemplate}s in a {@link Project}
 */
@Controller
@RequestMapping("/projects/{projectId}/metadata-templates")
public class ProjectSamplesMetadataTemplateController {
	private ProjectService projectService;
	private MetadataTemplateService metadataTemplateService;
	private ProjectControllerUtils projectControllerUtils;

	@Autowired
	public ProjectSamplesMetadataTemplateController(ProjectService projectService,
			ProjectControllerUtils projectControllerUtils, MetadataTemplateService metadataTemplateService) {
		this.projectService = projectService;
		this.projectControllerUtils = projectControllerUtils;
		this.metadataTemplateService = metadataTemplateService;
	}

	/**
	 * Get the page to create a new {@link MetadataTemplate}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @param model     {@link Model} spring page model
	 * @param principal {@link Principal} currently logged in user
	 * @return {@link String} path to the new template page
	 */
	@RequestMapping("/new")
	public String getMetadataTemplateListPage(@PathVariable Long projectId, Model model, Principal principal) {
		Project project = projectService.read(projectId);

		// Add an empty MetadataTemplate. This facilitates code reuse for this page
		// since it is used for both creation and updating a template, and the html
		// is looking for a template object.
		model.addAttribute("template", new UIMetadataTemplate());
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/project_samples_metadata_template";
	}

	/**
	 * Get a the page for a specific {@link MetadataTemplate}
	 *
	 * @param projectId  {@link Long} identifier for a {@link Project}
	 * @param templateId {@link Long} identifier for a {@link MetadataTemplate}
	 * @param principal  {@link Principal} currently logged in user
	 * @param model      {@link Model} spring page model
	 * @return {@link String} path to template page
	 */
	@RequestMapping("/{templateId}")
	public String getMetadataTemplatePage(@PathVariable Long projectId, @PathVariable Long templateId,
			Principal principal, Model model) {
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		MetadataTemplate metadataTemplate = metadataTemplateService.read(templateId);
		model.addAttribute("template", metadataTemplate);
		return "projects/project_samples_metadata_template";
	}

	/**
	 * Save or update a {@link MetadataTemplate} within a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a project
	 * @param id        {@link Long} identifier for a template
	 * @param name      {@link String} name for the template
	 * @param fields    {@link List} of fields names
	 * @return {@link String} result
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveMetadataTemplate(@PathVariable Long projectId, @RequestParam Long id, @RequestParam String name,
			@RequestParam List<String> fields) {
		Project project = projectService.read(projectId);
		List<MetadataTemplateField> templateFields = new ArrayList<>();
		for (String field : fields) {
			MetadataTemplateField f = metadataTemplateService.readMetadataFieldByLabel(field);
			if (f == null) {
				MetadataTemplateField templateField = metadataTemplateService.saveMetadataField(
						new MetadataTemplateField(field, "text"));
				templateFields.add(templateField);
			} else {
				templateFields.add(f);
			}
		}

		MetadataTemplate metadataTemplate;
		if (id != null) {
			metadataTemplate = metadataTemplateService.read(id);
			metadataTemplate.setName(name);
			metadataTemplate.setFields(templateFields);
			metadataTemplateService.updateMetadataTemplateInProject(metadataTemplate);
		} else {
			ProjectMetadataTemplateJoin projectMetadataTemplateJoin = metadataTemplateService.createMetadataTemplateInProject(
					new MetadataTemplate(name, templateFields), project);
			metadataTemplate = projectMetadataTemplateJoin.getObject();
		}
		return "redirect:/projects/" + projectId + "/metadata-templates/" + metadataTemplate.getId();
	}

	/**
	 * Delete a {@link MetadataTemplate} within a {@link Project}
	 *
	 * @param projectId  {@link Long} identifier for a {@link Project}
	 * @param templateId {@link Long} identifier for a {@link MetadataTemplate}
	 * @return {@link String} redirects to project - settings - metadata templates
	 */
	@RequestMapping(value = "/delete/{templateId}", method = RequestMethod.POST)
	public String deleteMetadataTemplate(@PathVariable Long projectId, @PathVariable Long templateId) {
		Project project = projectService.read(projectId);
		metadataTemplateService.deleteMetadataTemplateFromProject(project, templateId);
		return "redirect:/projects/" + projectId + "/settings/metadata-templates";
	}

	/**
	 * Download a {@link MetadataTemplate} as an Excel file.
	 *
	 * @param templateId {@link Long} identifier for a {@link MetadataTemplate}
	 * @param response   {@link HttpServletResponse}
	 * @throws IOException thrown if output stream cannot be used.
	 */
	@RequestMapping(value = "/{templateId}/excel")
	public void downloadTemplate(@PathVariable Long templateId, HttpServletResponse response) throws IOException {
		MetadataTemplate template = metadataTemplateService.read(templateId);
		List<MetadataTemplateField> fields = template.getFields();
		List<String> headers = fields.stream()
				.map(MetadataTemplateField::getLabel)
				.collect(Collectors.toList());
		String label = template.getLabel()
				.replace(" ", "_");
		//Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		//Create a blank sheet
		XSSFSheet worksheet = workbook.createSheet(label);

		// Write the headers
		XSSFRow headerRow = worksheet.createRow(0);
		for (int i = 0; i < headers.size(); i++) {
			XSSFCell cell = headerRow.createCell(i);
			cell.setCellValue(headers.get(i));
		}

		response.setHeader("Content-Disposition", "attachment; filename=\"" + label + ".xlsx\"");
		ServletOutputStream stream = response.getOutputStream();
		workbook.write(stream);
		stream.flush();
	}

	// *************************************************************************
	// AJAX METHODS                                                            *
	// *************************************************************************

	/**
	 * Search all Metadata keys available for adding to a template.
	 *
	 * @param query the query to search for
	 * @return a list of keys matching the query
	 */
	@RequestMapping("/fields")
	@ResponseBody
	public List<String> getMetadataKeysForProject(@RequestParam(value = "q") String query) {
		return metadataTemplateService.getAllMetadataFieldsByQueryString(query)
				.stream()
				.map(MetadataTemplateField::getLabel)
				.collect(Collectors.toList());
	}
}
