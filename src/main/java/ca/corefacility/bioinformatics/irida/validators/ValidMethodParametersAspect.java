package ca.corefacility.bioinformatics.irida.validators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * This aspect automatically invokes a {@link Validator} for method parameters
 * annotated with the {@link Valid} annotation.
 * 
 * 
 */
@Aspect
public class ValidMethodParametersAspect {

	private static final Logger logger = LoggerFactory.getLogger(ValidMethodParametersAspect.class);

	private final Validator validator;

	public ValidMethodParametersAspect(final Validator validator) {
		this.validator = validator;
	}

	/**
	 * Aspect that matches any method execution in our package with one or more
	 * parameters that have the {@link Valid} annotation.
	 * 
	 * @param jp
	 *            the {@link JoinPoint} representing the captured method
	 *            execution.
	 */
	@SuppressWarnings("unchecked")
	@Before("execution(* ca.corefacility.bioinformatics.irida..*(.., @javax.validation.Valid (*), ..))")
	public void validateParameters(JoinPoint jp) {
		// This is an array of the *actual* arguments passed to the method.
		Object[] args = jp.getArgs();
		List<List<Annotation>> annotations = getParameterAnnotations(jp);

		// Iterate over the array of arguments and look at the annotations
		// applied to that argument. If *any* of the arguments are @Valid, then
		// pass the argument to the validator for validation.
		for (int i = 0; i < args.length; i++) {
			List<Annotation> argAnnotations = annotations.get(i);
			boolean anyValidAnnotation = false;

			for (Annotation a : argAnnotations) {
				if (a.annotationType().equals(Valid.class)) {
					anyValidAnnotation = true;
					break;
				}
			}

			if (anyValidAnnotation) {
				// if any parameter is annotated with @Valid, proceed with
				// validation using the validator.
				Set<ConstraintViolation<Object>> violations;

				if (args[i] instanceof Iterable) {
					// the element that we're currently validating is a
					// collection of elements; we should validate each of those
					// elements individually.
					violations = new HashSet<>();
					for (Object o : (Iterable<Object>) args[i]) {
						violations.addAll(validator.validate(o));
					}
				} else {
					violations = validator.validate(args[i]);
				}
				if (!violations.isEmpty()) {
					// if any validation errors are found, throw a
					// ConstraintViolationException.
					if (logger.isDebugEnabled()) {
						final StringBuilder sb = new StringBuilder();
						sb.append("Found constraint violations when validating [")
								.append(jp.getSignature().toShortString())
								.append("], properties violating constraints:\n");
						for (final ConstraintViolation<Object> violation : violations) {
							sb.append("\t").append(violation.getRootBeanClass().toString()).append(".")
									.append(violation.getPropertyPath().toString()).append(": ")
									.append(violation.getMessage()).append("\n");
						}
						logger.debug(sb.toString());
					}
					throw new ConstraintViolationException(violations);
				}
			}
		}
	}

	/**
	 * Gets the set of annotations applied to parameters from both an interface
	 * and concrete implementation of a class.
	 * 
	 * @param jp
	 *            the {@link JoinPoint} that's currently executing.
	 * @return the collection of annotations applied to parameters of the
	 *         currently executing method.
	 */
	private List<List<Annotation>> getParameterAnnotations(JoinPoint jp) {
		List<List<Annotation>> annotations = new ArrayList<>();
		MethodSignature signature = (MethodSignature) jp.getSignature();
		Method m = signature.getMethod();

		// in the event that the class is *not* an interface, we need to get the
		// corresponding method from the interface to load any parameter
		// annotations from there. Note that we only load the direct super
		// interface, and stop when we find the first interface that matches the
		// method definition; order of interfaces is important.
		Class<?>[] interfaces = m.getDeclaringClass().getInterfaces();
		Method interfaceMethod = null;
		for (Class<?> iface : interfaces) {
			try {
				interfaceMethod = iface.getMethod(m.getName(), m.getParameterTypes());
			} catch (NoSuchMethodException | SecurityException e) {

			}
		}

		if (interfaceMethod == null) {
			interfaceMethod = m;
		}

		Annotation[][] interfaceAnnotations = interfaceMethod.getParameterAnnotations();

		for (Annotation[] interfaceAnnotation : interfaceAnnotations) {
			annotations.add(Lists.newArrayList(interfaceAnnotation));
		}
		try {
			Method implementedMethod = jp.getTarget().getClass().getMethod(m.getName(), m.getParameterTypes());
			// This is an array-of-arrays; the first index corresponds to the
			// arguments that were passed to the method, the second index
			// corresponds to each of the parameters that was applied to the
			// argument.
			Annotation[][] implementedAnnotations = implementedMethod.getParameterAnnotations();
			for (int i = 0; i < annotations.size(); i++) {
				annotations.get(i).addAll(Lists.newArrayList(implementedAnnotations[i]));
			}
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException("A concrete instance of a class *must* implement"
					+ " a method declared in an interface.");
		}

		return annotations;
	}

}
