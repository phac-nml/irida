package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaToolParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisParameterServiceGalaxy;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
	}

	/**
	 * Tests preparing workflow parameters and overriding with custom value
	 * successfully.
	 */
	@Test
	public void testPrepareParametersOverrideSuccess() {
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
	 */
	@Test
	public void testPrepareParametersDefaultSuccess() {
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
}
