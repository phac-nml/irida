package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

	@BeforeEach
	public void setUp() {
		this.galaxyHistoriesService = mock(GalaxyHistoriesService.class);
		this.toolsClient = mock(ToolsClient.class);
		this.jobsClient = mock(JobsClient.class);
		this.provenanceService = new AnalysisProvenanceServiceGalaxy(galaxyHistoriesService, toolsClient, jobsClient);
	}

	@Test
	public void testHistoriesFailure() throws ExecutionManagerException {
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenThrow(new ExecutionManagerException());
		assertThrows(ExecutionManagerException.class, () -> {
			provenanceService.buildToolExecutionForOutputFile(analysisSubmission(), analysisOutputFile());
		});
	}

	@Test
	public void testShowProvenanceFailureNoFiles() throws ExecutionManagerException {
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList());
		when(galaxyHistoriesService.showProvenance(any(String.class), any(String.class))).thenThrow(
				new ExecutionManagerException());
		assertThrows(ExecutionManagerException.class, () -> {
			provenanceService.buildToolExecutionForOutputFile(analysisSubmission(), analysisOutputFile());
		});
	}

	@Test
	public void testShowProvenanceFailureNoFiles2() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName("wrong name");
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc));
		assertThrows(ExecutionManagerException.class, () -> {
			provenanceService.buildToolExecutionForOutputFile(analysisSubmission(), analysisOutputFile());
		});
	}

	@Test
	public void testShowProvenanceFailureTooManyCooks() throws ExecutionManagerException {
		final HistoryContents hc1 = new HistoryContents();
		hc1.setName(FILENAME);
		final HistoryContents hc2 = new HistoryContents();
		hc2.setName(FILENAME);
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc1, hc2));
		assertThrows(ExecutionManagerException.class, () -> {
			provenanceService.buildToolExecutionForOutputFile(analysisSubmission(), analysisOutputFile());
		});
	}

	@Test
	public void testCantFindTools() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc));
		when(galaxyHistoriesService.showProvenance(any(String.class), any(String.class))).thenReturn(
				new HistoryContentsProvenance());
		when(toolsClient.showTool(any(String.class))).thenThrow(new RuntimeException());
		assertThrows(ExecutionManagerException.class, () -> {
			provenanceService.buildToolExecutionForOutputFile(analysisSubmission(), analysisOutputFile());
		});
	}

	@Test
	public void testBuildSingleStepToolExecutionOneParameter() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		hc.setId("historyid");
		final HistoryContentsProvenance hcp = new HistoryContentsProvenance();
		hcp.setParameters(ImmutableMap.of("akey", (Object) "avalue"));
		hcp.setToolId("toolid");
		hcp.setJobId("jobid");
		final JobDetails jd = new JobDetails();
		jd.setCommandLine("");
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc));
		when(galaxyHistoriesService.showProvenance(any(String.class), any(String.class))).thenReturn(hcp);
		when(toolsClient.showTool(any(String.class))).thenReturn(new Tool());
		when(jobsClient.showJob(any(String.class))).thenReturn(jd);
		final ToolExecution toolExecution = provenanceService.buildToolExecutionForOutputFile(analysisSubmission(),
				analysisOutputFile());
		assertTrue(toolExecution.getExecutionTimeParameters().containsKey("akey"),
				"tool execution should have the specified parameter.");
		assertEquals("avalue", toolExecution.getExecutionTimeParameters().get("akey"),
				"tool execution parameter should be specified value.");
		assertTrue(toolExecution.isInputTool(), "Tool execution should be considered input step, no predecessors.");
	}

	@Test
	public void testBuildSingleStepToolExecutionComplexParameters() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		hc.setId("historyid");
		final HistoryContentsProvenance hcp = new HistoryContentsProvenance();
		hcp.setToolId("toolid");
		hcp.setJobId("jobid");
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
		assertTrue(params.containsKey("akey"), "tool execution should have the specified parameter.");
		assertEquals("avalue", params.get("akey"), "tool execution parameter should be specified value.");
		assertTrue(params.containsKey("anotherKey.key"), "tool execution should have the specified parameter.");
		assertEquals("value", params.get("anotherKey.key"), "tool execution parameter should be specified value.");
		assertTrue(params.containsKey("thirdKey.key.key"), "tool execution should have the specified parameter.");
		assertEquals("value", params.get("thirdKey.key.key"), "tool execution parameter should be specified value.");
		assertTrue(params.containsKey("fourthKey.key"), "tool execution should have the specified parameter.");
		assertEquals("value", params.get("fourthKey.key"), "tool execution parameter should be specified value.");
		assertTrue(params.containsKey("fifthKey.key"), "tool execution should have the specified parameter.");
		assertEquals(AnalysisProvenanceServiceGalaxy.emptyValuePlaceholder(), params.get("fifthKey.key"),
				"tool execution parameter should be specified value.");
		assertTrue(params.containsKey("abadkey"), "tool execution should have the specified parameter.");
		assertEquals("[{\"key\":\"value\"]", params.get("abadkey"),
				"tool execution parameter should be specified value.");
		assertTrue(params.containsKey("listKey.key"), "tool execution should have the specified parameter.");
		assertEquals("value", params.get("listKey.key"), "tool execution parameter should be specified value.");
		assertTrue(params.containsKey("k"), "tool execution should have the specified parameter.");
		assertEquals("value-lower", params.get("k"), "tool execution parameter should be specified value.");
		assertTrue(params.containsKey("K"), "tool execution should have the specified parameter.");
		assertEquals("value-upper", params.get("K"), "tool execution parameter should be specified value.");
		assertTrue(params.containsKey("\\\\keyWithBackslash"), "tool execution should have the specified parameter.");
		assertEquals("value-backslash-lower", params.get("\\\\keyWithBackslash"),
				"tool execution parameter should be specified value.");
		assertTrue(params.containsKey("\\\\KeyWithBackslash"), "tool execution should have the specified parameter.");
		assertEquals("value-backslash-upper", params.get("\\\\KeyWithBackslash"),
				"tool execution parameter should be specified value.");
		assertTrue(params.containsKey("MULTIPLE_UPPER_case"), "tool execution should have the specified parameter.");
		assertEquals("upper-case-values!", params.get("MULTIPLE_UPPER_case"),
				"tool execution parameter should be specified value.");

		assertTrue(toolExecution.isInputTool(), "Tool execution should be considered input step, no predecessors.");
	}

	@Test
	public void testBuildSingleStepToolExecutionOneParameterOnePredecessor() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		hc.setId("historyContentsId");
		final HistoryContentsProvenance hcpWithPredecessor = new HistoryContentsProvenance();
		hcpWithPredecessor.setParameters(ImmutableMap.of("akey", (Object) ImmutableMap.of("id", "previousKey")));
		hcpWithPredecessor.setToolId("toolid");
		hcpWithPredecessor.setJobId("jobid");
		final HistoryContentsProvenance hcpWithoutPredecessor = new HistoryContentsProvenance();
		hcpWithoutPredecessor.setToolId("toolid");
		hcpWithoutPredecessor.setJobId("jobid");
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
		assertFalse(toolExecution.getExecutionTimeParameters().containsKey("akey"),
				"tool execution should not have an ID parameter.");
		assertFalse(toolExecution.getExecutionTimeParameters().containsKey("akey.id"),
				"tool execution should not have an ID parameter.");
		assertFalse(toolExecution.isInputTool(), "Tool execution has one predecessor, not input step.");
		final ToolExecution predecessor = toolExecution.getPreviousSteps().iterator().next();
		assertTrue(predecessor.isInputTool(), "predecessor step is input step.");
	}
	
	@Test
	public void testBuildSingleStepToolExecutionListParameters() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		hc.setId("historyid");
		final HistoryContentsProvenance hcp = new HistoryContentsProvenance();
		hcp.setToolId("toolid");
		hcp.setJobId("jobid");
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
		assertTrue(toolExecution.getExecutionTimeParameters().containsKey("akey"),
				"tool execution should have the specified parameter.");
		assertEquals("[avalue]", toolExecution.getExecutionTimeParameters().get("akey"),
				"tool execution parameter should be specified value.");
		assertTrue(toolExecution.getExecutionTimeParameters().containsKey("akey2"),
				"tool execution should have the specified parameter.");
		assertEquals("[avalue2]", toolExecution.getExecutionTimeParameters().get("akey2"),
				"tool execution parameter should be specified value.");
		assertEquals("[]", toolExecution.getExecutionTimeParameters().get("akey3"),
				"tool execution parameter should be specified value.");
		assertEquals("[]", toolExecution.getExecutionTimeParameters().get("akey4"),
				"tool execution parameter should be specified value.");
		assertEquals("[avalue5.1, avalue5.2]", toolExecution.getExecutionTimeParameters().get("akey5"),
				"tool execution parameter should be specified value.");
		assertEquals("[avalue6.1, avalue6.2]", toolExecution.getExecutionTimeParameters().get("akey6"),
				"tool execution parameter should be specified value.");
		assertTrue(toolExecution.isInputTool(), "Tool execution should be considered input step, no predecessors.");
	}
	
	@Test
	public void testBuildSingleStepToolExecutionStrangeDataStructureDoToString() throws ExecutionManagerException {
		final HistoryContents hc = new HistoryContents();
		hc.setName(FILENAME);
		hc.setId("historyid");
		final HistoryContentsProvenance hcp = new HistoryContentsProvenance();
		hcp.setToolId("toolid");
		hcp.setJobId("jobid");
		hcp.setParameters(ImmutableMap.of("akey", "[[\"avalue\"]]"));
		final JobDetails jd = new JobDetails();
		jd.setCommandLine("");
		when(galaxyHistoriesService.showHistoryContents(any(String.class))).thenReturn(Lists.newArrayList(hc));
		when(galaxyHistoriesService.showProvenance(any(String.class), any(String.class))).thenReturn(hcp);
		when(toolsClient.showTool(any(String.class))).thenReturn(new Tool());
		when(jobsClient.showJob(any(String.class))).thenReturn(jd);
		final ToolExecution toolExecution = provenanceService.buildToolExecutionForOutputFile(analysisSubmission(),
				analysisOutputFile());
		assertTrue(toolExecution.getExecutionTimeParameters().containsKey("akey"),
				"tool execution should have the specified parameter.");
		assertEquals("[[\"avalue\"]]", toolExecution.getExecutionTimeParameters().get("akey"),
				"tool execution parameter should be specified value.");
	}

	private String analysisSubmission() {
		return UUID.randomUUID().toString();
	}

	private String analysisOutputFile() {
		return new AnalysisOutputFile(Paths.get("/" + FILENAME), "", "", null).getFile().getFileName().toString();
	}
}
