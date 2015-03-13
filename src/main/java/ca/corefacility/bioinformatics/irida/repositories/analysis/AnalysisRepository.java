package ca.corefacility.bioinformatics.irida.repositories.analysis;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A custom repository for managing {@link Analysis} objects.
 * 
 *
 */
public interface AnalysisRepository extends IridaJpaRepository<Analysis, Long> {

	/**
	 * Load the set of {@link Analysis} for a {@link SequenceFile} that are a
	 * specific subtype of {@link Analysis}.
	 * 
	 * @param sf
	 *            the file to load {@link Analysis} records for.
	 * @param analysisType
	 *            the type of {@link Analysis} that should be loaded.
	 * @param <T> the type of analysis to load from the repository
	 * 
	 * @return the set of {@link Analysis} records of the specified type.
	 */
	@Query("select a from Analysis a where ?1 member of a.inputFiles and type(a) = ?2")
	public <T extends Analysis> Set<T> findAnalysesForSequenceFile(SequenceFile sf, Class<T> analysisType);

	/**
	 * Load the most recently created {@link Analysis} for a
	 * {@link SequenceFile} that is a specific subtype of {@link Analysis}.
	 * 
	 * @param sf
	 *            the file to load {@link Analysis} records for.
	 * @param analysisType
	 *            the type of {@link Analysis} that should be loaded.
	 * @param <T> the type of analysis to load from the repository
	 *
	 * @return the most recently created {@link Analysis} record of the
	 *         specified type.
	 */
	public default <T extends Analysis> T findMostRecentAnalysisForSequenceFile(SequenceFile sf, Class<T> analysisType) {
		List<T> list = findPageableAnalysisForSequenceFile(sf, analysisType,
				new PageRequest(0, 1, Sort.Direction.DESC, "createdDate")).getContent();
		if (list.size() == 0) {
			throw new EntityNotFoundException("No Analysis for SequenceFile [" + sf.getId() + "], of type "
					+ analysisType);
		} else {
			return list.get(0);
		}
	}

	/**
	 * Load a pageable list of {@link Analysis} for {@link SequenceFile} that
	 * are of a specific subtype of {@link Analysis}.
	 * 
	 * @param sf
	 *            the file to load {@link Analysis} records for.
	 * @param analysisType
	 *            the types of {@link Analysis} that should be loaded.
	 * @param page
	 *            the page that we should load.
	 * @param <T>
	 *            the type of analysis to load from the repository
	 * 
	 * @return the most recently created {@link Analysis} records of the
	 *         specified type.
	 */
	@Query("select a from Analysis a where ?1 member of a.inputFiles and type(a) = ?2")
	public <T extends Analysis> Page<T> findPageableAnalysisForSequenceFile(SequenceFile sf, Class<T> analysisType,
			Pageable page);
}
