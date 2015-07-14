package ca.corefacility.bioinformatics.irida.service.analysis.annotations;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;

public class RunAsUserAspectTest {

	RunAsUserAspect aspect = new RunAsUserAspect();

	AnnotatedClass annotatedClass;

	static User submittingUser;

	static User adminUser;

	@Before
	public void setup() {
		aspect = new RunAsUserAspect();
		annotatedClass = new AnnotatedClass();
		AspectJProxyFactory proxyFactory = new AspectJProxyFactory(annotatedClass);
		proxyFactory.addAspect(aspect);

		annotatedClass = proxyFactory.getProxy();

		submittingUser = new User("tom", null, null, "Test", "User", null);
		submittingUser.setSystemRole(Role.ROLE_USER);

		adminUser = new User("admin", null, null, "admin", "User", null);
		PreAuthenticatedAuthenticationToken initialAuth = new PreAuthenticatedAuthenticationToken(adminUser, null,
				Lists.newArrayList(Role.ROLE_ADMIN));

		// Set initial context
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(initialAuth);
		SecurityContextHolder.setContext(context);
	}

	@Test
	public void testMethodWithArgument() {
		annotatedClass.methodWithArgument(submittingUser);

		String securedUserName = SecurityContextHolder.getContext().getAuthentication().getName();
		assertEquals("Should be admin user", adminUser.getUsername(), securedUserName);
	}

	@Test
	public void testMethodThatThrows() {
		try {
			annotatedClass.methodThatThrows(submittingUser);
		} catch (Exception e) {
			// It's a good thing!
		}

		String securedUserName = SecurityContextHolder.getContext().getAuthentication().getName();
		assertEquals("Should be admin user", adminUser.getUsername(), securedUserName);
	}

	private static class AnnotatedClass {

		@RunAsUser("#submitter")
		public void methodWithArgument(User submitter) {
			String submitterName = SecurityContextHolder.getContext().getAuthentication().getName();
			assertEquals("Should be submitting user", submittingUser.getUsername(), submitterName);
		}

		@RunAsUser("#submission")
		public void methodThatThrows(User submitter) throws Exception {
			throw new Exception("I'm broken!");
		}

	}
}
