package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.ParameterBuilderGalaxy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Tests out building up a data structure of parameters for Galaxy.
 * 
 *
 */
public class ParameterBuilderGalaxyTest {

	private ParameterBuilderGalaxy parameterBuilderGalaxy;

	/**
	 * Setup for tests.
	 */
	@BeforeEach
	public void setup() {
		parameterBuilderGalaxy = new ParameterBuilderGalaxy();
	}

	/**
	 * Tests out failing to add a parameter due to null tool id.
	 */
	@Test
	public void testParameterBuilderGalaxyFailNullToolId() {
		assertThrows(IllegalArgumentException.class, () -> {
			parameterBuilderGalaxy.addParameter(null, "parameter", "value");
		});
	}

	/**
	 * Tests out failing to add a parameter due to empty tool id.
	 */
	@Test
	public void testParameterBuilderGalaxyFailEmptyToolId() {
		assertThrows(IllegalArgumentException.class, () -> {
			parameterBuilderGalaxy.addParameter("", "parameter", "value");
		});
	}

	/**
	 * Tests out failing to add a parameter due to null name.
	 */
	@Test
	public void testParameterBuilderGalaxyFailNullParameter() {
		assertThrows(IllegalArgumentException.class, () -> {
			parameterBuilderGalaxy.addParameter("toolId", null, "value");
		});
	}

	/**
	 * Tests out failing to add a parameter due to an empty name.
	 */
	@Test
	public void testParameterBuilderGalaxyFailEmptyParameter() {
		assertThrows(IllegalArgumentException.class, () -> {
			parameterBuilderGalaxy.addParameter("toolId", "", "value");
		});
	}

	/**
	 * Tests out failing to add a parameter due to a null value.
	 */
	@Test
	public void testParameterBuilderGalaxyFailNullValue() {
		assertThrows(NullPointerException.class, () -> {
			parameterBuilderGalaxy.addParameter("toolId", "parameter", null);
		});
	}

	/**
	 * Tests out building a data structure with only one simple parameter
	 * mapping for Galaxy.
	 */
	@Test
	public void testParameterBuilderGalaxySuccessOneParameter() {
		parameterBuilderGalaxy.addParameter("toolId", "parameter", "value");

		ParameterBuilderGalaxy.ParameterId expectedParameterId = new ParameterBuilderGalaxy.ParameterId("toolId",
				"parameter");
		assertEquals(Sets.newHashSet(expectedParameterId), parameterBuilderGalaxy.getParameterIds(),
				"parameter id sets are the same");
		assertEquals("value", parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId),
				"parameter mappings are the same");
	}

	/**
	 * Tests out building a data structure with two simple parameter mappings
	 * for Galaxy.
	 */
	@Test
	public void testParameterBuilderGalaxySuccessTwoParameters() {
		parameterBuilderGalaxy.addParameter("toolId", "parameter", "value");
		parameterBuilderGalaxy.addParameter("toolId", "parameter2", "value2");

		ParameterBuilderGalaxy.ParameterId expectedParameterId1 = new ParameterBuilderGalaxy.ParameterId("toolId",
				"parameter");
		ParameterBuilderGalaxy.ParameterId expectedParameterId2 = new ParameterBuilderGalaxy.ParameterId("toolId",
				"parameter2");
		assertEquals(Sets.newHashSet(expectedParameterId1, expectedParameterId2),
				parameterBuilderGalaxy.getParameterIds(),
				"parameter id sets are the same");
		assertEquals("value", parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId1),
				"first parameter mappings are the same");
		assertEquals("value2", parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId2),
				"second parameter mappings are the same");
	}

	/**
	 * Tests out building a data structure with one simple parameter mapping and
	 * two tools for Galaxy.
	 */
	@Test
	public void testParameterBuilderGalaxySuccessTwoTools() {
		parameterBuilderGalaxy.addParameter("toolId", "parameter", "value");
		parameterBuilderGalaxy.addParameter("toolId2", "parameter", "value2");

		ParameterBuilderGalaxy.ParameterId expectedParameterId1 = new ParameterBuilderGalaxy.ParameterId("toolId",
				"parameter");
		ParameterBuilderGalaxy.ParameterId expectedParameterId2 = new ParameterBuilderGalaxy.ParameterId("toolId2",
				"parameter");
		assertEquals(Sets.newHashSet(expectedParameterId1, expectedParameterId2),
				parameterBuilderGalaxy.getParameterIds(),
				"parameter id sets are the same");
		assertEquals("value", parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId1),
				"first parameter mappings are the same");
		assertEquals("value2", parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId2),
				"second parameter mappings are the same");
	}

	/**
	 * Tests out building a data structure with two levels in parameter name for
	 * Galaxy.
	 */
	@Test
	public void testParameterBuilderGalaxySuccessTwoLevelParameter() {
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level2", "value");

		ParameterBuilderGalaxy.ParameterId expectedParameterId = new ParameterBuilderGalaxy.ParameterId("toolId",
				"parameter");
		assertEquals(Sets.newHashSet(expectedParameterId), parameterBuilderGalaxy.getParameterIds(),
				"parameter id sets are the same");
		assertEquals(ImmutableMap.of("level2", "value"),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId),
				"parameter mappings are the same");
	}

	/**
	 * Tests out building a data structure with three levels in parameter name
	 * for Galaxy.
	 */
	@Test
	public void testParameterBuilderGalaxySuccessThreeLevelParameter() {
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level2.level3", "value");

		ParameterBuilderGalaxy.ParameterId expectedParameterId = new ParameterBuilderGalaxy.ParameterId("toolId",
				"parameter");
		assertEquals(Sets.newHashSet(expectedParameterId), parameterBuilderGalaxy.getParameterIds(),
				"parameter id sets are the same");
		assertEquals(ImmutableMap.of("level2", ImmutableMap.of("level3", "value")),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId),
				"parameter mappings are the same");
	}

	/**
	 * Tests out building a data structure with two two level parameters (not
	 * overriding each other).
	 */
	@Test
	public void testParameterBuilderGalaxySuccessTwoTwoLevelParameter() {
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level2a", "valuea");
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level2b", "valueb");

		ParameterBuilderGalaxy.ParameterId expectedParameterId = new ParameterBuilderGalaxy.ParameterId("toolId",
				"parameter");
		assertEquals(Sets.newHashSet(expectedParameterId), parameterBuilderGalaxy.getParameterIds(),
				"parameter id sets are the same");
		assertEquals(ImmutableMap.of("level2a", "valuea", "level2b", "valueb"),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId),
				"parameter mappings are the same");
	}

	/**
	 * Tests out building a data structure with mixed two and three level
	 * parameters (not overriding each other).
	 */
	@Test
	public void testParameterBuilderGalaxySuccessMixedLevelParameter() {
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level2.level3a", "valuea");
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level", "value");
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level2.level3b", "valueb");

		ParameterBuilderGalaxy.ParameterId expectedParameterId = new ParameterBuilderGalaxy.ParameterId("toolId",
				"parameter");
		assertEquals(Sets.newHashSet(expectedParameterId), parameterBuilderGalaxy.getParameterIds(),
				"parameter id sets are the same");
		assertEquals(
				ImmutableMap.of("level", "value", "level2", ImmutableMap.of("level3a", "valuea", "level3b", "valueb")),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId),
				"parameter mappings are the same");
	}

	/**
	 * Tests out building a data structure with two two level parameters and two
	 * start names (not overriding each other).
	 */
	@Test
	public void testParameterBuilderGalaxySuccessTwoFirstNameTwoTwoLevelParameter() {
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level2a", "valuea");
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level2b", "valueb");
		parameterBuilderGalaxy.addParameter("toolId", "parameter2.level2", "value");

		ParameterBuilderGalaxy.ParameterId expectedParameterId1 = new ParameterBuilderGalaxy.ParameterId("toolId",
				"parameter");
		ParameterBuilderGalaxy.ParameterId expectedParameterId2 = new ParameterBuilderGalaxy.ParameterId("toolId",
				"parameter2");
		assertEquals(Sets.newHashSet(expectedParameterId1, expectedParameterId2),
				parameterBuilderGalaxy.getParameterIds(), "parameter id sets are the same");
		assertEquals(ImmutableMap.of("level2a", "valuea", "level2b", "valueb"),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId1),
				"parameter mappings are the same");
		assertEquals(ImmutableMap.of("level2", "value"),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId2),
				"parameter mappings are the same");
	}
	
	/**
	 * Tests out building a data structure with a mapping of "level2" to a map and also a simple value.
	 */
	@Test
	public void testParameterBuilderGalaxyFailMixedLevelParameterAndSimpleParameter() {
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level2.level3a", "valuea");
		assertThrows(IllegalArgumentException.class, () -> {
			parameterBuilderGalaxy.addParameter("toolId", "parameter.level2", "value");
		});
	}
}
