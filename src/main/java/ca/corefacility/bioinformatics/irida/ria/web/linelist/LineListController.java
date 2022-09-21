package ca.corefacility.bioinformatics.irida.ria.web.linelist;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.StaticMetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.ProjectMetadataResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.agGrid.AgGridColumn;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.*;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

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
	private ProjectOwnerPermission projectOwnerPermission;

	@Autowired
	public LineListController(ProjectService projectService, SampleService sampleService,
			MetadataTemplateService metadataTemplateService, ProjectOwnerPermission projectOwnerPermission,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.projectOwnerPermission = projectOwnerPermission;
		this.messages = messageSource;
	}

	/**
	 * Get a {@link List} of {@link Map} containing information from {@link MetadataEntry} for a {@link Page} of
	 * {@link Sample}s in a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @param current   the number of the {@link Page}
	 * @param pageSize  the size of the {@link Page}
	 * @return {@link List} of {@link UISampleMetadata}s of all {@link Sample} metadata in a {@link Project}
	 */
	@RequestMapping(value = "/entries", method = RequestMethod.GET)
	@ResponseBody
	public EntriesResponse getProjectSamplesMetadataEntries(@RequestParam long projectId, @RequestParam int current,
			@RequestParam int pageSize) {
		Project project = projectService.read(projectId);
		List<UISampleMetadata> projectSamplesMetadata = new ArrayList<>();

		List<Long> lockedSamplesInProject = sampleService.getLockedSamplesInProject(project);

		//default sort for the samples in the project
		Sort sort = Sort.by(Sort.Direction.DESC, "sample.modifiedDate");

		//fetch a page of samples at a time for the project
		Page<ProjectSampleJoin> page = sampleService.getFilteredSamplesForProjects(Arrays.asList(project),
				Collections.emptyList(), "", "", "", null, null, current, pageSize, sort);
		List<Sample> samples = page.stream().map(ProjectSampleJoin::getObject).collect(Collectors.toList());
		List<Long> sampleIds = samples.stream().map(Sample::getId).collect(Collectors.toList());

		List<MetadataTemplateField> metadataTemplateFields = metadataTemplateService
				.getPermittedFieldsForCurrentUser(project, true);

		Map<Long, Set<MetadataEntry>> metadataForProject;

		//check that we have some fields
		if (!metadataTemplateFields.isEmpty()) {
			//if we have fields, get all the metadata
			ProjectMetadataResponse metadataResponse = sampleService.getMetadataForProjectSamples(project, sampleIds,
					metadataTemplateFields);

			metadataForProject = metadataResponse.getMetadata();
		} else {
			//if we have no fields, just give an empty map.  We'll just show the date fields
			metadataForProject = new HashMap<>();
		}

		//for each sample
		for (Sample s : samples) {
			//get the metadata for that sample
			Set<MetadataEntry> metadata = metadataForProject.getOrDefault(s.getId(), new HashSet<>());

			//check if the project owns the sample
			boolean ownership = !lockedSamplesInProject.contains(s.getId());

			projectSamplesMetadata.add(new UISampleMetadata(project, s, ownership, metadata));
		}

		return new EntriesResponse(page.getTotalElements(), projectSamplesMetadata);
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
			// find the field
			MetadataTemplateField templateField = metadataTemplateService.readMetadataFieldByLabel(label);
			if (templateField == null) {
				templateField = new MetadataTemplateField(label, "text");
				metadataTemplateService.saveMetadataField(templateField);
			}

			// create and merge the new entry in
			MetadataEntry entry = new MetadataEntry(value, "text", templateField);

			//update the sample
			sampleService.mergeSampleMetadata(sample, Sets.newHashSet(entry));
			response.setStatus(HttpServletResponse.SC_OK);
			return "SUCCESS";
		} catch (EntityExistsException | EntityNotFoundException | ConstraintViolationException
				| InvalidPropertyException e) {
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
		boolean canEdit = canUserEdit(project);
		List<UIMetadataTemplate> templates = new ArrayList<>();

		/*
		 * Need all MetadataTemplate fields (either already on the project, or
		 * in templates associated with the project).
		 */
		List<MetadataTemplate> templateJoins = metadataTemplateService.getMetadataTemplatesForProject(project);
		List<AgGridColumn> allFields = this.getProjectMetadataTemplateFields(projectId, locale);

		// Add a "Template" for all fields
		templates.add(new UIMetadataTemplate(-1L,
				messages.getMessage("linelist.templates.Select.none", new Object[] {}, locale), allFields));

		for (MetadataTemplate template : templateJoins) {
			List<AgGridColumn> fields = formatTemplateForUI(template, allFields, canEdit);
			templates.add(new UIMetadataTemplate(template.getId(), template.getName(), fields));
		}

		return templates;
	}

	/**
	 * If there are any {@link UIMetadataFieldDefault} in a template that they are sent to the UI in a form that the
	 * interface knows how to handle (e.g. a "Created Date" that is saved to a template will have an ID, but the table
	 * will be looking for the field "irida-created" instead of "irida-##").
	 *
	 * @param field {@link MetadataTemplateField}
	 * @return {@link AgGridColumn} of either {@link UIMetadataField} or {@link UIMetadataFieldDefault}
	 */
	private AgGridColumn mapFieldToColumn(MetadataTemplateField field, boolean canEdit) {
		if (field instanceof StaticMetadataTemplateField) {
			return new UIMetadataFieldDefault(field.getLabel(), field.getFieldKey(), field.getType());
		} else {
			return new UIMetadataField(field, false, canEdit);
		}
	}

	/**
	 * Format a {@link MetadataTemplate} to be consumed by a UI instance of AgGrid.
	 *
	 * @param template               {@link MetadataTemplate}
	 * @param allFieldsAgGridColumns {@link List} of {@link AgGridColumn} - this is the "All Fields" template for the
	 *                               {@link Project}
	 * @return {@link List} of {@link AgGridColumn} that has all the fields in the project, but ones for this template
	 *         are first and are the only ones that are not hidden in the UI
	 */
	private List<AgGridColumn> formatTemplateForUI(MetadataTemplate template,
			final List<AgGridColumn> allFieldsAgGridColumns, boolean canEdit) {

		AgGridColumn iconCol = allFieldsAgGridColumns.get(0);
		AgGridColumn sampleNameCol = allFieldsAgGridColumns.get(1);

		List<MetadataTemplateField> permittedFieldsForTemplate = metadataTemplateService
				.getPermittedFieldsForTemplate(template);

		/*
		Get a list of all the column field keys to facilitate faster look ups.
		 */
		List<String> allFieldsLabels = allFieldsAgGridColumns.stream()
				.map(AgGridColumn::getField)
				.collect(Collectors.toList());

		/*
		 * Create the new UI AgGridColumn template.
		 */
		List<AgGridColumn> templateAgGridColumns = new ArrayList<>();
		List<String> templateFieldsLabels = new ArrayList<>();

		// Add the "icon" and "sampleName" to the template columns
		templateAgGridColumns.add(iconCol);
		templateAgGridColumns.add(sampleNameCol);

		/*
		For each field in the template:
		1. Create an AgGridColumn for the field and add it to the template.
		2. Add the fieldKey to the templateFieldsLabels list for use later in hiding non template fields.
		 */
		for (MetadataTemplateField field : permittedFieldsForTemplate) {
			// Need to add parameter for if they have permissions to edit.
			templateAgGridColumns.add(mapFieldToColumn(field, canEdit));
			templateFieldsLabels.add(field.getFieldKey());
		}

		/*
		Hide all fields that are not present in the template.
		*/
		allFieldsLabels.forEach(fieldKey -> {
			// Hide the field which is not present in our template
			if (!templateFieldsLabels.contains(fieldKey)) {
				AgGridColumn field = allFieldsAgGridColumns.get(allFieldsLabels.indexOf(fieldKey));
				AgGridColumn fieldCopy = new AgGridColumn(field.getHeaderName(), fieldKey, field.getType(), true,
						field.isEditable());
				templateAgGridColumns.add(fieldCopy);
			}
		});

		return templateAgGridColumns;
	}

	/**
	 * Save or update a {@link MetadataTemplate}
	 *
	 * @param template  {@link UIMetadataTemplate}
	 * @param projectId {@link Long} project identifier
	 * @param locale    {@link Locale}
	 * @param response  {@link HttpServletResponse}
	 * @return saved or updated {@link UIMetadataTemplate}
	 */
	@RequestMapping(value = "/templates", method = RequestMethod.POST)
	public UIMetadataTemplate saveLineListTemplate(@RequestBody UIMetadataTemplate template,
			@RequestParam Long projectId, Locale locale, HttpServletResponse response) {
		Project project = projectService.read(projectId);

		// Get or create the template fields.
		List<MetadataTemplateField> fields = new ArrayList<>();
		for (AgGridColumn field : template.getFields()) {
			// Don't save the sample label
			if (!field.getField().equals(UISampleMetadata.SAMPLE_NAME)) {
				MetadataTemplateField metadataTemplateField = metadataTemplateService
						.readMetadataFieldByKey(field.getField());
				if (metadataTemplateField == null) {
					String type = Strings.isNullOrEmpty(field.getType()) ? "text" : field.getType();
					metadataTemplateField = metadataTemplateService
							.saveMetadataField(new MetadataTemplateField(field.getHeaderName(), type));
				}
				fields.add(metadataTemplateField);
			}
		}

		// Save the template.
		MetadataTemplate metadataTemplate;
		if (template.getId() == null) {
			// NO ID means that this is a new template
			metadataTemplate = new MetadataTemplate(template.getName(), fields);
			metadataTemplate = metadataTemplateService.createMetadataTemplateInProject(metadataTemplate, project);

			response.setStatus(HttpServletResponse.SC_CREATED);
		} else {
			metadataTemplate = metadataTemplateService.read(template.getId());
			metadataTemplate.setFields(fields);
			metadataTemplate = metadataTemplateService.updateMetadataTemplateInProject(metadataTemplate);
			response.setStatus(HttpServletResponse.SC_OK);
		}
		return new UIMetadataTemplate(metadataTemplate.getId(), metadataTemplate.getName(), formatTemplateForUI(
				metadataTemplate, getProjectMetadataTemplateFields(projectId, locale), canUserEdit(project)));
	}

	/**
	 * Get a list of all {@link MetadataTemplateField}s on a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @param locale    {@link Locale}
	 * @return {@link List} of {@link UIMetadataField}
	 */
	public List<AgGridColumn> getProjectMetadataTemplateFields(long projectId, Locale locale) {
		Project project = projectService.read(projectId);

		List<MetadataTemplateField> permittedFieldsForCurrentUser = metadataTemplateService
				.getPermittedFieldsForCurrentUser(project, true);

		/*
		 * IGNORED TEMPLATE FIELDS: These fields are ignored here because they
		 * are not part of sample metadata, but instead part of the sample
		 * object itself. They are allowed to be saved into the template, but
		 * will be added separately below to ensure that they are displayed
		 * correctly in the UI. These fields will be included in the templates
		 * sent down to the UI.
		 */
		List<StaticMetadataTemplateField> staticMetadataFields = metadataTemplateService.getStaticMetadataFields();

		//Removing fields that are in the staticMetadataFields above
		List<MetadataTemplateField> fieldSet = permittedFieldsForCurrentUser.stream()
				.filter(f -> !staticMetadataFields.contains(f))
				.collect(Collectors.toList());

		List<AgGridColumn> fields = fieldSet.stream()
				.map(f -> new UIMetadataField(f, false, true))
				.sorted((f1, f2) -> f1.getHeaderName().compareToIgnoreCase(f2.getHeaderName()))
				.collect(Collectors.toList());

		fields.add(0, new UIMetadataFieldDefault(messages.getMessage("linelist.field.created", new Object[] {}, locale),
				UISampleMetadata.CREATED_DATE, "date"));
		UIMetadataFieldDefault modifiedField = new UIMetadataFieldDefault(
				messages.getMessage("linelist.field.modified", new Object[] {}, locale), UISampleMetadata.MODIFIED_DATE,
				"date");
		modifiedField.setSort("desc");
		fields.add(0, modifiedField);

		UIMetadataFieldDefault sampleField = new UIMetadataFieldDefault(
				messages.getMessage("linelist.field.sampleLabel", new Object[] {}, locale),
				UISampleMetadata.SAMPLE_NAME, "text");
		sampleField.setPinned("left");
		sampleField.setLockPinned(true);
		sampleField.setLockPosition(true);
		fields.add(0, sampleField);

		/*
		 * This field is to display to the user any notification icons that they
		 * might have (e.g. sample is locked).
		 */
		UIMetadataFieldDefault iconField = new UIMetadataFieldDefault("", "icons", "text");
		iconField.setPinned("left");
		iconField.setLockPinned(true);
		iconField.setLockPosition(true);
		iconField.setCheckboxSelection(true);
		iconField.setHeaderCheckboxSelection(true);
		iconField.setResizable(false);
		fields.add(0, iconField);

		return fields;
	}

	/**
	 * Check to see if the currently logged in user has permission to edit {@link MetadataEntry} on the current project
	 *
	 * @param project {@link Project}
	 * @return {@link Boolean} true if user can edit on the current project
	 */
	private boolean canUserEdit(Project project) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return projectOwnerPermission.isAllowed(authentication, project);
	}
}
