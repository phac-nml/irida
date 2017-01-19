package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
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
	 * Get a {@link List} of {@link MetadataTemplate}s available for the current {@link Project}
	 *
	 * @param locale
	 * 		{@link Locale} users current locale
	 * @param project
	 * 		{@link Project} the project to get {@link MetadataTemplate}s for
	 *
	 * @return {@link List} of {@link MetadataTemplate}
	 */
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

		// templateId usually comes into play when a user just uploaded a metadata
		// spreadsheet and is being redirected to this page.
		if (templateId != null) {
			model.addAttribute("currentTemplate", templateId);
		}

		// Get a list of all available templates for displaying metadata
		model.addAttribute("templates", getTemplateNames(locale, project));

		// Get the headers (metadata fields)
		List<String> headers = getAllProjectMetadataFields(projectId);
		model.addAttribute("headers", headers);

		// Get all the metadata for each sample in the project
		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		List<List<Object>> metadataList = new ArrayList<>(samplesForProject.size());
		for (Join<Project, Sample> join : samplesForProject) {
			Sample sample = join.getObject();
			List<Object> fullMetadata = new ArrayList<>();
			SampleMetadata sampleMetadata = sampleService.getMetadataForSample(sample);
			if (sampleMetadata != null) {
				Map<String, Object> metadata = sampleMetadata.getMetadata();
				for (String header : headers) {
					if (header.equalsIgnoreCase("id")) {
						fullMetadata.add(sample.getId());
					} else if (header.equalsIgnoreCase("label")) {
						fullMetadata.add(sample.getSampleName());
					} else {
						fullMetadata.add(metadata.getOrDefault(header, ""));
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
	 * Get the metadata fields for a specific template
	 *
	 * @param templateId
	 * 		{@link Long} identifier for a template
	 *
	 * @return {@link List} list of {@link MetadataField} for a template.
	 */
	@RequestMapping("/metadatafields")
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
		// These are default fields for the start
		fieldList.add(0, "label");
		fieldList.add(0, "id");

		return fieldList;
	}
}
