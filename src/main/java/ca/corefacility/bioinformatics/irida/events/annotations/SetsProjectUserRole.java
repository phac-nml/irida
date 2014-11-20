package ca.corefacility.bioinformatics.irida.events.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Annotation to be put on service methods that alter the role of a {@link User}
 * on a {@link Project}. This may include adding a user to a project, or
 * updating their role. These methods should return a {@link ProjectUserJoin}.
 * 
 * This is not to be used when removing a user from a project. In that case use
 * {@link RemovesUserFromProject}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Target(ElementType.METHOD)
public @interface SetsProjectUserRole {

}
