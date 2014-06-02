package ca.corefacility.bioinformatics.irida.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@WithSecurityContext(factory = WithMockOAuth2SecurityContextFactory.class)
public @interface WithMockOAuth2Client {
    /**
     * The username to be used. Note that {@link #value()} is a synonym for {@link #username()}, but if {@link #username()} is specified it will take precedence.
     * @return
     */
    String username() default "";

    /**
     * The roles to use. The default is "USER". A {@link GrantedAuthority} will
     * be created for each value within roles. Each value in roles will
     * automatically be prefixed with "ROLE_". For example, the default will
     * result in "ROLE_USER" being used.
     *
     * @return
     */
    String[] roles() default { "USER" };

    /**
     * The password to be used. The default is "password".
     * @return
     */
    String password() default "password";
    
    /**
     * The clientId of the client being mocked
     * @return
     */
    String clientId() default "client";
}
