package ca.corefacility.bioinformatics.irida.repositories.analysis;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;

/**
 * A custom repository for managing {@link AnalysisOutputFile} objects.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface AnalysisOutputFileRepository extends IridaJpaRepository<AnalysisOutputFile, Long>,
		FilesystemSupplementedRepository<AnalysisOutputFile> {

}
