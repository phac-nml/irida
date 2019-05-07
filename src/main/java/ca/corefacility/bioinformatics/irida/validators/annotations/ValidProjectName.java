package ca.corefacility.bioinformatics.irida.validators.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

/**
 * Custom validation annotation that implements a black-list of unacceptable
 * characters in resources that can be named.
 * 
 * 
 */
@Pattern.List({ @Pattern(regexp = "^[^\\?]*$", message = "{irida.name.invalid.question.mark}"),
		@Pattern(regexp = "^[^\\(]*$", message = "{irida.name.invalid.left.paren}"),
		@Pattern(regexp = "^[^\\)]*$", message = "{irida.name.invalid.right.paren}"),
		@Pattern(regexp = "^[^\\[]*$", message = "{irida.name.invalid.left.bracket}"),
		@Pattern(regexp = "^[^\\]]*$", message = "{irida.name.invalid.right.bracket}"),
		@Pattern(regexp = "^[^\\/]*$", message = "{irida.name.invalid.forward.slash}"),
		@Pattern(regexp = "^[^\\\\]*$", message = "{irida.name.invalid.back.slash}"),
		@Pattern(regexp = "^[^\\=]*$", message = "{irida.name.invalid.equals}"),
		@Pattern(regexp = "^[^\\+]*$", message = "{irida.name.invalid.plus}"),
		@Pattern(regexp = "^[^\\<]*$", message = "{irida.name.invalid.left.angle}"),
		@Pattern(regexp = "^[^\\>]*$", message = "{irida.name.invalid.right.angle}"),
		@Pattern(regexp = "^[^\\:]*$", message = "{irida.name.invalid.colon}"),
		@Pattern(regexp = "^[^\\;]*$", message = "{irida.name.invalid.semi.colon}"),
		@Pattern(regexp = "^[^\\\"]*$", message = "{irida.name.invalid.double.quote}"),
		@Pattern(regexp = "^[^\\,]*$", message = "{irida.name.invalid.comma}"),
		@Pattern(regexp = "^[^\\*]*$", message = "{irida.name.invalid.star}"),
		@Pattern(regexp = "^[^\\^]*$", message = "{irida.name.invalid.circumflex}"),
		@Pattern(regexp = "^[^\\|]*$", message = "{irida.name.invalid.pipe}"),
		@Pattern(regexp = "^[^\\&]*$", message = "{irida.name.invalid.ampersand}"), })
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidProjectName {

	String message() default "{irida.name.invalid.default}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * Invalid characters in a project name
	 */
	public static class ValidProjectNameBlacklist {
		public static final char[] BLACKLIST = { '?', '(', ')', '[', ']', '/', '\\', '=', '+', '<', '>', ':', ';', '"',
				',', '*', '^', '|', '&' };
	}
}
