package ca.corefacility.bioinformatics.irida.service.sample;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.StaticMetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Service for managing {@link MetadataTemplate}s and related {@link Project}s
 */
public interface MetadataTemplateService extends CRUDService<Long, MetadataTemplate> {
	/**
	 * Create a new {@link MetadataTemplate} for a {@link Project}
	 *
	 * @param template the {@link MetadataTemplate} to create
	 * @param project  the {@link Project} to create the template in
	 * @return a {@link MetadataTemplate}
	 */
	public MetadataTemplate createMetadataTemplateInProject(MetadataTemplate template, Project project);

	/**
	 * Deleta a {@link MetadataTemplate} from a {@link Project}
	 *
	 * @param project the {@link Project} to template lives in.
	 * @param id      the {@link Long} identifier for a {@link MetadataTemplate}
	 */
	public void deleteMetadataTemplateFromProject(Project project, Long id);

	/**
	 * Update a {@link MetadataTemplate} within a {@link Project}
	 *
	 * @param metadataTemplate {@link MetadataTemplate}
	 * @return {@link MetadataTemplate}
	 */
	public MetadataTemplate updateMetadataTemplateInProject(MetadataTemplate metadataTemplate);

	/**
	 * Get a list of {@link MetadataTemplate}s for a given {@link Project}
	 *
	 * @param project the {@link Project}
	 * @return a list of {@link MetadataTemplate}
	 */
	public List<MetadataTemplate> getMetadataTemplatesForProject(Project project);

	/**
	 * Get a {@link MetadataTemplateField} by its {@link Long} identifier
	 *
	 * @param id {@link Long} identifier for a {@link MetadataTemplateField}
	 * @return {@link MetadataTemplateField}
	 */
	public MetadataTemplateField readMetadataField(Long id);

	/**
	 * Get a {@link MetadataTemplateField} by its {@link String} label
	 *
	 * @param label the {@link String} label for the
	 *              {@link MetadataTemplateField}.
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
	 * @param field the {@link MetadataTemplateField} to save.
	 * @return the saved {@link MetadataTemplateField}
	 */
	public MetadataTemplateField saveMetadataField(MetadataTemplateField field);

	/**
	 * Get a list of all {@link MetadataTemplateField}s that contain the query
	 *
	 * @param query the {@link String} to search labels for.
	 * @return {@link List} of {@link MetadataTemplateField}
	 */
	public List<MetadataTemplateField> getAllMetadataFieldsByQueryString(String query);

	/**
	 * Get the appropriate {@link MetadataTemplateField}s and {@link MetadataEntry}s for a given map of Strings
	 *
	 * @param metadataMap the strings to convert
	 * @return a Set of {@link MetadataEntry}s
	 */
	public Set<MetadataEntry> convertMetadataStringsToSet(Map<String, MetadataEntry> metadataMap);

	/**
	 * Get all the {@link MetadataTemplateField}s on a given {@link Project}
	 *
	 * @param project the Project to get fields for
	 * @return a list of fields
	 */
	@Deprecated
	public List<MetadataTemplateField> getMetadataFieldsForProject(Project project);

	/**
	 * Get the {@link MetadataRestriction} for the given {@link MetadataTemplateField} and {@link Project}
	 *
	 * @param project the {@link Project} to get the restriction for
	 * @param field   the {@link MetadataTemplateField} to get the restriction for
	 * @return The found {@link MetadataRestriction}.  If there is no restriction defined, this may be null
	 */
	public MetadataRestriction getMetadataRestrictionForFieldAndProject(Project project, MetadataTemplateField field);

	/**
	 * List all the {@link MetadataRestriction} for the given {@link Project}
	 *
	 * @param project the {@link Project} the Project to get restrictions for
	 * @return a List of all defined {@link MetadataRestriction}
	 */
	public List<MetadataRestriction> getMetadataRestrictionsForProject(Project project);

	/**
	 * Adds or updates the {@link MetadataRestriction} for a project and field
	 *
	 * @param project the {@link Project} to add a restriction for
	 * @param field   the field to set a restriction on
	 * @param role    the role level to set the restriction for
	 * @return the created {@link MetadataRestriction}
	 */
	public MetadataRestriction setMetadataRestriction(Project project, MetadataTemplateField field, ProjectMetadataRole role);

	/**
	 * Get all {@link MetadataTemplateField} the current user is allowed to read for a {@link MetadataTemplate}
	 * @param template the {@link MetadataTemplate} to get fields for
	 * @return a list of {@link MetadataTemplateField}
	 */
	public List<MetadataTemplateField> getPermittedFieldsForTemplate(MetadataTemplate template);

	/**
	 * Get all {@link MetadataTemplateField} that the currently logged in user is allowed to read
	 *
	 * @param project               the {@link Project} to request fields from
	 * @param includeTemplateFields whether to include fields from the project's associated {@link MetadataTemplate}s
	 * @return a list of {@link MetadataTemplateField} collecting the allowed {@link MetadataTemplateField}
	 */
	public List<MetadataTemplateField> getPermittedFieldsForCurrentUser(Project project, boolean includeTemplateFields);

	/**
	 * Get the default {@link MetadataTemplate} for the given {@link Project} (if one exists)
	 *
	 * @param project the {@link Project} to get the template for
	 * @return the default {@link MetadataTemplate} if one is set
	 */
	public MetadataTemplate getDefaultTemplateForProject(Project project);

	/**
	 * Update which {@link MetadataTemplate} is the default for the given {@link Project}
	 *
	 * @param project  the {@link Project} to set a template on
	 * @param template the {@link MetadataTemplate} to set
	 * @return the new default {@link MetadataTemplate}
	 */
	public MetadataTemplate updateDefaultMetadataTemplateForProject(Project project, MetadataTemplate template);

	/**
	 * Remove the default {@link MetadataTemplate} from the project.  It will now use all fields as default
	 *
	 * @param project the {@link Project} to remove the template from
	 */
	public void removeDefaultMetadataTemplateForProject(Project project);
}
