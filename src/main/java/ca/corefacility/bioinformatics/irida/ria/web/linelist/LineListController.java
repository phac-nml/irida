package ca.corefacility.bioinformatics.irida.ria.web.linelist;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
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
	 * @param locale    {@link Locale}
	 * @return {@link List} of {@link MetadataTemplateField}
	 */
	@RequestMapping("/fields")
	@ResponseBody
	public List<MetadataTemplateField> getProjectMetadataTemplateFields(@RequestParam long projectId, Locale locale) {
		return getAllProjectMetadataFieldsWithSampleId(projectId, locale);
	}

	/**
	 * Get a {@link List} of {@link Map} containing information from {@link MetadataEntry} for all
	 * {@link  Sample}s in a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link Map}s of all {@link Sample} metadata in a {@link Project}
	 */
	@RequestMapping(value = "/entries", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, String>> getProjectSamplesMetadataEntries(@RequestParam long projectId) {
		return getAllProjectSamplesMetadataEntries(projectId);
	}

	/**
	 * Save an updated sample metadata entry
	 *
	 * @param sampleId {@link Long} identifier for a sample
	 * @param label    {@link String} the name of the {@link MetadataTemplateField}
	 * @param value    {@link String} the value to store in the {@link MetadataEntry}
	 * @param response {@link HttpServletResponse}
	 * @return The status of the request.
	 */
	@RequestMapping(value = "/entries", method = RequestMethod.POST)
	@ResponseBody
	public String saveMetadataEntry(@RequestParam long sampleId, @RequestParam String label, @RequestParam String value,
			HttpServletResponse response) {
		Sample sample = sampleService.read(sampleId);
		try {
			Map<MetadataTemplateField, MetadataEntry> metadata = sample.getMetadata();
			MetadataTemplateField templateField = metadataTemplateService.readMetadataFieldByLabel(label);
			if (templateField == null) {
				templateField = new MetadataTemplateField(label, "text");
				metadataTemplateService.saveMetadataField(templateField);
			}
			MetadataEntry entry;
			/*
			 Check to see if the field exists already.  If it does, then just update it.
			 If not create a new entry and carry on.
			 */
			if (metadata.containsKey(templateField)) {
				entry = metadata.get(templateField);
				entry.setValue(value);
			} else {
				entry = new MetadataEntry(value, "text");
			}
			metadata.put(templateField, entry);
			sampleService.update(sample);
			response.setStatus(HttpServletResponse.SC_OK);
			return "SUCCESS";
		} catch (EntityExistsException | EntityNotFoundException | ConstraintViolationException | InvalidPropertyException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "ERROR";
		}
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
		List<MetadataTemplateField> allFields = getAllProjectMetadataFields(projectId);
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
	 * @param response  {@link HttpServletResponse}
	 * @param locale    {@link Locale}
	 * @return saved or updated {@link UIMetadataTemplate}
	 */
	@RequestMapping(value = "/templates", method = RequestMethod.POST)
	public UIMetadataTemplate saveLineListTemplate(@RequestBody UIMetadataTemplate template,
			@RequestParam Long projectId, HttpServletResponse response, Locale locale) {
		String sampleNameColumn = messageSource.getMessage("linelist.agGrid.sampleName", new Object[] {}, locale);

		// Get or create the template fields.
		List<MetadataTemplateField> fields = new ArrayList<>();
		MetadataTemplateField metadataTemplateField;
		for (UIMetadataTemplateField field : template.getFields()) {
			// Don't save the same name column
			if (!field.getLabel()
					.equals(sampleNameColumn)) {
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
		return new UIMetadataTemplate(metadataTemplate, getAllProjectMetadataFieldsWithSampleId(projectId, locale));
	}

	/**
	 * Get a {@link List} of {@link MetadataTemplateField}s for a {@link Project}
	 *
	 * @param projectId {@link Long} Identifier for the project.
	 * @return {@link List} of {@link MetadataTemplateField}
	 */
	private List<MetadataTemplateField> getAllProjectMetadataFields(Long projectId) {
		Project project = projectService.read(projectId);
		List<MetadataTemplateField> metadataFieldsForProject = metadataTemplateService.getMetadataFieldsForProject(
				project);
		Set<MetadataTemplateField> fieldSet = new HashSet<>(metadataFieldsForProject);

		// Need to get all the fields from the templates too!
		List<ProjectMetadataTemplateJoin> templateJoins = metadataTemplateService.getMetadataTemplatesForProject(project);
		for (ProjectMetadataTemplateJoin join : templateJoins) {
			MetadataTemplate template = join.getObject();
			List<MetadataTemplateField> templateFields = template.getFields();
			fieldSet.addAll(templateFields);
		}
		List<MetadataTemplateField> fields = Lists.newArrayList(fieldSet);

		// Add the created and modified dates
		fields.add(new MetadataTemplateField("createdDate", "date"));
		fields.add(new MetadataTemplateField("modifiedDate", "date"));

		// Sort so they always return in the same order
		fields.sort((f1, f2) -> f1.getLabel()
				.compareToIgnoreCase(f2.getLabel()));
		return fields;
	}

	/**
	 * Get the template the the line list table.  This becomes the table headers.
	 *
	 * @param projectId {@link Long} identifier of the current {@link Project}
	 * @param locale    {@link Locale}
	 * @return {@link Set} containing unique metadata fields
	 */
	private List<MetadataTemplateField> getAllProjectMetadataFieldsWithSampleId(Long projectId, Locale locale) {
		List<MetadataTemplateField> fields = getAllProjectMetadataFields(projectId);
		return addDefaultTemplateFields(fields, locale);
	}

	private List<MetadataTemplateField> addDefaultTemplateFields(List<MetadataTemplateField> f, Locale locale) {
		List<MetadataTemplateField> fields = Lists.newArrayList(f);
		// Need the sample name.  This will enforce that it is in the first position.
		fields.add(0, new MetadataTemplateField(
				messageSource.getMessage("linelist.agGrid.sampleName", new Object[] {}, locale), "text"));
		fields.add(new MetadataTemplateField("createdDate", "date"));
		fields.add(new MetadataTemplateField("modifiedDate", "date"));
		return fields;
	}

	/**
	 * Get a {@link List} of {@link Map} for all {@link  Sample} {@link MetadataEntry}s in a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link List}s of all {@link Sample} metadata in a {@link Project}
	 */
	private List<Map<String, String>> getAllProjectSamplesMetadataEntries(Long projectId) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		List<Map<String, String>> result = new ArrayList<>(samplesForProject.size());

		for (Join<Project, Sample> join : samplesForProject) {
			Sample sample = join.getObject();
			Map<String, String> metadata = getMetadataForSample(sample);
			metadata.put("sampleId", String.valueOf(sample.getId()));
			metadata.put("sampleName", sample.getSampleName());
			metadata.put("projectId", String.valueOf(projectId));
			metadata.put("projectName", project.getLabel());
			metadata.put("createdDate", String.valueOf(sample.getCreatedDate()));
			metadata.put("modifiedDate", String.valueOf(sample.getCreatedDate()));
			result.add(metadata);
		}

		return result;
	}

	private Map<String, String> getMetadataForSample(Sample sample) {
		Map<String, String> entries = new HashMap<>();
		Map<MetadataTemplateField, MetadataEntry> sampleMetadata = sample.getMetadata();
		for (MetadataTemplateField field : sampleMetadata.keySet()) {
			MetadataEntry entry = sampleMetadata.getOrDefault(field, new MetadataEntry());
			entries.put(field.getLabel(), entry.getValue());
		}
		return entries;
	}
}
