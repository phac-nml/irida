package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
			MetadataTemplateService metadataTemplateService, MessageSource messageSource,
			ProjectControllerUtils utils) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.messageSource = messageSource;
		this.projectControllerUtils = utils;
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

		// templateId usually comes into play when a user just uploaded a metadata
		// spreadsheet and is being redirected to this page.
		if (templateId != null) {
			model.addAttribute("currentTemplate", templateId);
		}

		// Get the headers (metadata fields)
		List<String> headers = getAllProjectMetadataFields(projectId);
		model.addAttribute("headers", headers);

		// Get all the metadata for each sample in the project
		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		List<Map<String, Object>> metadataList = new ArrayList<>(samplesForProject.size());
		for (Join<Project, Sample> join : samplesForProject) {
			Sample sample = join.getObject();
			Map<String, Object> fullMetadata = new HashMap<>();
			SampleMetadata sampleMetadata = sampleService.getMetadataForSample(sample);
			if (sampleMetadata != null) {
				Map<String, Object> metadata = sampleMetadata.getMetadata();
				for (String header : headers) {
					/*
					Since the id and the label are kept on the Sample not in the JSON,
					They need to be pulled specifically from the sample.
					 */
					if (header.equalsIgnoreCase("id")) {
						fullMetadata.put("id", ImmutableMap.of("value", sample.getId()));
					} else if (header.equalsIgnoreCase("label")) {
						fullMetadata.put("label", ImmutableMap.of("value", sample.getSampleName()));
					} else {
						fullMetadata.put(header, metadata.getOrDefault(header, ImmutableMap.of("value", "")));
					}
				}

				// Put this here to avoid showing samples that do not have
				// any metadata associated with them.
				metadataList.add(fullMetadata);
			}
		}
		model.addAttribute("metadataList", metadataList);
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
	 * 		{@link Locale}
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
		model.addAttribute("templates", projectControllerUtils.getTemplateNames(locale, project));
		return "projects/project_linelist_template";
	}

	/**
	 * Get the metadata fields for a specific template
	 *
	 * @param templateId
	 * 		{@link Long} identifier for a template
	 *
	 * @return {@link List} list of {@link MetadataField} for a template.
	 */
	@RequestMapping("/upload/metadatafields")
	@ResponseBody
	public List<MetadataField> getMetadaFieldsForTemplate(@RequestParam Long templateId) {
		MetadataTemplate template = metadataTemplateService.read(templateId);
		return template.getFields();
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

	/**
	 * Get the template the the line list table.  This becomes the table headers.
	 *
	 * @param projectId
	 * 		{@link Long} identifier of the current {@link Project}
	 *
	 * @return {@link Set} containing unique metadata fields
	 */
	private List<String> getAllProjectMetadataFields(Long projectId) {
		Project project = projectService.read(projectId);
		Set<String> fields = new HashSet<>();

		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		for (Join<Project, Sample> join : samplesForProject) {
			Sample sample = join.getObject();
			SampleMetadata sampleMetadata = sampleService.getMetadataForSample(sample);
			if (sampleMetadata != null) {
				Map<String, Object> metadataFields = sampleMetadata.getMetadata();
				fields.addAll(metadataFields.keySet());
			}
		}
		List<String> fieldList = new ArrayList<>(fields);

		// Need to add default sample fields.
		fieldList.add(0, "label");
		fieldList.add(0, "id");

		return fieldList;
	}

	/**
	 * Get a {@link List} of {@link MetadataTemplate}s for a specific {@link Project}
	 *
	 * @param projectId
	 * 		{@link Long} identifier for a {@link Project}
	 * @param locale
	 * 		users current {@link Locale}
	 *
	 * @return {@link List} of {@link MetadataTemplate}
	 */
	@RequestMapping(value = "/templates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<MetadataTemplate> getMetadataTemplates(@PathVariable long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		List<ProjectMetadataTemplateJoin> joins = metadataTemplateService.getMetadataTemplatesForProject(project);
		List<MetadataTemplate> templates = new ArrayList<>();
		// Add Template for all fields
		MetadataTemplate allTemplate = new MetadataTemplate(
				messageSource.getMessage("linelist.templates.all", new Object[] {}, locale), ImmutableList.of());
		allTemplate.setId(0L);
		templates.add(allTemplate);

		for (ProjectMetadataTemplateJoin join : joins) {
			templates.add(join.getObject());
		}
		return templates;
	}

	/**
	 * Save a list a {@link MetadataField} as a {@link MetadataTemplate}
	 *
	 * @param projectId
	 * 		identifier for the current {@link Project}
	 * @param fields
	 * 		{@link List} of {@link String} names of {@link MetadataField}
	 * @param name
	 * 		{@link String} name for the new template.
	 *
	 * @return
	 */
	@RequestMapping(value = "/templates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public MetadataTemplate saveMetadataTemplate(@PathVariable long projectId,
			@RequestParam(value = "fields[]") List<String> fields, @RequestParam String name) {
		Project project = projectService.read(projectId);
		List<MetadataField> metadataFields = new ArrayList<>();
		for (String field : fields) {
			// Check to see if this field already exists.
			MetadataField metadataField = metadataTemplateService.readMetadataFieldByLabel(field);
			// If it does not exist, create a new field.
			if (metadataField == null) {
				metadataField = new MetadataField(field, "text");
				metadataTemplateService.saveMetadataField(metadataField);
			}
			metadataFields.add(metadataField);
		}
		MetadataTemplate template = new MetadataTemplate(name, metadataFields);
		ProjectMetadataTemplateJoin join = metadataTemplateService.createMetadataTemplateInProject(template, project);
		return join.getObject();
	}
}
