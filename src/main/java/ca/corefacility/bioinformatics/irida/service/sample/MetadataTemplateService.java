package ca.corefacility.bioinformatics.irida.service.sample;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.StaticMetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

import java.util.List;
import java.util.Map;

/**
 * Service for managing {@link MetadataTemplate}s and related {@link Project}s
 */
public interface MetadataTemplateService extends CRUDService<Long, MetadataTemplate> {
	/**
	 * Create a new {@link MetadataTemplate} for a {@link Project}
	 * 
	 * @param template
	 *            the {@link MetadataTemplate} to create
	 * @param project
	 *            the {@link Project} to create the template in
	 * @return a {@link ProjectMetadataTemplateJoin}
	 */
	public ProjectMetadataTemplateJoin createMetadataTemplateInProject(MetadataTemplate template, Project project);

	/**
	 * Deleta a {@link MetadataTemplate} from a {@link Project}
	 *
	 * @param project
	 *            the {@link Project} to template lives in.
	 * @param id
	 *            the {@link Long} identifier for a {@link MetadataTemplate}
	 */
	public void deleteMetadataTemplateFromProject(Project project, Long id);

	/**
	 * Update a {@link MetadataTemplate} within a {@link Project}
	 * 
	 * @param metadataTemplate
	 *            {@link MetadataTemplate}
	 * @return {@link MetadataTemplate}
	 */
	public MetadataTemplate updateMetadataTemplateInProject(MetadataTemplate metadataTemplate);

	/**
	 * Get a list of {@link MetadataTemplate}s for a given {@link Project}
	 * 
	 * @param project
	 *            the {@link Project}
	 * @return a list of {@link ProjectMetadataTemplateJoin}
	 */
	public List<ProjectMetadataTemplateJoin> getMetadataTemplatesForProject(Project project);

	/**
	 * Get a {@link MetadataTemplateField} by its {@link Long} identifier
	 *
	 * @param id
	 *            {@link Long} identifier for a {@link MetadataTemplateField}
	 * @return {@link MetadataTemplateField}
	 */
	public MetadataTemplateField readMetadataField(Long id);

	/**
	 * Get a {@link MetadataTemplateField} by its {@link String} label
	 *
	 * @param label
	 *            the {@link String} label for the
	 *            {@link MetadataTemplateField}.
	 *
	 * @return {@link MetadataTemplateField}
	 */
	public MetadataTemplateField readMetadataFieldByLabel(String label);

	/**
	 * Read a {@link MetadataTemplateField} by its key
	 *
	 * @param key key for the field
	 * @return a {@link MetadataTemplateField}
	 */
	public MetadataTemplateField readMetadataFieldByKey(String key);

	/**
	 * Get a list of all {@link StaticMetadataTemplateField}s available
	 *
	 * @return a list of {@link StaticMetadataTemplateField}
	 */
	public List<StaticMetadataTemplateField> getStaticMetadataFields();

	/**
	 * Save a new metadata fields
	 *
	 * @param field
	 *            the {@link MetadataTemplateField} to save.
	 *
	 * @return the saved {@link MetadataTemplateField}
	 */
	public MetadataTemplateField saveMetadataField(MetadataTemplateField field);

	/**
	 * Get a list of all {@link MetadataTemplateField}s that contain the query
	 *
	 * @param query
	 *            the {@link String} to search labels for.
	 *
	 * @return {@link List} of {@link MetadataTemplateField}
	 */
	public List<MetadataTemplateField> getAllMetadataFieldsByQueryString(String query);

	/**
	 * Get the appropriate {@link MetadataTemplateField}s and {@link MetadataEntry}s for a given map of Strings
	 *
	 * @param metadata the strings to convert
	 * @return a Map of {@link MetadataTemplateField}s and {@link MetadataEntry}s
	 */
	public Map<MetadataTemplateField, MetadataEntry> getMetadataMap(Map<String, MetadataEntry> metadata);

	/**
	 * Get all the {@link MetadataTemplateField}s on a given {@link Project}
	 *
	 * @param project the Project to get fields for
	 * @return a list of fields
	 */
	public List<MetadataTemplateField> getMetadataFieldsForProject(Project project);
}
