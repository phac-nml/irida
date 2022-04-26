package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * A service for storing and retrieving {@link GenomeAssembly} entities.
 */
public interface GenomeAssemblyService extends CRUDService<Long, GenomeAssembly> {

	/**
	 * Create a new {@link GenomeAssembly} in the given {@link Sample}
	 *
	 * @param sample   the sample to add a new assembly
	 * @param assembly the assembly to create
	 * @return a new {@link SampleGenomeAssemblyJoin}
	 */
	public SampleGenomeAssemblyJoin createAssemblyInSample(Sample sample, GenomeAssembly assembly);

	/**
	 * Get all {@link GenomeAssembly} for the given {@link Sample}
	 *
	 * @param sample the sample to get assemblies for
	 * @return a collection of {@link SampleGenomeAssemblyJoin}
	 */
	public Collection<SampleGenomeAssemblyJoin> getAssembliesForSample(Sample sample);

	/**
	 * Gets the genome assembly for a sample.
	 *
	 * @param sample           The sample.
	 * @param genomeAssemblyId The id of the genome assembly.
	 * @return The {@link GenomeAssembly} with the given information.
	 */
	public GenomeAssembly getGenomeAssemblyForSample(Sample sample, Long genomeAssemblyId);

	/**
	 * Deletes the given genome assembly from the given sample.
	 *
	 * @param sample           The sample.
	 * @param genomeAssemblyId The genome assembly.
	 */
	public void removeGenomeAssemblyFromSample(Sample sample, Long genomeAssemblyId);

	/**
	 * Get a map of {@link GenomeAssembly}s and corresponding {@link Sample}s.
	 * 
	 * @param genomeAssemblies A {@link Set} of {@link GenomeAssembly}s.
	 * @return A {@link Map} of between {@link Sample} and {@link SequencingObject}.
	 * @throws DuplicateSampleException If there is a duplicate sample.
	 */
	public Map<Sample, GenomeAssembly> getUniqueSamplesForGenomeAssemblies(Set<GenomeAssembly> genomeAssemblies)
			throws DuplicateSampleException;

	/**
	 * Get a set of {@link GenomeAssembly}s used as inputs in an {@link AnalysisSubmission}
	 * 
	 * @param submission The {@link AnalysisSubmission} to get genome assemblies from
	 * @return A {@link Set} of {@link GenomeAssembly}s
	 */
	public Set<GenomeAssembly> getGenomeAssembliesForAnalysisSubmission(AnalysisSubmission submission);
}
