package ca.corefacility.bioinformatics.irida.ria.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * ServletContext initializer for Spring Security specific configuration such as
 * the chain of Spring Security filters.
 * <p>
 * The Spring Security configuration is customized with
 * {@link WebSecurityConfig}.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class WebSecurityInitializer extends AbstractSecurityWebApplicationInitializer {
}
