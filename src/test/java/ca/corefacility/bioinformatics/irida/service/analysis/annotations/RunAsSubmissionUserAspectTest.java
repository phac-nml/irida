package ca.corefacility.bioinformatics.irida.service.analysis.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

public class RunAsSubmissionUserAspectTest {

	RunAsSubmissionUserAspect aspect = new RunAsSubmissionUserAspect();

	AnnotatedClass annotatedClass;

	static User submittingUser;

	User adminUser;

	@Before
	public void setup() {
		aspect = new RunAsSubmissionUserAspect();
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
		AnalysisSubmission submission = new AnalysisSubmission.Builder(UUID.randomUUID())
				.inputFilesSingle(Sets.newHashSet(new SequenceFile())).build();
		submission.setSubmitter(submittingUser);

		annotatedClass.methodWithArgument(submission);

		String securedUserName = SecurityContextHolder.getContext().getAuthentication().getName();
		assertEquals("Should be admin user", adminUser.getUsername(), securedUserName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMethodWithoutArgument() {
		annotatedClass.methodWithoutArgument();
	}

	private static class AnnotatedClass {

		@RunAsSubmissionUser
		public void methodWithArgument(AnalysisSubmission submission) {
			String submitterName = SecurityContextHolder.getContext().getAuthentication().getName();
			assertEquals("Should be submitting user", submittingUser.getUsername(), submitterName);
		}

		@RunAsSubmissionUser
		public void methodWithoutArgument() {
			fail("Method should not have run because it doesn't have an analysis submission as argument");
		}

	}
}
