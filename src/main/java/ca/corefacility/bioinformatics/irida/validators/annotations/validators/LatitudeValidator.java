package ca.corefacility.bioinformatics.irida.validators.annotations.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ca.corefacility.bioinformatics.irida.validators.annotations.Latitude;

/**
 * Validator for validating latitude portion of a geographic coordinate.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public class LatitudeValidator implements ConstraintValidator<Latitude, String> {

	private static final Pattern LATITUDE_PATTERN = Pattern.compile("^-?(\\d){2}(\\.(\\d){1,2})?$");
	
	private static final Float LAT_MIN = -90f;
	private static final Float LAT_MAX = 90f;

	@Override
	public void initialize(Latitude constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// validate with the regex to match "-?(\d){2}.(\d){2}"
		if (LATITUDE_PATTERN.matcher(value).matches()) {
			Float latitude = Float.valueOf(value);
			// let's now verify the range:
			return latitude >= LAT_MIN && latitude <= LAT_MAX;
		}
		
		return false;
	}

}
