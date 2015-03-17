package ca.corefacility.bioinformatics.irida.validators.annotations.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ca.corefacility.bioinformatics.irida.validators.annotations.Latitude;

/**
 * Validator for validating latitude portion of a geographic coordinate.
 * 
 *
 */
public class LatitudeValidator implements ConstraintValidator<Latitude, String> {

	private static final Pattern LATITUDE_PATTERN = Pattern.compile("^-?(\\d){1,2}(\\.\\d+)?$");

	private static final Double LAT_MIN = -90d;
	private static final Double LAT_MAX = 90d;

	@Override
	public void initialize(Latitude constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// @Latitude does not imply @NotNull, so don't try to validate if the
		// value is null.
		if (value == null) {
			return true;
		}
		// validate with the regex to match "-?(\d){2}.(\d){2}"
		if (LATITUDE_PATTERN.matcher(value).matches()) {
			Double latitude = Double.valueOf(value);
			// let's now verify the range:
			return latitude >= LAT_MIN && latitude <= LAT_MAX;
		}

		return false;
	}

}
