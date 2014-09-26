package ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;

/**
 * A RemoteWorkflow for a phylogenomics analysis in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "remote_workflow_phylogenomics")
public class RemoteWorkflowPhylogenomics extends RemoteWorkflowGalaxy {

	@NotNull
	private String inputSequenceFilesLabel;
	
	@NotNull
	private String inputReferenceFileLabel;
	
	@NotNull
	private String outputPhylogeneticTreeName;
	
	@NotNull
	private String outputSnpMatrixName;
	
	@NotNull
	private String outputSnpTableName;
	
	@SuppressWarnings("unused")
	private RemoteWorkflowPhylogenomics() {
	}
	
	/**
	 * Creates a new RemoteWorkflowPhylogenomics.
	 * @param workflowId The ID of the implementing workflow.
	 * @param workflowChecksum The checksum of the implementing workflow.
	 * @param inputSequenceFilesLabel The label to use as input for sequence files.
	 * @param inputReferenceFileLabel The label to use as input for a reference file.
	 * @param outputPhylogeneticTreeName  The name for the output phylogenetic tree.
	 * @param outputSnpMatrixName  The name for the output SNP matrix.
	 * @param outputSnpTableName  The name for the output SNP table.
	 */
	public RemoteWorkflowPhylogenomics(String workflowId,
			String workflowChecksum, String inputSequenceFilesLabel,
			String inputReferenceFileLabel, String outputPhylogeneticTreeName,
			String outputSnpMatrixName, String outputSnpTableName) {
		super(workflowId, workflowChecksum);
		this.inputSequenceFilesLabel = inputSequenceFilesLabel;
		this.inputReferenceFileLabel = inputReferenceFileLabel;
		this.outputPhylogeneticTreeName = outputPhylogeneticTreeName;
		this.outputSnpMatrixName = outputSnpMatrixName;
		this.outputSnpTableName = outputSnpTableName;
	}

	public String getInputSequenceFilesLabel() {
		return inputSequenceFilesLabel;
	}

	public String getInputReferenceFileLabel() {
		return inputReferenceFileLabel;
	}

	public String getOutputPhylogeneticTreeName() {
		return outputPhylogeneticTreeName;
	}

	public String getOutputSnpMatrixName() {
		return outputSnpMatrixName;
	}

	public String getOutputSnpTableName() {
		return outputSnpTableName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "RemoteWorkflowPhylogenomics [inputSequenceFilesLabel="
				+ inputSequenceFilesLabel + ", inputReferenceFileLabel="
				+ inputReferenceFileLabel + ", outputPhylogeneticTreeName="
				+ outputPhylogeneticTreeName + ", outputSnpMatrixName="
				+ outputSnpMatrixName + ", outputSnpTableName="
				+ outputSnpTableName + ", toString()=" + super.toString() + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), inputSequenceFilesLabel,
				inputReferenceFileLabel, outputPhylogeneticTreeName, outputSnpMatrixName,
				outputSnpTableName);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof RemoteWorkflowPhylogenomics) {
			RemoteWorkflowPhylogenomics other = (RemoteWorkflowPhylogenomics)obj;
			
			return super.equals(other) && Objects.equals(inputSequenceFilesLabel, other.inputSequenceFilesLabel) &&
					Objects.equals(inputReferenceFileLabel, other.inputReferenceFileLabel) &&
					Objects.equals(outputPhylogeneticTreeName, other.outputPhylogeneticTreeName) &&
					Objects.equals(outputSnpMatrixName, other.outputSnpMatrixName) &&
					Objects.equals(outputSnpTableName, other.outputSnpTableName);		
		}
		
		return false;
	}
}
