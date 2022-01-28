package ca.corefacility.bioinformatics.irida.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import ca.corefacility.bioinformatics.irida.validators.annotations.validators.LongitudeValidator;

/**
 * Tests the behaviour of {@link LongitudeValidator}.
 * 
 *
 */
public class LongitudeValidatorTest {

	private LongitudeValidator validator;

	@BeforeEach
	public void setUp() {
		validator = new LongitudeValidator();
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
	public void testThreeDigitZeroPrefix() {
		String coord = "001.394";
		assertTrue(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}
	
	@Test
	public void testThreeDigitNonZeroPrefix() {
		String coord = "011.394";
		assertTrue(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}
	
	@Test
	public void testThreeDigitNonZeroPrefix2() {
		String coord = "111.394";
		assertTrue(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}
	
	@Test
	public void testThreeDigitNegativeZeroPrefix() {
		String coord = "-001.394";
		assertTrue(validator.isValid(coord, null), String.format("[%s] should be valid.", coord));
	}
	
	@Test
	public void testThreeDigitNegativeNonZeroPrefix() {
		String coord = "-111.394";
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
