package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;

/**
 * A repository to store information about sequence files. This repository will
 * not directly store the file, just metadata
 * 
 */
public interface SequenceFileRepository extends FilesystemSupplementedRepository<SequenceFile>,
		IridaJpaRepository<SequenceFile, Long> {
	/**
	 * Get the collection of {@link SequenceFile} created as part of a
	 * {@link SequencingRun}.
	 * 
	 * @param sequencingRun
	 *            the run to load the files for.
	 * @return the files created as part of a run.
	 */
	@Query("select f from SequenceFile f where f.sequencingRun = ?1")
	public Set<SequenceFile> findSequenceFilesForSequencingRun(SequencingRun sequencingRun);

	/**
	 * Get the fastqc analysis for a specific file.
	 * 
	 * @param sequenceFile
	 *            the file to load the fastqc analysis for
	 * @return the fastqc analysis for the file.
	 */
	@Query("select f.fastqcAnalysis from SequenceFile f where f = ?1")
	public AnalysisFastQC findFastqcAnalysisForSequenceFile(final SequenceFile sequenceFile);
}
