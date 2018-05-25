package ca.corefacility.bioinformatics.irida.ria.web.linelist;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.web.models.UIMetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.models.UIMetadataTemplateField;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * This controller is responsible for AJAX handling for the line list page, which displays sample metadata.
 */
@Controller
@RequestMapping("/linelist")
public class LineListController {
	private ProjectService projectService;
	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;
	private MessageSource messageSource;

	@Autowired
	public LineListController(ProjectService projectService, SampleService sampleService,
			MetadataTemplateService metadataTemplateService, MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.messageSource = messageSource;
	}

	/**
	 * Get a list of all {@link MetadataTemplateField}s on a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link MetadataTemplateField}
	 */
	@RequestMapping("/fields")
	@ResponseBody
	public List<MetadataTemplateField> getProjectMetadataTemplateFields(@RequestParam long projectId) {
		return getAllProjectMetadataFields(projectId);
	}

	/**
	 * Get a {@link List} of {@link Map} containing information from {@link MetadataEntry} for all
	 * {@link  Sample}s in a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link List}s of all {@link Sample} metadata in a {@link Project}
	 */
	@RequestMapping("/entries")
	@ResponseBody
	public List<Map<String, MetadataEntry>> getProjectSamplesMetadataEntries(@RequestParam long projectId) {
		return getAllProjectSamplesMetadataEntries(projectId);
	}

	/**
	 * Get a {@link List} of all {@link MetadataTemplate} associated with the project.
	 *
	 * @param projectId {@link Long} Identifier for the project to get id's for.
	 * @param locale    {@link Locale} Locale of the currently logged in user.
	 * @return {@link List}
	 */
	@RequestMapping("/templates")
	@ResponseBody
	public List<UIMetadataTemplate> getLineListTemplates(@RequestParam long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		List<MetadataTemplateField> allFields = metadataTemplateService.getMetadataFieldsForProject(project);
		List<ProjectMetadataTemplateJoin> joins = metadataTemplateService.getMetadataTemplatesForProject(project);
		List<UIMetadataTemplate> templates = joins.stream()
				.map(join -> new UIMetadataTemplate(join.getObject(), new ArrayList<>(allFields)))
				.collect(Collectors.toList());

		// Add a "Template" for all fields
		templates.add(0, new UIMetadataTemplate(new MetadataTemplate(
				messageSource.getMessage("linelist.templates.Select.none", new Object[] {}, locale), allFields)));

		return templates;
	}

	/**
	 * Save or update a {@link MetadataTemplate}
	 *
	 * @param template  {@link UIMetadataTemplate}
	 * @param projectId {@link Long} project identifier
	 * @return saved or updated {@link UIMetadataTemplate}
	 */
	@RequestMapping(value = "/templates", method = RequestMethod.POST)
	public UIMetadataTemplate saveLineListTemplate(@RequestBody UIMetadataTemplate template,
			@RequestParam Long projectId, HttpServletResponse response) {

		// Get or create the template fields.
		List<MetadataTemplateField> fields = new ArrayList<>();
		MetadataTemplateField metadataTemplateField;
		for (UIMetadataTemplateField field : template.getFields()) {
			// Don't save the same name
			if (!field.getLabel()
					.equals("sampleName")) {
				if (field.getId() == null) {
					metadataTemplateField = metadataTemplateService.saveMetadataField(
							new MetadataTemplateField(field.getLabel(), "text"));
				} else {
					metadataTemplateField = metadataTemplateService.readMetadataField(field.getId());
				}
				fields.add(metadataTemplateField);
			}
		}

		// Save the template.
		MetadataTemplate metadataTemplate;
		if (template.getId() == null) {
			// NO ID means that this is a new template
			Project project = projectService.read(projectId);
			metadataTemplate = new MetadataTemplate(template.getName(), fields);
			ProjectMetadataTemplateJoin join = metadataTemplateService.createMetadataTemplateInProject(metadataTemplate,
					project);
			metadataTemplate = join.getObject();
			response.setStatus(HttpServletResponse.SC_CREATED);
		} else {
			metadataTemplate = metadataTemplateService.read(template.getId());
			metadataTemplate.setFields(fields);
			metadataTemplate = metadataTemplateService.updateMetadataTemplateInProject(metadataTemplate);
			response.setStatus(HttpServletResponse.SC_OK);
		}
		return new UIMetadataTemplate(metadataTemplate, getAllProjectMetadataFields(projectId));
	}

	/**
	 * Get the template the the line list table.  This becomes the table headers.
	 *
	 * @param projectId {@link Long} identifier of the current {@link Project}
	 * @return {@link Set} containing unique metadata fields
	 */
	private List<MetadataTemplateField> getAllProjectMetadataFields(Long projectId) {
		Project project = projectService.read(projectId);
		Iterable<MetadataTemplateField> fieldCollection = metadataTemplateService.getMetadataFieldsForProject(project);

		// Need to get all the fields from the templates too!
		List<ProjectMetadataTemplateJoin> templateJoins = metadataTemplateService.getMetadataTemplatesForProject(
				project);
		for (ProjectMetadataTemplateJoin join : templateJoins) {
			MetadataTemplate template = join.getObject();
			Iterable<MetadataTemplateField> templateFields = template.getFields();
			fieldCollection = Iterables.concat(fieldCollection, templateFields);
		}

		List<MetadataTemplateField> fields = Lists.newArrayList(fieldCollection);
		// Need the sample name.  This will enforce that it is in the first position.
		fields.add(0, new MetadataTemplateField("sampleName", "text"));
		return fields;
	}

	/**
	 * Get a {@link List} of {@link Map} for all {@link  Sample} {@link MetadataEntry}s in a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link List}s of all {@link Sample} metadata in a {@link Project}
	 */
	private List<Map<String, MetadataEntry>> getAllProjectSamplesMetadataEntries(Long projectId) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		List<Map<String, MetadataEntry>> result = new ArrayList<>(samplesForProject.size());

		for (Join<Project, Sample> join : samplesForProject) {
			Sample sample = join.getObject();
			Map<String, MetadataEntry> entries = new HashMap<>();

			// Need to have the sample name and Id
			entries.put("sampleName", new MetadataEntry(sample.getLabel(), "text"));
			entries.put("sampleId", new MetadataEntry(String.valueOf(sample.getId()), "number"));

			Map<MetadataTemplateField, MetadataEntry> sampleMetadata = sample.getMetadata();
			for (MetadataTemplateField field : sampleMetadata.keySet()) {
				entries.put(field.getLabel(), sampleMetadata.getOrDefault(field, new MetadataEntry()));
			}
			result.add(entries);
		}

		return result;
	}
}
