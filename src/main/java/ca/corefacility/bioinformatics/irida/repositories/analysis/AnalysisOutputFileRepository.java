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
	//TODO: Look into a solution that places this in FilesystemSupplementedRepository
	/**
	 * {@inheritDoc}
	 */
	<S extends AnalysisOutputFile> S save(S entity);
}
