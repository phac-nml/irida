package ca.corefacility.bioinformatics.irida.validators.annotations.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ca.corefacility.bioinformatics.irida.validators.annotations.Longitude;

/**
 * Validator for validating longitude portion of a geographic coordinate.
 * 
 *
 */
public class LongitudeValidator implements ConstraintValidator<Longitude, String> {

	private static final Pattern LONGITUDE_PATTERN = Pattern.compile("^-?(\\d){1,3}(\\.\\d+)?$");

	private static final Double LONG_MIN = -180d;
	private static final Double LONG_MAX = 180d;

	@Override
	public void initialize(Longitude constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// @Longitude does not imply @NotNull, so don't try to validate if the
		// value is null.
		if (value == null) {
			return true;
		}

		if (LONGITUDE_PATTERN.matcher(value).matches()) {
			Double longitude = Double.valueOf(value);
			return longitude >= LONG_MIN && longitude <= LONG_MAX;
		}

		return false;
	}

}
