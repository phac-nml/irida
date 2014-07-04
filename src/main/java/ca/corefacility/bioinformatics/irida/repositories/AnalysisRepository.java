package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * A custom repository for managing {@link Analysis} objects.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface AnalysisRepository extends PagingAndSortingRepository<Analysis, Long> {

	/**
	 * Load the set of {@link Analysis} for a {@link SequenceFile}.
	 * 
	 * @param sf
	 *            the file to load {@link Analysis} records for.
	 * @return the set of {@link Analysis} records.
	 */
	@Query("select os from Analysis os where ?1 member of os.inputFiles")
	public Set<Analysis> findAnalysesForSequenceFile(SequenceFile sf);
}
