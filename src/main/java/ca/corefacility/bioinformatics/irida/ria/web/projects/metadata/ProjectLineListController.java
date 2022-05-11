package ca.corefacility.bioinformatics.irida.ria.web.projects.metadata;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableMap;

/**
 * Handles requests for the metadata in a project
 */
@Controller
@RequestMapping("/projects/{projectId}/linelist")
public class ProjectLineListController {

	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MetadataTemplateService metadataTemplateService;
	private final ProjectControllerUtils projectControllerUtils;
	private final MessageSource messageSource;

	@Autowired
	public ProjectLineListController(ProjectService projectService, SampleService sampleService,
			MetadataTemplateService metadataTemplateService, ProjectControllerUtils utils,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.projectControllerUtils = utils;
		this.messageSource = messageSource;
	}

	/**
	 * Get the page to display the project samples linelist.
	 *
	 * @param projectId  {@link Long} identifier for the current {@link Project}
	 * @param templateId {@link Long} id for the current template
	 * @param model      {@link Model}
	 * @param principal  {@link Principal} currently logged in user.
	 * @return {@link String} path to the current page.
	 */
	@RequestMapping("")
	public String getLineListPage(@PathVariable Long projectId, @RequestParam(required = false) Long templateId,
			Model model, Principal principal) {
		// Set up the template information
		Project project = projectService.read(projectId);
		Long totalSamples = sampleService.getNumberOfSamplesForProject(project);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("activeNav", "linelist");
		model.addAttribute("totalSamples", totalSamples);

		// templateId usually comes into play when a user just uploaded a metadata
		// spreadsheet and is being redirected to this page.
		if (templateId != null) {
			model.addAttribute("currentTemplate", templateId);
		}

		return "projects/project_linelist";
	}

	/**
	 * Get the page to create new linelist templates
	 *
	 * @param projectId {@link Long} identifier for the current {@link Project}
	 * @param model     {@link Model}
	 * @param locale    {@link Locale}
	 * @param principal {@link Principal}
	 * @return {@link String} path to the page.
	 */
	@RequestMapping("/linelist-templates")
	public String getLinelistTemplatePage(@PathVariable Long projectId, Model model, Locale locale,
			Principal principal) {
		// Set up the template information
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("templates", projectControllerUtils.getTemplateNames(locale, project));
		return "projects/project_linelist_template";
	}

	/**
	 * Get the metadata fields for a specific template
	 *
	 * @param templateId {@link Long} identifier for a template
	 * @return {@link List} list of {@link MetadataTemplateField} for a template.
	 */
	@RequestMapping("/upload/metadatafields")
	@ResponseBody
	public List<MetadataTemplateField> getMetadaFieldsForTemplate(@RequestParam Long templateId) {
		MetadataTemplate template = metadataTemplateService.read(templateId);
		List<MetadataTemplateField> permittedFieldsForTemplate = metadataTemplateService.getPermittedFieldsForTemplate(
				template);

		return permittedFieldsForTemplate;
	}

	/**
	 * Save a new line list template.
	 *
	 * @param projectId    {@link Long} id for the current project
	 * @param templateName {@link String} name for the new template
	 * @param fields       The fields to save to the template
	 * @return The result of saving.
	 */
	@RequestMapping(value = "/linelist-templates/save-template/{templateName}",
			consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveLinelistTemplate(@PathVariable Long projectId, @PathVariable String templateName,
			@RequestBody List<Map<String, String>> fields) {
		Project project = projectService.read(projectId);
		List<MetadataTemplateField> metadataFields = new ArrayList<>();

		for (Map<String, String> field : fields) {
			String label = field.get("label");
			// Label and identifier are default that are always in the list.
			MetadataTemplateField metadataField;
			if (field.containsKey("identifier")) {
				// Identifier would indicate an existing field.  Therefore we should use the existing field
				// instead of creating a new one.
				metadataField = metadataTemplateService.readMetadataField(Long.parseLong(field.get("identifier")));
			} else {
				// Check to see if the field already exists
				metadataField = metadataTemplateService.readMetadataFieldByLabel(label);
				if (metadataField == null) {
					metadataField = new MetadataTemplateField(label, field.get("type"));
					metadataTemplateService.saveMetadataField(metadataField);
				}
			}
			metadataFields.add(metadataField);
		}
		MetadataTemplate metadataTemplate = new MetadataTemplate(templateName, metadataFields);
		metadataTemplate = metadataTemplateService
				.createMetadataTemplateInProject(metadataTemplate, project);

		return ImmutableMap.of("templateId", metadataTemplate.getId());
	}

	/**
	 * Get a {@link List} of {@link MetadataTemplate}s for a specific {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @param locale    users current {@link Locale}
	 * @return {@link List} of {@link MetadataTemplate}
	 */
	@RequestMapping(value = "/templates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<MetadataTemplate> getMetadataTemplates(@PathVariable long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		List<MetadataTemplate> templates = metadataTemplateService.getMetadataTemplatesForProject(project);

		return templates;
	}

	/**
	 * Save a list a {@link MetadataTemplateField} as a {@link MetadataTemplate}
	 *
	 * @param projectId  identifier for the current {@link Project}
	 * @param fields     {@link List} of {@link String} names of {@link MetadataTemplateField}
	 * @param name       {@link String} name for the new template.
	 * @param templateId ID of the template to update. Will create new template if null
	 * @param locale     Locale of teh logged in user
	 * @return the saved {@link MetadataTemplate} and a response message
	 */
	@RequestMapping(value = "/templates", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveMetadataTemplate(@PathVariable long projectId, @RequestParam String name,
			@RequestParam(value = "fields[]") List<String> fields, @RequestParam(required = false) Long templateId,
			Locale locale) {
		Project project = projectService.read(projectId);

		List<MetadataTemplateField> metadataFields = new ArrayList<>();

		for (String label : fields) {
			// Check to see if this field already exists.
			MetadataTemplateField metadataField = metadataTemplateService.readMetadataFieldByLabel(label);
			// If it does not exist, create a new field.
			if (metadataField == null) {
				metadataField = new MetadataTemplateField(label, "text");
				metadataTemplateService.saveMetadataField(metadataField);
			}
			metadataFields.add(metadataField);
		}
		MetadataTemplate template;
		String message;
		// If the template already has an ID, it is an existing template, so just update it.
		if (templateId != null) {
			template = metadataTemplateService.read(templateId);
			template.setFields(metadataFields);
			template.setName(name);
			metadataTemplateService.updateMetadataTemplateInProject(template);
			message = messageSource.getMessage("linelist.create-template.update-success", new Object[] { name },
					locale);
		} else {
			template = new MetadataTemplate(name, metadataFields);
			template = metadataTemplateService.createMetadataTemplateInProject(template, project);
			message = messageSource.getMessage("linelist.create-template.success", new Object[]{name}, locale);
		}
		return ImmutableMap.of("template", template, "message", message);
	}

	/**
	 * Ajax request handler for deleting a specific {@link MetadataTemplate}
	 *
	 * @param projectId  {@link Long} identifier for a {@link Project}
	 * @param templateId {@link Long} identifier for a {@link MetadataTemplate}
	 * @param locale     {@link Locale}
	 * @return {@link Map} of success message.
	 */
	@RequestMapping(value = "/templates/{templateId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, String> deleteMetadataTemplate(@PathVariable Long projectId, @PathVariable Long templateId,
			Locale locale) {
		Project project = projectService.read(projectId);
		MetadataTemplate template = metadataTemplateService.read(templateId);
		metadataTemplateService.deleteMetadataTemplateFromProject(project, templateId);
		return ImmutableMap.of("message", messageSource.getMessage("linelist.create-template.delete-success",
				new Object[] { template.getLabel(), project.getLabel() }, locale));
	}
}
