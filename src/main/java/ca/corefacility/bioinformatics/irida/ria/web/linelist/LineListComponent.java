package ca.corefacility.bioinformatics.irida.ria.web.linelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.models.UIMetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.models.UIMetadataTemplateField;
import ca.corefacility.bioinformatics.irida.ria.web.models.UISampleMetadata;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Component to handle data manipulation for the {@link LineListController}
 */
@Component
public class LineListComponent {
	private ProjectService projectService;
	private SampleService sampleService;
	private MetadataTemplateService templateService;
	private MessageSource messages;

	@Autowired
	public LineListComponent(ProjectService projectService, SampleService sampleService,
			MetadataTemplateService templateService, MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.templateService = templateService;
		this.messages = messageSource;
	}

	/**
	 * Get a {@link List} of all {@link UIMetadataTemplateField} belonging to a {@link Project}
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @param locale    {@link Locale}
	 * @return {@link List} of {@link UIMetadataTemplateField}
	 */
	List<UIMetadataTemplateField> getProjectMetadataFields(Long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		List<MetadataTemplateField> metadataFieldsForProject = templateService.getMetadataFieldsForProject(project);

		List<UIMetadataTemplateField> fields = metadataFieldsForProject.stream()
				.map(f -> formatMetadataTemplateField(f, locale))
				.sorted((f1, f2) -> f1.getHeaderName()
						.compareToIgnoreCase(f2.getHeaderName()))
				.collect(Collectors.toList());

		// Add the sample name, project name, created date and the modified date
		MetadataTemplateField sampleLabelField = new MetadataTemplateField("sampleLabel", "text");
		UIMetadataTemplateField uiSampleField = new UIMetadataTemplateField(sampleLabelField,
				messages.getMessage("linelist.field.sampleLabel", new Object[] {}, locale), false, false);
		fields.add(0, uiSampleField);

		MetadataTemplateField projectLabelField = new MetadataTemplateField("projectLabel", "text");
		UIMetadataTemplateField uiProjectField = new UIMetadataTemplateField(projectLabelField,
				messages.getMessage("linelist.field.projectLabel", new Object[] {}, locale), false, true);
		fields.add(0, uiProjectField);

		MetadataTemplateField createdField = new MetadataTemplateField("created", "date");
		UIMetadataTemplateField uiCreatedField = new UIMetadataTemplateField(createdField,
				messages.getMessage("linelist.field.created", new Object[] {}, locale), false, false);
		fields.add(0, uiCreatedField);

		MetadataTemplateField modifiedField = new MetadataTemplateField("modified", "date");
		UIMetadataTemplateField uiModifiedField = new UIMetadataTemplateField(modifiedField,
				messages.getMessage("linelist.field.modified", new Object[] {}, locale), false, false);
		fields.add(0, uiModifiedField);

		return fields;
	}

	/**
	 * Get a list of {@link Sample} metadata
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @return {@link List} of {@link UISampleMetadata}
	 */
	List<UISampleMetadata> getProjectSampleMetadata(Long projectId) {
		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> projectSamples = sampleService.getSamplesForProject(project);
		return projectSamples.stream()
				.map(this::formatSampleMetadata)
				.collect(Collectors.toList());
	}

	/**
	 * Get a {@link List} of {@link MetadataTemplate} for a {@link Project} for the linelist table
	 *
	 * @param projectId {@link Long} identifier for a {@link Project}
	 * @param locale    {@link Locale}
	 * @return {@link List} of {@link UIMetadataTemplate}
	 */
	List<UIMetadataTemplate> getProjectMetadataTemplates(Long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		List<UIMetadataTemplateField> allFields = this.getProjectMetadataFields(projectId, locale);
		List<ProjectMetadataTemplateJoin> templateJoins = templateService.getMetadataTemplatesForProject(project);
		List<UIMetadataTemplate> templates = new ArrayList<>();

		// Add a "Template" for all fields
		templates.add(new UIMetadataTemplate(-1L,
				messages.getMessage("linelist.templates.Select.none", new Object[] {}, locale), allFields));

		for (ProjectMetadataTemplateJoin join : templateJoins) {
			MetadataTemplate template = join.getObject();
			List<UIMetadataTemplateField> fields = template.getFields()
					.stream()
					.map(f -> this.formatMetadataTemplateField(f, locale))
					.collect(Collectors.toList());
			templates.add(new UIMetadataTemplate(template.getId(), template.getName(), fields));
		}

		return templates;

	}

	/**
	 * Format a {@link UIMetadataTemplateField} from a {@link MetadataTemplateField}
	 *
	 * @param field  {@link MetadataTemplateField}
	 * @param locale {@link Locale}
	 * @return {@link UIMetadataTemplateField}
	 */
	private UIMetadataTemplateField formatMetadataTemplateField(MetadataTemplateField field, Locale locale) {
		String headerName;
		try {
			headerName = messages.getMessage("linelist.field." + field.getLabel(), new Object[] {}, locale);
		} catch (NoSuchMessageException e) {
			headerName = field.getLabel();
		}
		return new UIMetadataTemplateField(field, headerName, true, false);
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
