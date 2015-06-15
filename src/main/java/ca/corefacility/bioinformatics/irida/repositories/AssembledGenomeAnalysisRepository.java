package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.genomeFile.AssembledGenomeAnalysis;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

public interface AssembledGenomeAnalysisRepository extends IridaJpaRepository<AssembledGenomeAnalysis, Long> {

	/**
	 * Get a {@link AssembledGenomeAnalysis} for a {@link SequenceFilePair}.
	 * 
	 * @param sequenceFilePair
	 *            The {@link SequenceFilePair}.
	 * @return The {@link AssembledGenomeAnalysis}, or null if no such assembly.
	 */
	@Query("SELECT p.assembledGenome FROM SequenceFilePair p WHERE p = ?1")
	public AssembledGenomeAnalysis getAssembledGenomeForSequenceFilePair(SequenceFilePair sequenceFilePair);
}
