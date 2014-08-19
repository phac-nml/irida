package ca.corefacility.bioinformatics.irida.model.workflow;

/**
 * An ID for a workflow in a remote execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface RemoteAnalysisId {
	
	/**
	 * Gets the String value of this id.
	 * @return  The String value of this id.
	 */
	public String getRemoteAnalysisId();
}
