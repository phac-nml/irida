package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowParameterException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaToolParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisParameterServiceGalaxy;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Tests for the {@link AnalysisParameterServiceGalaxy} class.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisParameterServiceGalaxyTest {

	@Mock
	private IridaWorkflow iridaWorkflow;

	@Mock
	private IridaWorkflowDescription iridaWorkflowDescription;

	private AnalysisParameterServiceGalaxy analysisParameterService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

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

		assertNotNull("workflowInputsGalaxy is null", workflowInputsGalaxy);

		WorkflowInputs workflowInputs = workflowInputsGalaxy.getInputsObject();
		Map<Object, Map<String, Object>> workflowParameters = workflowInputs.getParameters();
		Map<String, Object> tool1Parameters = workflowParameters.get("galaxy-tool1");
		assertNotNull("parameters for galaxy-tool1 should not be null", tool1Parameters);

		assertEquals("galaxy-tool1,parameter1 is not valid", "1", tool1Parameters.get("parameter1"));
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
		assertNotNull("workflowInputsGalaxy is null", workflowInputsGalaxy);

		WorkflowInputs workflowInputs = workflowInputsGalaxy.getInputsObject();
		Map<Object, Map<String, Object>> workflowParameters = workflowInputs.getParameters();
		Map<String, Object> tool1Parameters = workflowParameters.get("galaxy-tool1");
		assertNotNull("parameters for galaxy-tool1 should not be null", tool1Parameters);

		assertEquals("galaxy-tool1,parameter1 is not valid", "0", tool1Parameters.get("parameter1"));
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

		assertNotNull("workflowInputsGalaxy is null", workflowInputsGalaxy);

		WorkflowInputs workflowInputs = workflowInputsGalaxy.getInputsObject();
		Map<Object, Map<String, Object>> workflowParameters = workflowInputs.getParameters();

		Map<String, Object> tool1Parameters = workflowParameters.get("galaxy-tool1");
		assertNotNull("parameters for galaxy-tool1 should not be null", tool1Parameters);
		assertEquals("galaxy-tool1,parameter1 is not valid", "1", tool1Parameters.get("parameter1"));
		assertEquals("galaxy-tool1,parameter2 is not valid", "1", tool1Parameters.get("parameter2"));
	}

	/**
	 * Tests preparing workflow parameters and failing due to parameters not
	 * defined within the IRIDA workflow.
	 * 
	 * @throws IridaWorkflowParameterException
	 */
	@Test(expected = IridaWorkflowParameterException.class)
	public void testPrepareParametersOverrideFail() throws IridaWorkflowParameterException {
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("parameter-invalid", "1");

		analysisParameterService.prepareAnalysisParameters(parameters, iridaWorkflow);
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

		assertNotNull("workflowInputsGalaxy is null", workflowInputsGalaxy);

		WorkflowInputs workflowInputs = workflowInputsGalaxy.getInputsObject();
		assertNotNull("workflowInputs is null", workflowInputs);
		
		verify(iridaWorkflowDescription).acceptsParameters();
	}
}
