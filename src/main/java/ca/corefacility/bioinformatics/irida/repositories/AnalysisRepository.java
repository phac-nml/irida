package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * A custom repository for managing {@link Analysis} objects.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface AnalysisRepository extends PagingAndSortingRepository<Analysis, Long> {

}
