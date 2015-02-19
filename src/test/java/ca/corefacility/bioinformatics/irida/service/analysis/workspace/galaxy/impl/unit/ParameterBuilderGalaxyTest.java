package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.ParameterBuilderGalaxy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Tests out building up a data structure of parameters for Galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class ParameterBuilderGalaxyTest {

	private ParameterBuilderGalaxy parameterBuilderGalaxy;

	/**
	 * Setup for tests.
	 */
	@Before
	public void setup() {
		parameterBuilderGalaxy = new ParameterBuilderGalaxy();
	}

	/**
	 * Tests out failing to add a parameter due to null tool id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParameterBuilderGalaxyFailNullToolId() {
		parameterBuilderGalaxy.addParameter(null, "parameter", "value");
	}

	/**
	 * Tests out failing to add a parameter due to empty tool id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParameterBuilderGalaxyFailEmptyToolId() {
		parameterBuilderGalaxy.addParameter("", "parameter", "value");
	}

	/**
	 * Tests out failing to add a parameter due to null name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParameterBuilderGalaxyFailNullParameter() {
		parameterBuilderGalaxy.addParameter("toolId", null, "value");
	}

	/**
	 * Tests out failing to add a parameter due to an empty name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParameterBuilderGalaxyFailEmptyParameter() {
		parameterBuilderGalaxy.addParameter("toolId", "", "value");
	}

	/**
	 * Tests out failing to add a parameter due to a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void testParameterBuilderGalaxyFailNullValue() {
		parameterBuilderGalaxy.addParameter("toolId", "parameter", null);
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
		assertEquals("parameter id sets are the same", Sets.newHashSet(expectedParameterId),
				parameterBuilderGalaxy.getParameterIds());
		assertEquals("parameter mappings are the same", "value",
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId));
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
		assertEquals("parameter id sets are the same", Sets.newHashSet(expectedParameterId1, expectedParameterId2),
				parameterBuilderGalaxy.getParameterIds());
		assertEquals("first parameter mappings are the same", "value",
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId1));
		assertEquals("second parameter mappings are the same", "value2",
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId2));
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
		assertEquals("parameter id sets are the same", Sets.newHashSet(expectedParameterId1, expectedParameterId2),
				parameterBuilderGalaxy.getParameterIds());
		assertEquals("first parameter mappings are the same", "value",
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId1));
		assertEquals("second parameter mappings are the same", "value2",
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId2));
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
		assertEquals("parameter id sets are the same", Sets.newHashSet(expectedParameterId),
				parameterBuilderGalaxy.getParameterIds());
		assertEquals("parameter mappings are the same", ImmutableMap.of("level2", "value"),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId));
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
		assertEquals("parameter id sets are the same", Sets.newHashSet(expectedParameterId),
				parameterBuilderGalaxy.getParameterIds());
		assertEquals("parameter mappings are the same", ImmutableMap.of("level2", ImmutableMap.of("level3", "value")),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId));
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
		assertEquals("parameter id sets are the same", Sets.newHashSet(expectedParameterId),
				parameterBuilderGalaxy.getParameterIds());
		assertEquals("parameter mappings are the same", ImmutableMap.of("level2a", "valuea", "level2b", "valueb"),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId));
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
		assertEquals("parameter id sets are the same", Sets.newHashSet(expectedParameterId),
				parameterBuilderGalaxy.getParameterIds());
		assertEquals(
				"parameter mappings are the same",
				ImmutableMap.of("level", "value", "level2", ImmutableMap.of("level3a", "valuea", "level3b", "valueb")),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId));
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
		assertEquals("parameter id sets are the same", Sets.newHashSet(expectedParameterId1, expectedParameterId2),
				parameterBuilderGalaxy.getParameterIds());
		assertEquals("parameter mappings are the same", ImmutableMap.of("level2a", "valuea", "level2b", "valueb"),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId1));
		assertEquals("parameter mappings are the same", ImmutableMap.of("level2", "value"),
				parameterBuilderGalaxy.getMappingForParameterId(expectedParameterId2));
	}
	
	/**
	 * Tests out building a data structure with a mapping of "level2" to a map and also a simple value.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testParameterBuilderGalaxyFailMixedLevelParameterAndSimpleParameter() {
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level2.level3a", "valuea");
		parameterBuilderGalaxy.addParameter("toolId", "parameter.level2", "value");
	}
}
