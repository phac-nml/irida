package ca.corefacility.bioinformatics.irida.model.workflow.description;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Optional;

/**
 * Defines the input labels for a workflow.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class IridaWorkflowInput {

	@XmlElement(name = "sequenceReadsSingle")
	private String sequenceReadsSingle;

	@XmlElement(name = "reference")
	private String reference;

	@XmlElement(name = "sequenceReadsPaired")
	private String sequenceReadsPaired;

	public IridaWorkflowInput() {
	}

	/**
	 * Builds a new {@link IridaWorkflowInput} object with the given
	 * information.
	 * 
	 * @param sequenceReadsSingle
	 *            The label to use for a collection of single-end sequence
	 *            reads. Null if no acceptance of single-end reads.
	 * @param sequenceReadsPaired
	 *            The label to use for a collection of paired-end sequence
	 *            reads. Null if no acceptance of paired-end reads.
	 * @param reference
	 *            The label to use for a reference file.
	 */
	public IridaWorkflowInput(String sequenceReadsSingle, String sequenceReadsPaired, String reference) {
		this.sequenceReadsSingle = sequenceReadsSingle;
		this.sequenceReadsPaired = sequenceReadsPaired;
		this.reference = reference;
	}

	/**
	 * Gets the sequence reads single label.
	 * 
	 * @return The sequence reads single label, or {@link Optional.Absent} if no
	 *         such label exists.
	 */
	public Optional<String> getSequenceReadsSingle() {
		return Optional.fromNullable(sequenceReadsSingle);
	}

	/**
	 * Gets the reference label.
	 * 
	 * @return The reference label, or {@link Optional.Absent} if no such label
	 *         exists.
	 */
	public Optional<String> getReference() {
		return Optional.fromNullable(reference);
	}

	/**
	 * Gets the sequence reads paired label.
	 * 
	 * @return The sequence reads paired label, or {@link Optional.Absent} if no
	 *         such label exists.
	 */
	public Optional<String> getSequenceReadsPaired() {
		return Optional.fromNullable(sequenceReadsPaired);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sequenceReadsSingle, sequenceReadsPaired, reference);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflowInput) {
			IridaWorkflowInput other = (IridaWorkflowInput) obj;

			return Objects.equals(sequenceReadsSingle, other.sequenceReadsSingle)
					&& Objects.equals(sequenceReadsPaired, other.sequenceReadsPaired)
					&& Objects.equals(reference, other.reference);
		}

		return false;
	}
}
