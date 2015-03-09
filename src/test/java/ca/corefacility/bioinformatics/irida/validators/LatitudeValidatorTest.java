package ca.corefacility.bioinformatics.irida.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.validators.annotations.validators.LatitudeValidator;

/**
 * Tests the behaviour of {@link LatitudeValidator}.
 * 
 *
 */
public class LatitudeValidatorTest {
	private LatitudeValidator validator;

	@Before
	public void setUp() {
		validator = new LatitudeValidator();
	}

	@Test
	public void testOneDigitPrefix() {
		String coord = "1.340";
		assertTrue(String.format("[%s] should be valid.", coord), validator.isValid(coord, null));
	}

	@Test
	public void testTwoDigitZeroPrefix() {
		String coord = "01.340";
		assertTrue(String.format("[%s] should be valid.", coord), validator.isValid(coord, null));
	}

	@Test
	public void testTwoDigitNonZeroPrefix() {
		String coord = "41.340";
		assertTrue(String.format("[%s] should be valid.", coord), validator.isValid(coord, null));
	}

	@Test
	public void testOneDigitNegativePrefix() {
		String coord = "-1.340";
		assertTrue(String.format("[%s] should be valid.", coord), validator.isValid(coord, null));
	}

	@Test
	public void testTwoDigitNegativeZeroPrefix() {
		String coord = "-01.340";
		assertTrue(String.format("[%s] should be valid.", coord), validator.isValid(coord, null));
	}

	@Test
	public void testTwoDigitNegativeNonZeroPrefix() {
		String coord = "-31.340";
		assertTrue(String.format("[%s] should be valid.", coord), validator.isValid(coord, null));
	}

	@Test
	public void testOutsideRangePositive() {
		String coord = "1000";
		assertFalse(String.format("[%s] should be valid.", coord), validator.isValid(coord, null));
	}

	@Test
	public void testOutsideRangeNegative() {
		String coord = "-1000";
		assertFalse(String.format("[%s] should be valid.", coord), validator.isValid(coord, null));
	}
}
