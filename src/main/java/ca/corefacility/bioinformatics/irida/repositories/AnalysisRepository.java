package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.repositories.pagingsortingspecification.PagingSortingSpecificationRepository;

/**
 * A custom repository for managing {@link Analysis} objects.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface AnalysisRepository extends PagingSortingSpecificationRepository<Analysis, Long> {

	/**
	 * Load the set of {@link Analysis} for a {@link SequenceFile}.
	 * 
	 * @param sf
	 *            the file to load {@link Analysis} records for.
	 * @return the set of {@link Analysis} records.
	 */
	@Query("select os from Analysis os where ?1 member of os.inputFiles")
	public Set<Analysis> findAnalysesForSequenceFile(SequenceFile sf);

	/**
	 * Load the set of {@link Analysis} for a {@link SequenceFile} that are a
	 * specific subtype of {@link Analyis}.
	 * 
	 * @param sf
	 *            the file to load {@link Analysis} records for.
	 * @param analysisType
	 *            the type of {@link Analysis} that should be loaded.
	 * @return the set of {@link Analysis} records of the specified type.
	 */
	@Query("select a from Analysis a where ?1 member of a.inputFiles and type(a) = ?2")
	public <T extends Analysis> Set<T> findAnalysesForSequenceFile(SequenceFile sf, Class<T> analysisType);
}
