package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for storing and retrieving {@link SequencingObject}s
 */
public interface SequencingObjectRepository extends IridaJpaRepository<SequencingObject, Long> {

	/**
	 * Get the {@link SequencingObject}s for a given {@link SequencingRun}
	 * 
	 * @param sequencingRun
	 *            the {@link SequencingRun}
	 * @return a set of {@link SequencingObject}
	 */
	@Query("select f from SequencingObject f where f.sequencingRun = ?1")
	public Set<SequencingObject> findSequencingObjectsForSequencingRun(SequencingRun sequencingRun);

	/**
	 * Get the {@link SequencingObject}s associated with a given
	 * {@link AnalysisSubmission}
	 * 
	 * @param analysisSubmission
	 *            the {@link AnalysisSubmission}
	 * @return the set of associated {@link SequencingObject}s
	 */
	@Query("select f from SequencingObject f where ?1 IN elements(f.analysisSubmissions)")
	public Set<SequencingObject> findSequencingObjectsForAnalysisSubmission(AnalysisSubmission analysisSubmission);
}
