package ca.corefacility.bioinformatics.irida.ria.web.linelist;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.StaticMetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.web.components.agGrid.AgGridColumn;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.UIMetadataField;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.UIMetadataFieldDefault;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.UIMetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.UISampleMetadata;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
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
	private UpdateSamplePermission updateSamplePermission;

	@Autowired
	public LineListController(ProjectService projectService, SampleService sampleService,
			MetadataTemplateService metadataTemplateService, UpdateSamplePermission updateSamplePermission,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.updateSamplePermission = updateSamplePermission;
		this.messages = messageSource;
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
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		Project project = projectService.read(projectId);

		List<Join<Project, Sample>> projectSamples = sampleService.getSamplesForProject(project);
		return projectSamples.stream()
				.map(join -> {
					ProjectSampleJoin psj = (ProjectSampleJoin)join;
					return new UISampleMetadata(psj, updateSamplePermission.isAllowed(authentication, psj.getObject()));
				})
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
		List<UIMetadataTemplate> templates = new ArrayList<>();

		/*
		Need all MetadataTemplate fields (either already on the project, or in templates associated with the project).
		 */
		List<AgGridColumn> allFields = this.getProjectMetadataTemplateFields(projectId, locale);
		List<ProjectMetadataTemplateJoin> templateJoins = metadataTemplateService.getMetadataTemplatesForProject(
				project);

		// Add a "Template" for all fields
		templates.add(new UIMetadataTemplate(-1L,
				messages.getMessage("linelist.templates.Select.none", new Object[] {}, locale), allFields));

		for (ProjectMetadataTemplateJoin join : templateJoins) {
			MetadataTemplate template = join.getObject();
			List<AgGridColumn> allFieldsCopy = this.getProjectMetadataTemplateFields(projectId, locale);
			List<AgGridColumn> fields = formatTemplateForUI(template, allFieldsCopy);
			templates.add(new UIMetadataTemplate(template.getId(), template.getName(), fields));
		}

		return templates;
	}

	/**
	 * If there are any {@link UIMetadataFieldDefault} in a template that they are sent to the UI in a form that
	 * the interface knows how to handle (e.g. a "Created Date" that is saved to a template will have an ID, but
	 * the table will be looking for the field "irida-created" instead of "irida-##").
	 *
	 * @param field        {@link MetadataTemplateField}
	 * @return {@link AgGridColumn} of either {@link UIMetadataField} or {@link UIMetadataFieldDefault}
	 */
	private AgGridColumn mapFieldToColumn(MetadataTemplateField field) {
		if (field instanceof StaticMetadataTemplateField) {
			return new UIMetadataFieldDefault(field.getLabel(), field.getFieldKey(), field.getType());
		} else {
			return new UIMetadataField(field, false, true);
		}
	}

	/**
	 * Format a {@link MetadataTemplate} to be consumed by a UI instance of AgGrid.
	 *
	 * @param template  {@link MetadataTemplate}
	 * @param allFieldsAgGridColumns {@link List} of {@link AgGridColumn} - this is the "All Fields" template for the {@link Project}
	 * @return {@link List} of {@link AgGridColumn} that has all the fields in the project, but ones for this template are first
	 * and are the only ones that are not hidden in the UI
	 */
	private List<AgGridColumn> formatTemplateForUI(MetadataTemplate template, List<AgGridColumn> allFieldsAgGridColumns) {
		/*
		Need to remove the sample since allFields begins with the sample.
		 */
		allFieldsAgGridColumns.remove(0);

		/*
		Get a list of all the column field keys to facilitate faster look ups.
		 */
		List<String> allFieldsLabels = allFieldsAgGridColumns.stream()
				.map(AgGridColumn::getField)
				.collect(Collectors.toList());

		/*
		Create the new UI AgGridColumn template.
		 */
		List<AgGridColumn> templateAgGridColumns = new ArrayList<>();

		/*
		For each field in the template:
		1. find out where it is the the default template.
		2. Remove that column from the default template (allFieldsAgGridColumns) (any remaining at the end will be marked as hidden).
		3. Remove the label from the allFieldsLabels to maintain proper indexing.
		4. Create an AgGridColumn for the field and add it to the template.
		 */
		for (MetadataTemplateField field : template.getFields()) {
			int index = allFieldsLabels.indexOf(field.getFieldKey());
			allFieldsAgGridColumns.remove(index);
			allFieldsLabels.remove(index);
			templateAgGridColumns.add(mapFieldToColumn(field));
		}

		/*
		Since it the previous for loop we removed all of the current template fields from allFieldsAgGridColumns,
		we can assume the rest should be hidden and then just appended to the end of the template.
		 */
		allFieldsAgGridColumns.forEach(field -> {
			field.setHide(true);
			templateAgGridColumns.add(field);
		});
		return templateAgGridColumns;
	}

	/**
	 * Save or update a {@link MetadataTemplate}
	 *
	 * @param template  {@link UIMetadataTemplate}
	 * @param projectId {@link Long} project identifier
	 * @param locale {@link Locale}
	 * @param response  {@link HttpServletResponse}
	 * @return saved or updated {@link UIMetadataTemplate}
	 */
	@RequestMapping(value = "/templates", method = RequestMethod.POST)
	public UIMetadataTemplate saveLineListTemplate(@RequestBody UIMetadataTemplate template,
			@RequestParam Long projectId, Locale locale, HttpServletResponse response) {
		// Get or create the template fields.
		List<MetadataTemplateField> fields = new ArrayList<>();
		for (AgGridColumn field : template.getFields()) {
			// Don't save the sample label
			if (!field.getField()
					.equals(UISampleMetadata.SAMPLE_NAME)) {
				MetadataTemplateField metadataTemplateField = metadataTemplateService.readMetadataFieldByKey(
						field.getField());
				if (metadataTemplateField == null) {
					metadataTemplateField = metadataTemplateService.saveMetadataField(
							new MetadataTemplateField(field.getHeaderName(), field.getType()));
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
				formatTemplateForUI(metadataTemplate, getProjectMetadataTemplateFields(projectId, locale)));
	}

	/**
	 * Get a list of all {@link MetadataTemplateField}s on a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @param locale    {@link Locale}
	 * @return {@link List} of {@link UIMetadataField}
	 */
	public List<AgGridColumn> getProjectMetadataTemplateFields(@RequestParam long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		List<MetadataTemplateField> metadataFieldsForProject = metadataTemplateService.getMetadataFieldsForProject(
				project);
		Set<MetadataTemplateField> fieldSet = new HashSet<>(metadataFieldsForProject);

		// Need to get all the fields from the templates too!
		List<ProjectMetadataTemplateJoin> templateJoins = metadataTemplateService.getMetadataTemplatesForProject(
				project);

		/*
		IGNORED TEMPLATE FIELDS:
		These fields are ignored here because they are not part of sample metadata, but instead part of the
		sample object itself.  They are allowed to be saved into the template, but will be added separately below
		to ensure that they are displayed correctly in the UI.  These fields will be included in the templates
		sent down to the UI.
		 */
		List<StaticMetadataTemplateField> staticMetadataFields = metadataTemplateService.getStaticMetadataFields();

		/*
		Get all unique fields from the templates.
		 */
		for (ProjectMetadataTemplateJoin join : templateJoins) {
			MetadataTemplate template = join.getObject();
			List<MetadataTemplateField> templateFields = template.getFields();
			for (MetadataTemplateField field : templateFields) {
				if (!staticMetadataFields.contains(field)) {
					fieldSet.add(field);
				}
			}
		}

		List<AgGridColumn> fields = fieldSet.stream()
				.map(f -> new UIMetadataField(f, false, true))
				.sorted((f1, f2) -> f1.getHeaderName()
						.compareToIgnoreCase(f2.getHeaderName()))
				.collect(Collectors.toList());

		fields.add(0, new UIMetadataFieldDefault(messages.getMessage("linelist.field.created", new Object[] {}, locale),
				UISampleMetadata.CREATED_DATE, "date"));
		UIMetadataFieldDefault modifiedField = new UIMetadataFieldDefault(
				messages.getMessage("linelist.field.modified", new Object[] {}, locale), UISampleMetadata.MODIFIED_DATE,
				"date");
		modifiedField.setSort("asc");
		fields.add(0, modifiedField);

		UIMetadataFieldDefault sampleField = new UIMetadataFieldDefault(
				messages.getMessage("linelist.field.sampleLabel", new Object[] {}, locale),
				UISampleMetadata.SAMPLE_NAME, "text");
		sampleField.setPinned("left");
		sampleField.setLockPinned(true);
		sampleField.setLockPosition(true);
		fields.add(0, sampleField);

		/*
		This field is to display to the user any notification icons that they might have (e.g. sample is locked).
		 */
		UIMetadataFieldDefault iconField = new UIMetadataFieldDefault("", "icons", "text");
		iconField.setPinned("left");
		iconField.setLockPinned(true);
		iconField.setLockPosition(true);
		iconField.setCheckboxSelection(true);
		iconField.setHeaderCheckboxSelection(true);
		iconField.setSuppressFilter(true);
		iconField.setSuppressResize(true);
		fields.add(0, iconField);

		return fields;
	}
}
