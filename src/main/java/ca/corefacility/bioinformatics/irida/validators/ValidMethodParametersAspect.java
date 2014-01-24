package ca.corefacility.bioinformatics.irida.validators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

import com.google.common.collect.Lists;

/**
 * This aspect automatically invokes a {@link Validator} for method parameters
 * annotated with the {@link @Valid} annotation.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
@Aspect
public class ValidMethodParametersAspect {

	private Validator validator;

	public ValidMethodParametersAspect(Validator validator) {
		this.validator = validator;
	}

	/**
	 * Aspect that matches any method execution in our package with one or more
	 * parameters that have the {@link @Valid} annotation.
	 * 
	 * @param jp
	 *            the {@link JoinPoint} representing the captured method
	 *            execution.
	 */
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
				Set<ConstraintViolation<Object>> violations = validator.validate(args[i]);
				if (!violations.isEmpty()) {
					// if any validation errors are found, throw a
					// ConstraintViolationException.
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
		Annotation[][] interfaceAnnotations = m.getParameterAnnotations();

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
