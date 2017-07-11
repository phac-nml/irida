package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ToolExecution;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisProvenanceServiceGalaxy;

import com.github.jmchilton.blend4j.galaxy.JobsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContentsProvenance;
import com.github.jmchilton.blend4j.galaxy.beans.JobDetails;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Tests for {@link AnalysisProvenanceServiceGalaxy}.
 * 
 *
 */
public class AnalysisProvenanceServiceGalaxyTest {

	private static final String FILENAME = "filename";

	private AnalysisProvenanceServiceGalaxy provenanceService;
	private GalaxyHistoriesService galaxyHistoriesService;
	private ToolsClient toolsClient;
	private JobsClient jobsClient;

	@Before
	public void setUp() {
		this.galaxyHistoriesService = mock(GalaxyHistoriesService.class);
		this.toolsClient = mock(ToolsClient.class);
		this.jobsClient = mock(JobsClient.class);
		this.provenanceService = new AnalysisProvenanceServiceGalaxy(galaxyHistoriesService, toolsClient, jobsClient);
	}

	@Test(expected = ExecutionManagerException.class)
	public void testHistoriesFailure() throws ExecutionManagerException {
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenThrow(new ExecutionManagerException());
		provenanceService.buildToolExecutionForOutputFile(analysisSubmission(), analysisOutputFile());
	}

	@Test(expected = ExecutionManagerException.class)
	public void testShowProvenanceFailureNoFiles() throws ExecutionManagerException {
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList());
		when(galaxyHistoriesService.showProvenance(any(String.class), any(String.class))).thenThrow(
				new ExecutionManagerException());
		provenanceService.buildToolExecutionForOutputFile(analysisSubmission(), analysisOutputFile());
	}

	@Test(expected = ExecutionManagerException.class)
	public void testShowProvenanceFailureNoFiles2() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName("wrong name");
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc));
		provenanceService.buildToolExecutionForOutputFile(analysisSubmission(), analysisOutputFile());
	}

	@Test(expected = ExecutionManagerException.class)
	public void testShowProvenanceFailureTooManyCooks() throws ExecutionManagerException {
		final HistoryContents hc1 = new HistoryContents();
		hc1.setName(FILENAME);
		final HistoryContents hc2 = new HistoryContents();
		hc2.setName(FILENAME);
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc1, hc2));
		provenanceService.buildToolExecutionForOutputFile(analysisSubmission(), analysisOutputFile());
	}

	@Test(expected = ExecutionManagerException.class)
	public void testCantFindTools() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc));
		when(galaxyHistoriesService.showProvenance(any(String.class), any(String.class))).thenReturn(
				new HistoryContentsProvenance());
		when(toolsClient.showTool(any(String.class))).thenThrow(new RuntimeException());
		provenanceService.buildToolExecutionForOutputFile(analysisSubmission(), analysisOutputFile());
	}

	@Test
	public void testBuildSingleStepToolExecutionOneParameter() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		final HistoryContentsProvenance hcp = new HistoryContentsProvenance();
		hcp.setParameters(ImmutableMap.of("akey", (Object) "avalue"));
		final JobDetails jd = new JobDetails();
		jd.setCommandLine("");
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc));
		when(galaxyHistoriesService.showProvenance(any(String.class), any(String.class))).thenReturn(hcp);
		when(toolsClient.showTool(any(String.class))).thenReturn(new Tool());
		when(jobsClient.showJob(any(String.class))).thenReturn(jd);
		final ToolExecution toolExecution = provenanceService.buildToolExecutionForOutputFile(analysisSubmission(),
				analysisOutputFile());
		assertTrue("tool execution should have the specified parameter.", toolExecution.getExecutionTimeParameters()
				.containsKey("akey"));
		assertEquals("tool execution parameter should be specified value.", "avalue", toolExecution
				.getExecutionTimeParameters().get("akey"));
		assertTrue("Tool execution should be considered input step, no predecessors.", toolExecution.isInputTool());
	}

	@Test
	public void testBuildSingleStepToolExecutionComplexParameters() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		final HistoryContentsProvenance hcp = new HistoryContentsProvenance();
		final Map<String, Object> mapWithNullValue = new HashMap<>();
		mapWithNullValue.put("key", null);
		final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
		builder.put("akey", (Object) "avalue").put("anotherKey", (Object) "{\"key\": \"value\"}")
				.put("thirdKey", (Object) ImmutableMap.of("key", "{\"key\":\"value\"}"))
				.put("fourthKey", (Object) "[{\"key\":\"value\"}]").put("fifthKey", (Object) mapWithNullValue)
				.put("abadkey", (Object) "[{\"key\":\"value\"]")
				.put("listKey", (Object) ImmutableList.of(ImmutableMap.of("key", "value")))
				.put("k", (Object) "value-lower").put("K", (Object) "value-upper")
				.put("\\\\keyWithBackslash", (Object) "value-backslash-lower")
				.put("\\\\KeyWithBackslash", (Object) "value-backslash-upper")
				.put("MULTIPLE_UPPER_case", (Object) "upper-case-values!");
		hcp.setParameters(builder.build());
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc));
		when(galaxyHistoriesService.showProvenance(any(String.class), any(String.class))).thenReturn(hcp);
		when(toolsClient.showTool(any(String.class))).thenReturn(new Tool());
		when(jobsClient.showJob(any(String.class))).thenReturn(new JobDetails());
		final ToolExecution toolExecution = provenanceService.buildToolExecutionForOutputFile(analysisSubmission(),
				analysisOutputFile());
		final Map<String, String> params = toolExecution.getExecutionTimeParameters();
		assertTrue("tool execution should have the specified parameter.", params.containsKey("akey"));
		assertEquals("tool execution parameter should be specified value.", "avalue", params.get("akey"));
		assertTrue("tool execution should have the specified parameter.", params.containsKey("anotherKey.key"));
		assertEquals("tool execution parameter should be specified value.", "value", params.get("anotherKey.key"));
		assertTrue("tool execution should have the specified parameter.", params.containsKey("thirdKey.key.key"));
		assertEquals("tool execution parameter should be specified value.", "value", params.get("thirdKey.key.key"));
		assertTrue("tool execution should have the specified parameter.", params.containsKey("fourthKey.key"));
		assertEquals("tool execution parameter should be specified value.", "value", params.get("fourthKey.key"));
		assertTrue("tool execution should have the specified parameter.", params.containsKey("fifthKey.key"));
		assertEquals("tool execution parameter should be specified value.",
				AnalysisProvenanceServiceGalaxy.emptyValuePlaceholder(), params.get("fifthKey.key"));
		assertTrue("tool execution should have the specified parameter.", params.containsKey("abadkey"));
		assertEquals("tool execution parameter should be specified value.", "[{\"key\":\"value\"]",
				params.get("abadkey"));
		assertTrue("tool execution should have the specified parameter.", params.containsKey("listKey.key"));
		assertEquals("tool execution parameter should be specified value.", "value", params.get("listKey.key"));
		assertTrue("tool execution should have the specified parameter.", params.containsKey("k"));
		assertEquals("tool execution parameter should be specified value.", "value-lower", params.get("k"));
		assertTrue("tool execution should have the specified parameter.", params.containsKey("K"));
		assertEquals("tool execution parameter should be specified value.", "value-upper", params.get("K"));
		assertTrue("tool execution should have the specified parameter.", params.containsKey("\\\\keyWithBackslash"));
		assertEquals("tool execution parameter should be specified value.", "value-backslash-lower",
				params.get("\\\\keyWithBackslash"));
		assertTrue("tool execution should have the specified parameter.", params.containsKey("\\\\KeyWithBackslash"));
		assertEquals("tool execution parameter should be specified value.", "value-backslash-upper",
				params.get("\\\\KeyWithBackslash"));
		assertTrue("tool execution should have the specified parameter.", params.containsKey("MULTIPLE_UPPER_case"));
		assertEquals("tool execution parameter should be specified value.", "upper-case-values!",
				params.get("MULTIPLE_UPPER_case"));

		assertTrue("Tool execution should be considered input step, no predecessors.", toolExecution.isInputTool());
	}

	@Test
	public void testBuildSingleStepToolExecutionOneParameterOnePredecessor() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		hc.setId("historyContentsId");
		final HistoryContentsProvenance hcpWithPredecessor = new HistoryContentsProvenance();
		hcpWithPredecessor.setParameters(ImmutableMap.of("akey", (Object) ImmutableMap.of("id", "previousKey")));
		final HistoryContentsProvenance hcpWithoutPredecessor = new HistoryContentsProvenance();
		hcpWithoutPredecessor.setParameters(ImmutableMap.of("akey", (Object) "value"));
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc));
		when(galaxyHistoriesService.showProvenance(any(String.class), eq("historyContentsId"))).thenReturn(
				hcpWithPredecessor);
		when(galaxyHistoriesService.showProvenance(any(String.class), eq("previousKey"))).thenReturn(
				hcpWithoutPredecessor);
		when(jobsClient.showJob(any(String.class))).thenReturn(new JobDetails());
		when(toolsClient.showTool(any(String.class))).thenReturn(new Tool());
		final ToolExecution toolExecution = provenanceService.buildToolExecutionForOutputFile(analysisSubmission(),
				analysisOutputFile());
		assertFalse("tool execution should not have an ID parameter.", toolExecution.getExecutionTimeParameters()
				.containsKey("akey"));
		assertFalse("tool execution should not have an ID parameter.", toolExecution.getExecutionTimeParameters()
				.containsKey("akey.id"));
		assertFalse("Tool execution has one predecessor, not input step.", toolExecution.isInputTool());
		final ToolExecution predecessor = toolExecution.getPreviousSteps().iterator().next();
		assertTrue("predecessor step is input step.", predecessor.isInputTool());
	}
	
	@Test
	public void testBuildSingleStepToolExecutionListParameters() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		final HistoryContentsProvenance hcp = new HistoryContentsProvenance();
		hcp.setParameters(ImmutableMap.<String, Object>builder()
				.put("akey", "[\"avalue\"]")
				.put("akey2", Lists.newArrayList("avalue2"))
				.put("akey3", "[]")
				.put("akey4", Lists.newArrayList())
				.put("akey5", "[\"avalue5.1\", \"avalue5.2\"]")
				.put("akey6", Lists.newArrayList("avalue6.1", "avalue6.2")).build());
		final JobDetails jd = new JobDetails();
		jd.setCommandLine("");
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc));
		when(galaxyHistoriesService.showProvenance(any(String.class), any(String.class))).thenReturn(hcp);
		when(toolsClient.showTool(any(String.class))).thenReturn(new Tool());
		when(jobsClient.showJob(any(String.class))).thenReturn(jd);
		final ToolExecution toolExecution = provenanceService.buildToolExecutionForOutputFile(analysisSubmission(),
				analysisOutputFile());
		assertTrue("tool execution should have the specified parameter.", toolExecution.getExecutionTimeParameters()
				.containsKey("akey"));
		assertEquals("tool execution parameter should be specified value.", "[avalue]", toolExecution
				.getExecutionTimeParameters().get("akey"));
		assertTrue("tool execution should have the specified parameter.", toolExecution.getExecutionTimeParameters()
				.containsKey("akey2"));
		assertEquals("tool execution parameter should be specified value.", "[avalue2]", toolExecution
				.getExecutionTimeParameters().get("akey2"));
		assertEquals("tool execution parameter should be specified value.", "[]", toolExecution
				.getExecutionTimeParameters().get("akey3"));
		assertEquals("tool execution parameter should be specified value.", "[]", toolExecution
				.getExecutionTimeParameters().get("akey4"));
		assertEquals("tool execution parameter should be specified value.", "[avalue5.1, avalue5.2]", toolExecution
				.getExecutionTimeParameters().get("akey5"));
		assertEquals("tool execution parameter should be specified value.", "[avalue6.1, avalue6.2]", toolExecution
				.getExecutionTimeParameters().get("akey6"));
		assertTrue("Tool execution should be considered input step, no predecessors.", toolExecution.isInputTool());
	}
	
	@Test
	public void testBuildSingleStepToolExecutionStrangeDataStructureDoToString() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		final HistoryContentsProvenance hcp = new HistoryContentsProvenance();
		hcp.setParameters(ImmutableMap.of("akey", "[[\"avalue\"]]"));
		final JobDetails jd = new JobDetails();
		jd.setCommandLine("");
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc));
		when(galaxyHistoriesService.showProvenance(any(String.class), any(String.class))).thenReturn(hcp);
		when(toolsClient.showTool(any(String.class))).thenReturn(new Tool());
		when(jobsClient.showJob(any(String.class))).thenReturn(jd);
		final ToolExecution toolExecution = provenanceService.buildToolExecutionForOutputFile(analysisSubmission(),
				analysisOutputFile());
		assertTrue("tool execution should have the specified parameter.", toolExecution.getExecutionTimeParameters()
				.containsKey("akey"));
		assertEquals("tool execution parameter should be specified value.", "[[\"avalue\"]]", toolExecution
				.getExecutionTimeParameters().get("akey"));
	}

	private String analysisSubmission() {
		return UUID.randomUUID().toString();
	}

	private String analysisOutputFile() {
		return new AnalysisOutputFile(Paths.get("/" + FILENAME), "", "", null).getFile().getFileName().toString();
	}
}
