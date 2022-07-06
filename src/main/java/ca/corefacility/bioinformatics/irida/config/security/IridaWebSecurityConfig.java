package ca.corefacility.bioinformatics.irida.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.LocaleResolver;

import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.ria.config.filters.SessionFilter;
import ca.corefacility.bioinformatics.irida.ria.security.CredentialsExpriredAuthenticationFailureHandler;
import ca.corefacility.bioinformatics.irida.ria.security.LoginSuccessHandler;

/**
 * Configuration for web security using OAuth2
 */
@Configuration
@Import({ IridaOauthSecurityConfig.class })
public class IridaWebSecurityConfig {

	/**
	 * UI security config for IRIDA
	 */
	@Configuration
	protected static class UISecurityConfig {

		@Autowired
		private UserRepository userRepository;

		@Autowired
		private LocaleResolver localeResolver;

		@Bean
		public LoginSuccessHandler getLoginSuccessHandler() {
			return new LoginSuccessHandler(userRepository, localeResolver);
		}

		@Autowired
		CredentialsExpriredAuthenticationFailureHandler authFailureHandler;

		@Bean
		@Order(Ordered.HIGHEST_PRECEDENCE + 1)
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
			authFailureHandler.setDefaultFailureUrl("/login?error=true");
			// @formatter:off
			http.requestMatcher(request -> {
				// Don't handle requests under the /api path except for paths that start with /api/oauth
				return !request.getRequestURI().matches("^.*/api(?!/oauth)(/.*)?$");
			}).authorizeRequests().and()

			// Prevent Cross Site Request Forgery
			.csrf().disable()
			// Refactor login form

			// See https://jira.springsource.org/browse/SPR-11496
			// This is for SockJS and Web Sockets
			.headers().frameOptions().disable().and()
			.formLogin().defaultSuccessUrl("/dashboard").loginPage("/login")
					.successHandler(getLoginSuccessHandler())
					.failureHandler(authFailureHandler).permitAll()
			.and()
			.logout().logoutSuccessUrl("/login").logoutUrl("/logout").permitAll()
			.and()
			.authorizeRequests().antMatchers("/login**").permitAll()
				.antMatchers("/resources/**").permitAll()
				.antMatchers("/dist/**").permitAll()
				.antMatchers("/v3/api-docs/**").permitAll()
				.antMatchers("/password_reset/**").permitAll()
				.antMatchers("/ajax/password_reset/**").permitAll()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/**").fullyAuthenticated()
			.and().addFilterAfter(getSessionModelFilter(), SecurityContextHolderAwareRequestFilter.class);
			// @formatter:on

			return http.build();
		}

		@Bean
		public WebSecurityCustomizer webSecurityCustomizer() {
			return (web) -> {
				web.ignoring()
						.antMatchers("/node_modules/**")
						.antMatchers("/dist/**")
						.antMatchers("/static/**")
						.antMatchers("/resources/**");
				web.httpFirewall(new DefaultHttpFirewall());
			};
		}

		@Bean
		public GenericFilterBean getSessionModelFilter() {
			return new SessionFilter();
		}
	}
}
