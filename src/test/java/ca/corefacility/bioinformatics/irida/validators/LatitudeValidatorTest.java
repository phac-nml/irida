package ca.corefacility.bioinformatics.irida.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.validators.annotations.validators.LatitudeValidator;

/**
 * Tests the behaviour of {@link LatitudeValidator}.
 * 
 *
 */
public class LatitudeValidatorTest {
	private LatitudeValidator validator;

	@BeforeEach
	public void setUp() {
		validator = new LatitudeValidator();
	}

	@Test
	public void testOneDigitPrefix() {
		String coord = "1.340";
		assertTrue(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}

	@Test
	public void testTwoDigitZeroPrefix() {
		String coord = "01.340";
		assertTrue(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}

	@Test
	public void testTwoDigitNonZeroPrefix() {
		String coord = "41.340";
		assertTrue(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}

	@Test
	public void testOneDigitNegativePrefix() {
		String coord = "-1.340";
		assertTrue(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}

	@Test
	public void testTwoDigitNegativeZeroPrefix() {
		String coord = "-01.340";
		assertTrue(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}

	@Test
	public void testTwoDigitNegativeNonZeroPrefix() {
		String coord = "-31.340";
		assertTrue(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}

	@Test
	public void testOutsideRangePositive() {
		String coord = "1000";
		assertFalse(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}

	@Test
	public void testOutsideRangeNegative() {
		String coord = "-1000";
		assertFalse(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}
}
