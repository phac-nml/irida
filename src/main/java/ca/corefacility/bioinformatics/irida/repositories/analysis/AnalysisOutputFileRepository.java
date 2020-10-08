package ca.corefacility.bioinformatics.irida.repositories.analysis;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;

/**
 * A custom repository for managing {@link AnalysisOutputFile} objects.
 * 
 *
 */
public interface AnalysisOutputFileRepository extends IridaJpaRepository<AnalysisOutputFile, Long>,
		FilesystemSupplementedRepository<AnalysisOutputFile> {
	/**
	 * {@inheritDoc}
	 *
	 * Save is overridden here instead of in FilesystemSupplementedRepository as it would throw a
	 * compilation error
	 */
	<S extends AnalysisOutputFile> S save(S entity);
}
