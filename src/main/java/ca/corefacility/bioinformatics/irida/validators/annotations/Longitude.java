package ca.corefacility.bioinformatics.irida.validators.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import ca.corefacility.bioinformatics.irida.validators.annotations.validators.LongitudeValidator;

/**
 * Validates geographic longitude coordinate values.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LongitudeValidator.class)
public @interface Longitude {
	String message() default "{ca.corefacility.bioinformatics.irida.validators.annotations.Longitude.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
