package ca.corefacility.bioinformatics.irida.model.workflow.description;

import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Defines the input labels for a workflow.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class IridaWorkflowInput {

	@XmlElement(name = "sequenceReadsSingle")
	private String sequenceReadsSingle;

	@XmlElement(name = "sequenceReadsPaired")
	private String sequenceReadsPaired;

	@XmlElement(name = "genomeAssemblies")
	private String genomeAssemblies;

	@XmlElement(name = "reference")
	private String reference;

	@XmlElement(name = "requiresSingleSample", defaultValue = "false")
	private boolean requiresSingleSample;

	public IridaWorkflowInput() {
	}

	/**
	 * Builds a new {@link IridaWorkflowInput} object with the given information.
	 *
	 * @param sequenceReadsSingle  The label to use for a collection of single-end sequence reads. Null if no acceptance
	 *                             of single-end reads.
	 * @param sequenceReadsPaired  The label to use for a collection of paired-end sequence reads. Null if no acceptance
	 *                             of paired-end reads.
	 * @param genomeAssemblies     The label to use for a collection of sequence assemblies. Null if no acceptance of
	 *                             assemblies.
	 * @param reference            The label to use for a reference file.
	 * @param requiresSingleSample Whether or not this workflow requires a single sample, or can work with multiple
	 *                             samples.
	 */
	public IridaWorkflowInput(String sequenceReadsSingle, String sequenceReadsPaired, String genomeAssemblies,
			String reference, boolean requiresSingleSample) {
		this.sequenceReadsSingle = sequenceReadsSingle;
		this.sequenceReadsPaired = sequenceReadsPaired;
		this.genomeAssemblies = genomeAssemblies;
		this.reference = reference;
		this.requiresSingleSample = requiresSingleSample;
	}

	/**
	 * Gets the sequence reads single label.
	 * 
	 * @return The sequence reads single label, or {@link Optional#empty()} if no such label exists.
	 */
	public Optional<String> getSequenceReadsSingle() {
		return Optional.ofNullable(sequenceReadsSingle);
	}

	/**
	 * Gets the sequence reads paired label.
	 * 
	 * @return The sequence reads paired label, or {@link Optional#empty()} if no such label exists.
	 */
	public Optional<String> getSequenceReadsPaired() {
		return Optional.ofNullable(sequenceReadsPaired);
	}

	/**
	 * Gets the sequence assemblies label.
	 * 
	 * @return The sequence assemblies label, or {@link Optional#empty()} if no such label exists.
	 */
	public Optional<String> getGenomeAssemblies() {
		return Optional.ofNullable(genomeAssemblies);
	}

	/**
	 * Gets the reference label.
	 * 
	 * @return The reference label, or {@link Optional#empty()} if no such label exists.
	 */
	public Optional<String> getReference() {
		return Optional.ofNullable(reference);
	}

	/**
	 * Whether or not this workflow requires a single sample.
	 * 
	 * @return True if this workflow requires a single sample, false otherwise.
	 */
	public boolean requiresSingleSample() {
		return requiresSingleSample;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sequenceReadsSingle, sequenceReadsPaired, genomeAssemblies, reference,
				requiresSingleSample);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflowInput) {
			IridaWorkflowInput other = (IridaWorkflowInput) obj;

			return Objects.equals(sequenceReadsSingle, other.sequenceReadsSingle)
					&& Objects.equals(sequenceReadsPaired, other.sequenceReadsPaired)
					&& Objects.equals(genomeAssemblies, other.genomeAssemblies)
					&& Objects.equals(reference, other.reference)
					&& Objects.equals(requiresSingleSample, other.requiresSingleSample);
		}

		return false;
	}
}
