package ca.corefacility.bioinformatics.irida.repositories.analysis;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A custom repository for managing {@link Analysis} objects.
 * 
 *
 */
public interface AnalysisRepository extends IridaJpaRepository<Analysis, Long> {
	
}
