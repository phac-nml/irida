package ca.corefacility.bioinformatics.irida.service.analysis.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.expression.EvaluationException;
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

		String securedUserName = SecurityContextHolder.getContext()
				.getAuthentication()
				.getName();
		assertEquals("Should be admin user", adminUser.getUsername(), securedUserName);
	}

	@Test
	public void testMethodThatThrows() {
		try {
			annotatedClass.methodThatThrows(submittingUser);
		} catch (Exception e) {
			// It's a good thing!
		}

		String securedUserName = SecurityContextHolder.getContext()
				.getAuthentication()
				.getName();
		assertEquals("Should be admin user", adminUser.getUsername(), securedUserName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOther() {
		annotatedClass.otherMethod("bleh");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadParamName() {
		annotatedClass.badParam("bleh");
	}

	/**
	 * Testing when an exception is thrown on the SpEL from the annotation
	 */
	@Test(expected = EvaluationException.class)
	public void testBadSpel() {
		annotatedClass.badSpel("bleh");
	}

	private static class AnnotatedClass {

		public AnnotatedClass() {
		}

		@RunAsUser("#submitter")
		public void methodWithArgument(User submitter) {
			String submitterName = SecurityContextHolder.getContext()
					.getAuthentication()
					.getName();
			assertEquals("Should be submitting user", submittingUser.getUsername(), submitterName);
		}

		@RunAsUser("#submission")
		public void methodThatThrows(User submitter) throws Exception {
			throw new Exception("I'm broken!");
		}

		@RunAsUser("#other")
		public void otherMethod(String other) {
			fail("Method should not be run");
		}

		@RunAsUser("#wrong")
		public void badParam(String other) {
			fail("Method should not be run");
		}

		@RunAsUser("#object.badMethod()")
		public void badSpel(String object) {
			fail("Method should not be run");
		}

	}
}
