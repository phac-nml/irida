package ca.corefacility.bioinformatics.irida.service.sample;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataField;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

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
	 * Get a list of {@link MetadataTemplate}s for a given {@link Project}
	 * 
	 * @param project
	 *            the {@link Project}
	 * @return a list of {@link ProjectMetadataTemplateJoin}
	 */
	public List<ProjectMetadataTemplateJoin> getMetadataTemplatesForProject(Project project);
	
	public MetadataField readMetadataField(Long id);

	/**
	 * Get a {@link MetadataField} by its {@link String} label
	 *
	 * @param label
	 * 		the {@link String} label for the {@link MetadataField}.
	 *
	 * @return {@link MetadataField}
	 */
	public MetadataField readMetadataFieldByLabel(String label);

	/**
	 * Save a new metadata fields
	 *
	 * @param field
	 * 		the {@link MetadataField} to save.
	 *
	 * @return the saved {@link MetadataField}
	 */
	public MetadataField saveMetadataField(MetadataField field);

	/**
	 * Get a list of all {@link MetadataField}s that contain the query
	 *
	 * @param query
	 * 		the {@link String} to search labels for.
	 *
	 * @return {@link List} of {@link MetadataField}
	 */
	public List<MetadataField> getAllMetadataFieldsByQueryString(String query);
}
