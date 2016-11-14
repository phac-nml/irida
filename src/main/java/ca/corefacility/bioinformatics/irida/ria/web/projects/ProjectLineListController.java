package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataField;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleMetadata;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Controller
@RequestMapping("/projects/{projectId}/linelist")
public class ProjectLineListController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectLineListController.class);

	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MetadataTemplateService metadataTemplateService;
	private final ProjectControllerUtils projectControllerUtils;
	private final MessageSource messageSource;

	private final List<String> DEFAULT_TEMPLATE = ImmutableList.of("id", "label");

	@Autowired
	public ProjectLineListController(ProjectService projectService, SampleService sampleService,
			MetadataTemplateService metadataTemplateService, MessageSource messageSource,
			ProjectControllerUtils utils) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.messageSource = messageSource;
		this.projectControllerUtils = utils;
	}

	private List<Map<String, String>> getTemplateNames(Locale locale, Project project) {
		List<ProjectMetadataTemplateJoin> metadataTemplatesForProject = metadataTemplateService.getMetadataTemplatesForProject(project);
		List<Map<String, String>> templates = new ArrayList<>();
		for (ProjectMetadataTemplateJoin projectMetadataTemplateJoin : metadataTemplatesForProject) {
			MetadataTemplate template = projectMetadataTemplateJoin.getObject();
			templates.add(ImmutableMap.of("label", template.getLabel(), "id", String.valueOf(template.getId())));
		}
		if (templates.size() == 0) {
			templates.add(ImmutableMap
					.of("label", messageSource.getMessage("linelist.no-templates-available", new Object[] {}, locale),
							"id", ""));
		}
		return templates;
	}

	/**
	 * Get the page to display the project samples linelist.
	 *
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}
	 * @param templateId
	 * 		{@link Long} id for the current template
	 * @param model
	 * 		{@link Model}
	 * @param locale
	 * 		{@link Locale}
	 * @param principal
	 * 		{@link Principal} currently logged in user.
	 *
	 * @return {@link String} path to the current page.
	 */
	@RequestMapping("")
	public String getLineListPage(@PathVariable Long projectId, @RequestParam(required = false) Long templateId,
			Model model, Locale locale, Principal principal) {
		// Set up the template information
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("activeNav", "linelist");
		if (templateId != null) {
			model.addAttribute("currentTemplate", templateId);
		}
		model.addAttribute("templates", getTemplateNames(locale, project));
		return "projects/project_linelist";
	}

	/**
	 * Get the page to create new linelist templates
	 *
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}
	 * @param model
	 * 		{@link Model}
	 * @param locale
	 *   	{@link Locale}
	 * @param principal
	 * 		{@link Principal}
	 *
	 * @return {@link String} path to the page.
	 */
	@RequestMapping("/linelist-templates")
	public String getLinelistTemplatePage(@PathVariable Long projectId, Model model, Locale locale,
			Principal principal) {
		// Set up the template information
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("templates", getTemplateNames(locale, project));
		return "projects/project_linelist_template";
	}

	/**
	 * Get the template the the line list table.  This becomes the table headers.
	 *
	 * @param templateId
	 * 		{@link Long} identifier of the template to get.
	 *
	 * @return {@link Map} containing the template.
	 */
	@RequestMapping("/fields")
	@ResponseBody
	public Map<String, Object> getLinelistTemplate(@RequestParam(required = false) Long templateId) {
		List<String> fields = new ArrayList<>();
		if (templateId != null) {
			MetadataTemplate metadataTemplate = metadataTemplateService.read(templateId);
			List<MetadataField> metadataTemplateFields = metadataTemplate.getFields();
			for (MetadataField metadataField : metadataTemplateFields) {
				fields.add(metadataField.getLabel());
			}
		} else {
			logger.debug("Trying to load a metadata template that does not exist.");
			fields = DEFAULT_TEMPLATE;
		}
		return ImmutableMap.of("fields", fields);
	}

	@RequestMapping("/metadatafields")
	@ResponseBody
	public List<MetadataField> getMetadaFieldsForTemplate(@RequestParam Long templateId) {
		MetadataTemplate template = metadataTemplateService.read(templateId);
		return template.getFields();
	}

	/**
	 * Get the metadata for the table that matches the current template.
	 *
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}.
	 * @param templateId
	 * 		{@link Long} name of the template to metadata for.
	 *
	 * @return {@link Map} of all the metadata.
	 */
	@RequestMapping("/metadata")
	@ResponseBody
	public Map<String, Object> getLinelistMetadata(@PathVariable Long projectId,
			@RequestParam(required = false) Long templateId) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> projectSamples = sampleService.getSamplesForProject(project);
		List<MetadataField> lineListFields = new ArrayList<>();
		if (templateId != null) {
			MetadataTemplate metadataTemplate = metadataTemplateService.read(templateId);
			lineListFields = metadataTemplate.getFields();
		}

		List<Map<String, Object>> metadata = new ArrayList<>();
		for (Join<Project, Sample> join : projectSamples) {
			Sample sample = join.getObject();
			SampleMetadata sampleMetadata = sampleService.getMetadataForSample(sample);

			// Get the current samples metadata (if it exists).
			Map<String, Object> data;
			if (sampleMetadata != null) {
				data = sampleMetadata.getMetadata();
			} else {
				data = ImmutableMap.of();
			}
			Map<String, Object> md = new HashMap<>();
			md.put("id", sample.getId());
			md.put("label", sample.getLabel());
			// Every template is expected to start with the sample identifier and label
			for (MetadataField field : lineListFields) {
				String header = field.getLabel();
				if (data.containsKey(header)) {
					md.put(header, data.get(header));
				} else {
					md.put(header, "");
				}
			}
			metadata.add(md);
		}
		return ImmutableMap.of("metadata", metadata);
	}

	/**
	 * Save a new line list template.
	 *
	 * @param projectId
	 * 		{@link Long} id for the current project
	 * @param templateName
	 * 		{@link String} name for the new template
	 *
	 * @return The result of saving.
	 */
	@RequestMapping(value = "/linelist-templates/save-template/{templateName}",
					consumes = MediaType.APPLICATION_JSON_VALUE,
					method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveLinelistTemplate(@PathVariable Long projectId, @PathVariable String templateName,
			@RequestBody List<Map<String, String>> fields) {
		Project project = projectService.read(projectId);
		List<MetadataField> metadataFields = new ArrayList<>();
		for (Map<String, String> field : fields) {
			String label = field.get("label");
			// Label and identifier are default that are always in the list.
			MetadataField metadataField;
			if (field.containsKey("identifier")) {
				// Identifier would indicate an existing field.  Therefore we should use the existing field
				// instead of creating a new one.
				metadataField = metadataTemplateService.readMetadataField(Long.parseLong(field.get("identifier")));
			} else {
				metadataField = new MetadataField(label, field.get("type"));
				metadataTemplateService.saveMetadataField(metadataField);
			}
			metadataFields.add(metadataField);
		}
		MetadataTemplate metadataTemplate = new MetadataTemplate(templateName, metadataFields);
		ProjectMetadataTemplateJoin projectMetadataTemplateJoin = metadataTemplateService
				.createMetadataTemplateInProject(metadataTemplate, project);

		return ImmutableMap.of("templateId", projectMetadataTemplateJoin.getObject().getId());
	}
}
