package ca.corefacility.bioinformatics.irida.repositories.analysis;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * A custom repository for managing {@link Analysis} objects.
 * 
 *
 */
public interface AnalysisRepository extends IridaJpaRepository<Analysis, Long> {
	
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
