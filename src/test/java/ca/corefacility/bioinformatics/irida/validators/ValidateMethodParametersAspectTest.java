package ca.corefacility.bioinformatics.irida.validators;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import com.google.common.collect.Lists;

/**
 * Unit tests for validating method parameters.
 */
public class ValidateMethodParametersAspectTest {

	private ValidMethodParametersAspect aspect;
	private AnnotatedMethodsClass proxy;
	private AnnotatedInterface interfaceProxy;
	@Mock
	private Validator validator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		AnnotatedMethodsClass target = new AnnotatedMethodsClass();
		AnnotatedInterfaceImpl interfaceProxyTarget = new AnnotatedInterfaceImpl();
		AspectJProxyFactory proxyFactory = new AspectJProxyFactory(target);
		AspectJProxyFactory interfaceProxyFactory = new AspectJProxyFactory(interfaceProxyTarget);
		aspect = new ValidMethodParametersAspect(validator);
		proxyFactory.addAspect(aspect);
		interfaceProxyFactory.addAspect(aspect);
		proxy = proxyFactory.getProxy();
		interfaceProxy = interfaceProxyFactory.getProxy();
	}

	@Test
	public void testOneMethodParameterAnnotated() {
		String validated = "this should be validated";
		proxy.testOneValidParameter(validated);
		verify(validator).validate(validated);
	}

	@Test
	public void testManyMethodParameterAnnotated() {
		String first = "first";
		String second = "second";
		String third = "third";
		String fourth = "fourth";
		proxy.testManyValidParameters(first, second, third, fourth);
		verify(validator, times(1)).validate(first);
		verify(validator, times(1)).validate(second);
		verify(validator, times(0)).validate(third);
		verify(validator, times(1)).validate(fourth);
	}

	@Test(expected = ConstraintViolationException.class)
	public void testThrowsConstraintViolations() {
		String first = "first";
		Set<ConstraintViolation<Object>> violations = new HashSet<>();
		violations.add(ConstraintViolationImpl.forBeanValidation(null, null, Object.class, null, null, first,
				PathImpl.createRootPath(), null, null));
		when(validator.validate(any())).thenReturn(violations);
		proxy.testOneValidParameter(first);
	}

	@Test
	public void testExecutesAnnotatedInterface() {
		String param = "Paramter";
		interfaceProxy.testParameter(param);
		verify(validator).validate(param);
	}

	@Test
	public void testExecutesArgAnnotatedInImpl() {
		String param = "Parameter";
		interfaceProxy.testParameterAnnotatedInClass(param);
		verify(validator).validate(param);
	}

	@Test
	public void testValidatesIterable() {
		List<String> collection = Lists.newArrayList("first", "second", "third", "fourth");
		interfaceProxy.testIterableValidAnnotation(collection);

		verify(validator, times(collection.size())).validate(any(String.class));
		for (String el : collection) {
			verify(validator).validate(el);
		}
	}

	private static class AnnotatedMethodsClass {
		public AnnotatedMethodsClass() {
		}

		public void testOneValidParameter(@Valid String param) {

		}

		public void testManyValidParameters(@Valid String first, @Valid String second, String third,
				@Valid String fourth) {

		}
	}

	private static interface AnnotatedInterface {
		public void testParameter(@Valid String parameter);

		public void testParameterAnnotatedInClass(String parameter);

		public void testIterableValidAnnotation(@Valid Iterable<String> collection);
	}

	private static class AnnotatedInterfaceImpl implements AnnotatedInterface {
		public AnnotatedInterfaceImpl() {
		}

		@Override
		public void testParameter(String parameter) {
		}

		@Override
		public void testParameterAnnotatedInClass(@Valid String parameter) {
		}

		@Override
		public void testIterableValidAnnotation(Iterable<String> collection) {
		}
	}
}
