package ca.corefacility.bioinformatics.irida.repositories.sample;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Custom repository methods for getting {@link MetadataTemplateField}s
 */
public class MetadataFieldRepositoryImpl implements MetadataFieldRepositoryCustom {

	private final EntityManager entityManager;

	@Autowired
	public MetadataFieldRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MetadataTemplateField> getMetadataFieldsForProject(Project p) {

		String queryString = "SELECT DISTINCT f.* FROM project_sample p INNER JOIN sample_metadata_entry s ON p.sample_id=s.sample_id INNER JOIN metadata_field f ON s.metadata_KEY=f.id WHERE p.project_id=:project";
		Query nativeQuery = entityManager.createNativeQuery(queryString, MetadataTemplateField.class);

		nativeQuery.setParameter("project", p.getId());

		return nativeQuery.getResultList();
	}
}
