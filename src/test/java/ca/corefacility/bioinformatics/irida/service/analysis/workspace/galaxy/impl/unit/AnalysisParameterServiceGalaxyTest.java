package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInvocationInputs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowParameterException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaToolParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisParameterServiceGalaxy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Tests for the {@link AnalysisParameterServiceGalaxy} class.
 *
 *
 */
public class AnalysisParameterServiceGalaxyTest {

	@Mock
	private IridaWorkflow iridaWorkflow;

	@Mock
	private IridaWorkflowDescription iridaWorkflowDescription;

	private AnalysisParameterServiceGalaxy analysisParameterService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		analysisParameterService = new AnalysisParameterServiceGalaxy();

		when(iridaWorkflow.getWorkflowDescription()).thenReturn(iridaWorkflowDescription);

		IridaToolParameter iridaToolParameter = new IridaToolParameter("galaxy-tool1", "parameter1");
		IridaWorkflowParameter parameter1 = new IridaWorkflowParameter("parameter1", "0",
				Lists.newArrayList(iridaToolParameter));
		List<IridaWorkflowParameter> iridaWorkflowParameters = Lists.newArrayList(parameter1);

		when(iridaWorkflowDescription.getParameters()).thenReturn(iridaWorkflowParameters);
		when(iridaWorkflowDescription.acceptsParameters()).thenReturn(true);
	}

	/**
	 * Tests preparing workflow parameters and overriding with custom value
	 * successfully.
	 *
	 * @throws IridaWorkflowParameterException
	 */
	@Test
	public void testPrepareParametersOverrideSuccess() throws IridaWorkflowParameterException {
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("parameter1", "1");

		WorkflowInputsGalaxy workflowInputsGalaxy = analysisParameterService.prepareAnalysisParameters(parameters,
				iridaWorkflow);

		assertNotNull(workflowInputsGalaxy, "workflowInputsGalaxy is null");

		WorkflowInvocationInputs workflowInvocationInputs = workflowInputsGalaxy.getInputsObject();
		Map<Object, Map<String, Object>> workflowParameters = workflowInvocationInputs.getParameters();
		Map<String, Object> tool1Parameters = workflowParameters.get("galaxy-tool1");
		assertNotNull(tool1Parameters, "parameters for galaxy-tool1 should not be null");

		assertEquals("1", tool1Parameters.get("parameter1"), "galaxy-tool1,parameter1 is not valid");
	}

	/**
	 * Tests preparing workflow parameters with multiple levels and overriding
	 * with custom value successfully.
	 *
	 * @throws IridaWorkflowParameterException
	 */
	@Test
	public void testPrepareParametersOverrideMultipleLevelSuccess() throws IridaWorkflowParameterException {
		IridaToolParameter iridaToolParameter = new IridaToolParameter("galaxy-tool1", "level1.parameter1");
		IridaWorkflowParameter parameter1 = new IridaWorkflowParameter("parameter1", "0",
				Lists.newArrayList(iridaToolParameter));
		List<IridaWorkflowParameter> iridaWorkflowParameters = Lists.newArrayList(parameter1);

		when(iridaWorkflowDescription.getParameters()).thenReturn(iridaWorkflowParameters);

		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("parameter1", "1");

		WorkflowInputsGalaxy workflowInputsGalaxy = analysisParameterService.prepareAnalysisParameters(parameters,
				iridaWorkflow);

		assertNotNull(workflowInputsGalaxy, "workflowInputsGalaxy is null");

		WorkflowInvocationInputs workflowInvocationInputs = workflowInputsGalaxy.getInputsObject();
		Map<Object, Map<String, Object>> workflowParameters = workflowInvocationInputs.getParameters();
		Map<String, Object> tool1Parameters = workflowParameters.get("galaxy-tool1");
		assertNotNull(tool1Parameters, "parameters for galaxy-tool1 should not be null");

		assertEquals(ImmutableMap.of("level1", ImmutableMap.of("parameter1", "1")), tool1Parameters, 
				"parameter not properly defined");
	}

	/**
	 * Tests preparing workflow parameters with multiple levels, multiple
	 * parameters and overriding with custom value successfully.
	 *
	 * @throws IridaWorkflowParameterException
	 */
	@Test
	public void testPrepareParametersOverrideMultipleLevelMultipleParameterSuccess()
			throws IridaWorkflowParameterException {
		IridaToolParameter iridaToolParameter = new IridaToolParameter("galaxy-tool1", "level1.parameter1");
		IridaToolParameter iridaToolParameter2 = new IridaToolParameter("galaxy-tool1", "level1.parameter2");
		IridaWorkflowParameter parameter1 = new IridaWorkflowParameter("parameter1", "0", Lists.newArrayList(
				iridaToolParameter, iridaToolParameter2));
		List<IridaWorkflowParameter> iridaWorkflowParameters = Lists.newArrayList(parameter1);

		when(iridaWorkflowDescription.getParameters()).thenReturn(iridaWorkflowParameters);

		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("parameter1", "1");

		WorkflowInputsGalaxy workflowInputsGalaxy = analysisParameterService.prepareAnalysisParameters(parameters,
				iridaWorkflow);

		assertNotNull(workflowInputsGalaxy, "workflowInputsGalaxy is null");

		WorkflowInvocationInputs workflowInvocationInputs = workflowInputsGalaxy.getInputsObject();
		Map<Object, Map<String, Object>> workflowParameters = workflowInvocationInputs.getParameters();
		Map<String, Object> tool1Parameters = workflowParameters.get("galaxy-tool1");
		assertNotNull(tool1Parameters, "parameters for galaxy-tool1 should not be null");

		assertEquals(ImmutableMap.of("level1", ImmutableMap.of("parameter1", "1", "parameter2", "1")), tool1Parameters,
				"parameter not properly defined");
	}

	/**
	 * Tests preparing workflow parameters with three levels, multiple
	 * parameters and overriding with custom value successfully.
	 *
	 * @throws IridaWorkflowParameterException
	 */
	@Test
	public void testPrepareParametersOverrideThreeLevelMultipleParameterSuccess()
			throws IridaWorkflowParameterException {
		IridaToolParameter iridaToolParameter = new IridaToolParameter("galaxy-tool1", "level1.level2.parameter1");
		IridaToolParameter iridaToolParameter2 = new IridaToolParameter("galaxy-tool1", "level1.level2.parameter2");
		IridaToolParameter iridaToolParameter3 = new IridaToolParameter("galaxy-tool1", "level1.parameter3");
		IridaWorkflowParameter parameter1 = new IridaWorkflowParameter("parameter1", "0", Lists.newArrayList(
				iridaToolParameter, iridaToolParameter2, iridaToolParameter3));
		List<IridaWorkflowParameter> iridaWorkflowParameters = Lists.newArrayList(parameter1);

		when(iridaWorkflowDescription.getParameters()).thenReturn(iridaWorkflowParameters);

		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("parameter1", "1");

		WorkflowInputsGalaxy workflowInputsGalaxy = analysisParameterService.prepareAnalysisParameters(parameters,
				iridaWorkflow);

		assertNotNull(workflowInputsGalaxy, "workflowInputsGalaxy is null");

		WorkflowInvocationInputs workflowInvocationInputs = workflowInputsGalaxy.getInputsObject();
		Map<Object, Map<String, Object>> workflowParameters = workflowInvocationInputs.getParameters();
		Map<String, Object> tool1Parameters = workflowParameters.get("galaxy-tool1");
		assertNotNull(tool1Parameters, "parameters for galaxy-tool1 should not be null");

		assertEquals(ImmutableMap.of("level1",
						ImmutableMap.of("parameter3", "1", "level2", ImmutableMap.of("parameter1", "1", "parameter2", "1"))),
				tool1Parameters, "parameter not properly defined");
	}

	/**
	 * Tests preparing workflow parameters and using the default value defined.
	 *
	 * @throws IridaWorkflowParameterException
	 */
	@Test
	public void testPrepareParametersDefaultSuccess() throws IridaWorkflowParameterException {
		Map<String, String> parameters = Maps.newHashMap();

		WorkflowInputsGalaxy workflowInputsGalaxy = analysisParameterService.prepareAnalysisParameters(parameters,
				iridaWorkflow);
		assertNotNull(workflowInputsGalaxy, "workflowInputsGalaxy is null");

		WorkflowInvocationInputs workflowInvocationInputs = workflowInputsGalaxy.getInputsObject();
		Map<Object, Map<String, Object>> workflowParameters = workflowInvocationInputs.getParameters();
		Map<String, Object> tool1Parameters = workflowParameters.get("galaxy-tool1");
		assertNotNull(tool1Parameters, "parameters for galaxy-tool1 should not be null");

		assertEquals("0", tool1Parameters.get("parameter1"), "galaxy-tool1,parameter1 is not valid");
	}

	/**
	 * Tests preparing workflow parameters and ignoring the default value successfully.
	 *
	 * @throws IridaWorkflowParameterException
	 */
	@Test
	public void testPrepareParametersIgnoreDefaultSuccess() throws IridaWorkflowParameterException {
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("parameter1", IridaWorkflowParameter.IGNORE_DEFAULT_VALUE);

		WorkflowInputsGalaxy workflowInputsGalaxy = analysisParameterService.prepareAnalysisParameters(parameters,
				iridaWorkflow);
		assertNotNull(workflowInputsGalaxy, "workflowInputsGalaxy is null");

		WorkflowInvocationInputs workflowInvocationInputs = workflowInputsGalaxy.getInputsObject();
		Map<Object, Map<String, Object>> workflowParameters = workflowInvocationInputs.getParameters();
		assertNull(workflowParameters.get("galaxy-tool1"), "should be no parameter set for galaxy-tool1");
	}

	/**
	 * Tests preparing workflow parameters and overriding with custom value
	 * successfully in two tools.
	 *
	 * @throws IridaWorkflowParameterException
	 */
	@Test
	public void testPrepareParametersOverrideSuccessTwoTools() throws IridaWorkflowParameterException {
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("parameter1", "1");

		IridaToolParameter iridaToolParameter = new IridaToolParameter("galaxy-tool1", "parameter1");
		IridaToolParameter iridaToolParameter2 = new IridaToolParameter("galaxy-tool1", "parameter2");
		IridaWorkflowParameter parameter1 = new IridaWorkflowParameter("parameter1", "0", Lists.newArrayList(
				iridaToolParameter, iridaToolParameter2));
		List<IridaWorkflowParameter> iridaWorkflowParameters = Lists.newArrayList(parameter1);

		when(iridaWorkflowDescription.getParameters()).thenReturn(iridaWorkflowParameters);

		WorkflowInputsGalaxy workflowInputsGalaxy = analysisParameterService.prepareAnalysisParameters(parameters,
				iridaWorkflow);

		assertNotNull(workflowInputsGalaxy, "workflowInputsGalaxy is null");

		WorkflowInvocationInputs workflowInvocationInputs = workflowInputsGalaxy.getInputsObject();
		Map<Object, Map<String, Object>> workflowParameters = workflowInvocationInputs.getParameters();

		Map<String, Object> tool1Parameters = workflowParameters.get("galaxy-tool1");
		assertNotNull(tool1Parameters, "parameters for galaxy-tool1 should not be null");
		assertEquals("1", tool1Parameters.get("parameter1"), "galaxy-tool1,parameter1 is not valid");
		assertEquals("1", tool1Parameters.get("parameter2"), "galaxy-tool1,parameter2 is not valid");
	}

	/**
	 * Tests preparing workflow parameters and failing due to parameters not
	 * defined within the IRIDA workflow.
	 *
	 * @throws IridaWorkflowParameterException
	 */
	@Test
	public void testPrepareParametersOverrideFail() throws IridaWorkflowParameterException {
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("parameter-invalid", "1");

		assertThrows(IridaWorkflowParameterException.class, () -> {
			analysisParameterService.prepareAnalysisParameters(parameters, iridaWorkflow);
		});
	}

	/**
	 * Tests preparing workflow parameters when there are no parameters to prepare.
	 *
	 * @throws IridaWorkflowParameterException
	 */
	@Test
	public void testPrepareParametersSuccessNoParameters() throws IridaWorkflowParameterException {
		when(iridaWorkflowDescription.acceptsParameters()).thenReturn(false);

		WorkflowInputsGalaxy workflowInputsGalaxy = analysisParameterService.prepareAnalysisParameters(ImmutableMap.of(),
				iridaWorkflow);

		assertNotNull(workflowInputsGalaxy, "workflowInputsGalaxy is null");

		WorkflowInvocationInputs workflowInvocationInputs = workflowInputsGalaxy.getInputsObject();
		assertNotNull(workflowInvocationInputs, "workflowInvocationInputs is null");

		verify(iridaWorkflowDescription).acceptsParameters();
	}

	/**
	 * Tests failing to prepare workflow parameters when there are some parameters passed but the workflow accepts no parameters.
	 *
	 * @throws IridaWorkflowParameterException
	 */
	@Test
	public void testPrepareParametersSuccessNoAcceptParametersWithParameters() throws IridaWorkflowParameterException {
		when(iridaWorkflowDescription.acceptsParameters()).thenReturn(false);

		assertThrows(IridaWorkflowParameterException.class, () -> {
			analysisParameterService.prepareAnalysisParameters(ImmutableMap.of("name", "value"),
					iridaWorkflow);
		});
	}
}
