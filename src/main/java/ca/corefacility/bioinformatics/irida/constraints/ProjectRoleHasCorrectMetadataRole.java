package ca.corefacility.bioinformatics.irida.constraints;

import ca.corefacility.bioinformatics.irida.constraints.impl.ProjectMetadataRoleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validation annotation to validate that if the projectRole is set to PROJECT_OWNER for a user/usergroup then the
 * metadataRole must be set to the highest level An array of fields (projectRole, metadataRole) must be supplied
 * <p>
 * Example, compare a single user/usergroup projectRole to it's metadataRole:
 * {@literal @}ProjectRoleHasCorrectMetadataRole(first = "projectRole", second = "metadataRole", message =
 * "server.project.owner.incorrect.metadata.role"))
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ProjectMetadataRoleValidator.class)
@Documented
public @interface ProjectRoleHasCorrectMetadataRole {
	String message() default "{constraints.projectrolehascorrectmetadatarole}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * @return The projectRole field
	 */
	String first();

	/**
	 * @return The metadataRole field
	 */
	String second();

	/**
	 *
	 * Defines several <code>@ProjectRoleHasCorrectMetadataRole</code> annotations on the same element
	 *
	 * @see ProjectRoleHasCorrectMetadataRole
	 *
	 * If a list of validations is required then uncomment the code below
	 *
	 * @Target({TYPE, ANNOTATION_TYPE})
	 * @Retention(RUNTIME)
	 * @Documented
	 * @interface List
	 * {
	 *   ProjectRoleHasCorrectMetadataRole[] value();
	 * }
	 *
	 */

}
