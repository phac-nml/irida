package ca.corefacility.bioinformatics.irida.service.analysis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Annotation saying the function should be run as the {@link User} identified
 * in the {@link #value()}. The user should be identified using SpEL. Example
 * usage:
 * 
 * <pre>
 * {@code @RunAsUser("#user") 
 * public void testMethod(User user){...} }
 * </pre>
 * 
 * @see RunAsUserAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RunAsUser {
	/**
	 * SpEL expression denoting the user to add to the security context prior to
	 * running the annotated method
	 * 
	 * @return SpEL expression identifying a {@link User} object
	 */
	String value();
}
