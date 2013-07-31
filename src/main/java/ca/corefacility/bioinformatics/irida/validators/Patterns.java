package ca.corefacility.bioinformatics.irida.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.constraints.Pattern;

/**
 * An annotation to allow multiple patterns to be applied to a String using the
 * {@link javax.validation.constraints.Pattern} annotation. Inspired by the blog
 * post at:
 * http://relation.to/Bloggers/BeanValidationSneakPeekPartIICustomConstraints
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Patterns {
    Pattern[] value();
}
