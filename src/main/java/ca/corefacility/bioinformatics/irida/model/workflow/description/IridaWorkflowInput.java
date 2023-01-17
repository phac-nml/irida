package ca.corefacility.bioinformatics.irida.model.workflow.description;

import java.util.Objects;

import javax.xml.bind.annotation.*;

import java.util.Optional;

/**
 * Defines the input labels for a workflow.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class IridaWorkflowInput {

	@XmlElement(name = "sequenceReadsSingle")
	private String sequenceReadsSingle;

	@XmlElement(name = "reference")
	private String reference;

	@XmlElement(name = "sequenceReadsPaired")
	private String sequenceReadsPaired;

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
	 * @param reference            The label to use for a reference file.
	 * @param requiresSingleSample Whether or not this workflow requires a single sample, or can work with multiple
	 *                             samples.
	 */
	public IridaWorkflowInput(String sequenceReadsSingle, String sequenceReadsPaired, String reference,
			boolean requiresSingleSample) {
		this.sequenceReadsSingle = sequenceReadsSingle;
		this.sequenceReadsPaired = sequenceReadsPaired;
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
	 * Gets the reference label.
	 * 
	 * @return The reference label, or {@link Optional#empty()} if no such label exists.
	 */
	public Optional<String> getReference() {
		return Optional.ofNullable(reference);
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
	 * Whether or not this workflow requires a single sample.
	 * 
	 * @return True if this workflow requires a single sample, false otherwise.
	 */
	public boolean requiresSingleSample() {
		return requiresSingleSample;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sequenceReadsSingle, sequenceReadsPaired, reference, requiresSingleSample);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflowInput) {
			IridaWorkflowInput other = (IridaWorkflowInput) obj;

			return Objects.equals(sequenceReadsSingle, other.sequenceReadsSingle)
					&& Objects.equals(sequenceReadsPaired, other.sequenceReadsPaired)
					&& Objects.equals(reference, other.reference)
					&& Objects.equals(requiresSingleSample, other.requiresSingleSample);
		}

		return false;
	}
}
