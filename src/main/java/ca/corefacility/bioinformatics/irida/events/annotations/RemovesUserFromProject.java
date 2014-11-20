package ca.corefacility.bioinformatics.irida.events.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Annotation to be put on methods removing a {@link User} from a
 * {@link Project}. Note: Any method using this annotation MUST have a
 * {@link Project} and {@link User} for arguments.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Target(ElementType.METHOD)
public @interface RemovesUserFromProject {

}
