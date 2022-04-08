package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

//ISS
import ca.corefacility.bioinformatics.irida.model.project.Project;
import java.util.List;

/**
 * Implementation of custom repository methods for {@link Sample}s
 */
public class SampleRepositoryImpl implements SampleRepositoryCustom {
	private final EntityManager entityManager;

	@Autowired
	public SampleRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public void updateSampleModifiedDate(Sample sample, Date modifiedDate) {
		Query query = entityManager.createNativeQuery("UPDATE sample SET modifiedDate = ? where id = ?");
		query.setParameter(1, modifiedDate);
		query.setParameter(2, sample.getId());
		query.executeUpdate();
	}

	/**ISS
	 * {@inheritDoc}
	 */
 	public List<Long> getSampleIdsByCodeInProject(Project project, List<String> sampleCodes) {
		//query to read samples for a project
		Query query = entityManager.createQuery("SELECT s.id FROM sample s INNER JOIN project_sample p ON p.sample_id=s.id INNER JOIN metadata_entry AS me ON s.id=me.sample_id " +
			"WHERE p.project_id=? AND me.field_id=8 AND me.value IN (?)");
		query.setParameter(1, project.getId());
		query.setParameter(2, sampleCodes);

		List<Long> results = query.getResultList();
		return results;
	}

	/**ISS
	 * {@inheritDoc}
	 */
 	public String getClusterIdByCodes(Project project, List<String> sampleCodes) {
		Query query = entityManager.createQuery("SELECT mec.value FROM metadata_entry as mec " + 
			"INNER JOIN metadata_entry as mes on mec.sample_id = mes.sample_id " +
			"WHERE mec.field_id = 7 AND mes.field_id = 8 AND mes.value IN (?) " +
			"ORDER BY ABS(length(mec.value) - length(replace(mec.value, '_', ''))-1.4) LIMIT 1");
		query.setParameter(1, sampleCodes);
		String result = (String) query.getSingleResult();
		return result;
	}

 	public Long getMasterProjectIdByCode(String sampleCode) {
		Query query = entityManager.createQuery("SELECT s.organism FROM sample AS s " +
			"INNER JOIN metadata_entry AS mes ON s.id = mes.sample_id " +
			"WHERE mes.field_id = 8 AND mes.value = ?");
		query.setParameter(1, sampleCode);
		String result = (String) query.getSingleResult();
		Long projectId = 3L;
		if(result.equals("Listeria monocytogenes")){
			projectId = 4L;
		}
		if(result.equals("Coronavirus")){
			projectId = 1L;
		}
		return projectId;
	}

 	public void setClusterIdByCode(Project project, List<String> sampleCodes, String clusterId) {
		Query query = entityManager.createNativeQuery("UPDATE metadata_entry AS mec " +
			"INNER JOIN metadata_entry as mes on mec.sample_id = mes.sample_id " +
			"SET mec.value = ? WHERE mec.field_id = 7 AND mes.field_id = 8 AND mes.value IN (?)");
		query.setParameter(1, clusterId);
		query.setParameter(2, sampleCodes);
		query.executeUpdate();
	}

 	public String getNextClusterId(Project project) {
		Query query = entityManager.createQuery("SELECT MAX(CAST(SUBSTRING(mec.value,9) AS int))+1 FROM metadata_entry AS mec WHERE mec.field_id = 7");
		String result = (String) query.getSingleResult();
		return "Cluster_" + result;
	}

	public List<String> getRecipientsByCodes(Project project, List<String> sampleCodes, Boolean isAlert) {
		Query query;

		//query to read samples for a project
		if (isAlert) {
			query = entityManager.createQuery("select DISTINCT u.email FROM project_sample ps INNER JOIN project_user pu ON pu.project_id=ps.project_id " +
				"INNER JOIN user u ON u.id=pu.user_id INNER JOIN metadata_entry AS mes ON ps.sample_id=mes.sample_id " +
				"WHERE mes.field_id = 8 AND mes.value IN (?)");
		} else {
			query = entityManager.createQuery("select DISTINCT u.email FROM project_sample ps INNER JOIN project_user pu ON pu.project_id=ps.project_id " +
				"INNER JOIN user u ON u.id=pu.user_id INNER JOIN metadata_entry AS mes ON ps.sample_id=mes.sample_id " +
				"WHERE mes.field_id = 8 AND mes.value IN (?) AND u.system_role <>'ROLE_MANAGER'");
		}
		query.setParameter(1, sampleCodes);

		List<String> results = query.getResultList();
		return results;		
	}

	public List<Sample> getSamplesForClusterShallow(Project project, String sampleCode, String clusterId) {
		Query query;

		if (clusterId.contains("_ext")) {
			query = entityManager.createQuery("select s.id, s.createdDate, s.modifiedDate, s.description, s.sampleName, s.collectedBy, s.geographicLocationName, s.isolate, s.isolationSource, s.latitude, s.longitude, s.organism, s.strain, s.collectionDate, s.arrivalDate, null as remote_status " +
				"FROM sample AS s " +
				"INNER JOIN metadata_entry AS mec ON mec.sample_id = s.id " +
				"WHERE mec.value = ? AND mec.field_id = 7");
			query.setParameter(1, clusterId.substring(0, clusterId.length() - 4));
		} else if (clusterId.contains("Cluster_")) {
			query = entityManager.createQuery("select s.id, s.createdDate, s.modifiedDate, s.description, s.sampleName, s.collectedBy, s.geographicLocationName, s.isolate, s.isolationSource, s.latitude, s.longitude, s.organism, s.strain, s.collectionDate, s.arrivalDate, null as remote_status " +
				"FROM sample AS s " +
				"INNER JOIN metadata_entry AS mec ON mec.sample_id = s.id " +
				"WHERE mec.value = ? AND mec.field_id = 7");
			query.setParameter(1, clusterId);
		} else {
			query = entityManager.createQuery("select s.id, s.createdDate, s.modifiedDate, s.description, s.sampleName, s.collectedBy, s.geographicLocationName, s.isolate, s.isolationSource, s.latitude, s.longitude, s.organism, s.strain, s.collectionDate, s.arrivalDate, null as remote_status " +
				"FROM sample AS s " +
				"INNER JOIN metadata_entry AS mes ON mes.sample_id = s.id " +
				"WHERE mes.value = ? AND mes.field_id = 8");
			query.setParameter(1, sampleCode);
		}

		List<Sample> results =  query.getResultList();
		return results;
	}

}
