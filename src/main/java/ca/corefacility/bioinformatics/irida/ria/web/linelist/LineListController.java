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
import ca.corefacility.bioinformatics.irida.ria.web.components.agGrid.AgGridUtilities;
import ca.corefacility.bioinformatics.irida.ria.web.components.agGrid.AgGridColumn;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.UIMetadataFieldDefault;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.UIMetadataField;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.UIMetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.UISampleMetadata;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * This controller is responsible for AJAX handling for the line list page, which displays sample metadata.
 */
@Controller
@RequestMapping("/linelist")
public class LineListController {
	private ProjectService projectService;
	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;
	private MessageSource messages;

	@Autowired
	public LineListController(ProjectService projectService, SampleService sampleService,
			MetadataTemplateService metadataTemplateService, MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.messages = messageSource;
	}

	/**
	 * Get a list of all {@link MetadataTemplateField}s on a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @param locale    {@link Locale}
	 * @return {@link List} of {@link UIMetadataField}
	 */
	@RequestMapping("/fields")
	@ResponseBody
	public List<AgGridColumn> getProjectMetadataTemplateFields(@RequestParam long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		List<MetadataTemplateField> metadataFieldsForProject = metadataTemplateService.getMetadataFieldsForProject(
				project);
		Set<MetadataTemplateField> fieldSet = new HashSet<>(metadataFieldsForProject);

		// Need to get all the fields from the templates too!
		List<ProjectMetadataTemplateJoin> templateJoins = metadataTemplateService.getMetadataTemplatesForProject(
				project);
		for (ProjectMetadataTemplateJoin join : templateJoins) {
			MetadataTemplate template = join.getObject();
			List<MetadataTemplateField> templateFields = template.getFields();
			fieldSet.addAll(templateFields);
		}

		List<AgGridColumn> fields = fieldSet.stream()
				.map(f -> new UIMetadataField(f, false, true))
				.sorted((f1, f2) -> f1.getHeaderName()
						.compareToIgnoreCase(f2.getHeaderName()))
				.collect(Collectors.toList());

		// Add the sample name, project name, created date and the modified date
		fields.add(0, new UIMetadataFieldDefault("sampleLabel",
				messages.getMessage("linelist.field.sampleLabel", new Object[] {}, locale), "text"));
		fields.add(0, new UIMetadataFieldDefault("projectLabel",
				messages.getMessage("linelist.field.projectLabel", new Object[] {}, locale), "text"));
		fields.add(0, new UIMetadataFieldDefault("created",
				messages.getMessage("linelist.field.created", new Object[] {}, locale), "date"));
		fields.add(0, new UIMetadataFieldDefault("modified",
				messages.getMessage("linelist.field.modified", new Object[] {}, locale), "date"));

		return fields;
	}

	/**
	 * Get a {@link List} of {@link Map} containing information from {@link MetadataEntry} for all
	 * {@link  Sample}s in a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link UISampleMetadata}s of all {@link Sample} metadata in a {@link Project}
	 */
	@RequestMapping(value = "/entries", method = RequestMethod.GET)
	@ResponseBody
	public List<UISampleMetadata> getProjectSamplesMetadataEntries(@RequestParam long projectId) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> projectSamples = sampleService.getSamplesForProject(project);
		return projectSamples.stream()
				.map(this::formatSampleMetadata)
				.collect(Collectors.toList());
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

		// The field label was transformed before passing it to the UI,
		// We must convert it back to its original state for look up.
		String field = AgGridUtilities.convertFieldToHeaderName(label);
		try {
			Map<MetadataTemplateField, MetadataEntry> metadata = sample.getMetadata();
			MetadataTemplateField templateField = metadataTemplateService.readMetadataFieldByLabel(field);
			if (templateField == null) {
				templateField = new MetadataTemplateField(field, "text");
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
		List<AgGridColumn> allFields = this.getProjectMetadataTemplateFields(projectId, locale);
		List<ProjectMetadataTemplateJoin> templateJoins = metadataTemplateService.getMetadataTemplatesForProject(
				project);
		List<UIMetadataTemplate> templates = new ArrayList<>();

		// Add a "Template" for all fields
		templates.add(new UIMetadataTemplate(-1L,
				messages.getMessage("linelist.templates.Select.none", new Object[] {}, locale), allFields));

		for (ProjectMetadataTemplateJoin join : templateJoins) {
			MetadataTemplate template = join.getObject();
			List<AgGridColumn> fields = template.getFields()
					.stream()
					.map(f -> new UIMetadataField(f, false, true))
					.collect(Collectors.toList());
			templates.add(new UIMetadataTemplate(template.getId(), template.getName(), fields));
		}

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
		String sampleLabel = messages.getMessage("linelist.field.sampleLabel", new Object[] {}, locale);

		// Get or create the template fields.
		List<MetadataTemplateField> fields = new ArrayList<>();
		for (AgGridColumn field : template.getFields()) {

			if (!field.getHeaderName()
					.equals(sampleLabel)) {
				MetadataTemplateField metadataTemplateField = metadataTemplateService.readMetadataFieldByLabel(
						field.getField());
				if (metadataTemplateField == null) {
					metadataTemplateField = metadataTemplateService.saveMetadataField(
							new MetadataTemplateField(field.getField(), "text"));
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
		return new UIMetadataTemplate(metadataTemplate.getId(), metadataTemplate.getName(),
				this.getProjectMetadataTemplateFields(projectId, locale));
	}

	/**
	 * Create a {@link UISampleMetadata} from a {@link Join}
	 *
	 * @param projectSampleJoin {@link Join} of {@link Project} and {@link Sample}
	 * @return {@link UISampleMetadata}
	 */
	private UISampleMetadata formatSampleMetadata(Join<Project, Sample> projectSampleJoin) {
		return new UISampleMetadata(projectSampleJoin.getSubject(), projectSampleJoin.getObject());
	}
}
