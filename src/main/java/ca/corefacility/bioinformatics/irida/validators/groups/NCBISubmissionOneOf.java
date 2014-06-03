package ca.corefacility.bioinformatics.irida.validators.groups;

import javax.validation.groups.Default;

/**
 * NCBI defines some attributes as a "one-of" must not be null. This group is
 * intended to represent that type of constraint.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface NCBISubmissionOneOf extends Default {

}
