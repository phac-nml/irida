package ca.corefacility.bioinformatics.irida.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * Annotation for use with {@link WithMockOAuth2SecurityContextFactory} to add a {@link OAuth2Authentication} object
 * into the security context for junit tests.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@WithSecurityContext(factory = WithMockOAuth2SecurityContextFactory.class)
public @interface WithMockOAuth2Client {
	/**
	 * The username to be used. Note that {@link #value()} is a synonym for {@link #username()}, but if
	 * {@link #username()} is specified it will take precedence.
	 * 
	 * @return
	 */
	String username() default "";

	/**
	 * The roles to use. The default is "USER". A {@link GrantedAuthority} will be created for each value within roles.
	 * Each value in roles will automatically be prefixed with "ROLE_". For example, the default will result in
	 * "ROLE_USER" being used.
	 *
	 * @return
	 */
	String[] roles() default { "USER" };

	/**
	 * The password to be used. The default is "password".
	 * 
	 * @return
	 */
	String password() default "password";

	/**
	 * The clientId of the client being mocked. Default is "client"
	 * 
	 * @return
	 */
	String clientId() default "client";

	/**
	 * Scope of OAuth2 client. Default is none
	 * 
	 * @return
	 */
	String[] scope() default "";
}
