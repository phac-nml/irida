package ca.corefacility.bioinformatics.irida.repositories.assembly;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;

/**
 * A repository for storing and retrieving {@link GenomeAssembly} entities
 */
public interface GenomeAssemblyRepository
		extends FilesystemSupplementedRepository<UploadedAssembly>, IridaJpaRepository<GenomeAssembly, Long> {

	/**
	 * {@inheritDoc}
	 * <p>
	 * Save is overridden here instead of in FilesystemSupplementedRepository as it would throw a compilation error
	 */
	<S extends GenomeAssembly> S save(S entity);

	/**
	 * Get the {@link GenomeAssembly}s associated with a given {@link AnalysisSubmission}
	 * 
	 * @param submission the {@link AnalysisSubmission}
	 * @return the set of associated {@link GenomeAssembly}s
	 */
	@Query("select f from GenomeAssembly f where ?1 in elements(f.analysisSubmissions)")
	Set<GenomeAssembly> findGenomeAssembliesForAnalysisSubmission(AnalysisSubmission submission);
}
