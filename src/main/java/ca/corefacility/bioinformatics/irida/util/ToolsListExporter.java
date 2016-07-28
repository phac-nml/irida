package ca.corefacility.bioinformatics.irida.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.IridaWorkflowsConfig;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowToolRepository;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Exports a YAML configuration file containing the list of all tools necessary
 * in Galaxy to run this IRIDA instance. The path to the YAML config file is
 * given as a command-line argument.
 * 
 * @author Aaron Petkau
 *
 */
public class ToolsListExporter {

	private static final Logger logger = LoggerFactory.getLogger(ToolsListExporter.class);

	private static String usage = "Usage: " + ToolsListExporter.class.getName() + " [output-file]";

	private static final String toolsSectionName = "tools-section";
	private static final Pattern regexPattern = Pattern.compile("- " + toolsSectionName + ": \"([^\"]+)\"");

	private static Map<AnalysisType, IridaWorkflow> getDefaultWorkflows() throws IridaWorkflowNotFoundException {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

		context.getEnvironment().setActiveProfiles("dev");
		context.register(new Class[] { IridaApiPropertyPlaceholderConfig.class, IridaWorkflowsConfig.class });
		context.refresh();

		IridaWorkflowsService iridaWorkflowsService = context.getBean(IridaWorkflowsService.class);
		Map<AnalysisType, IridaWorkflow> workflows = iridaWorkflowsService
				.getAllDefaultWorkflowsByType(Sets.newHashSet(AnalysisType.valuesMinusDefault()));

		context.close();

		return workflows;
	}

	/**
	 * Replaces all instances of pattern with capture group 1, with the given
	 * prefix and postfix.
	 * 
	 * @param originalString
	 *            The original String to search for replacement patterns.
	 * @param pattern
	 *            The regex pattern, assumes that this has one and only one
	 *            capture group.
	 * @param prefix
	 *            The prefix before the capture group to print.
	 * @param postfix
	 *            The postfix after the capture group to print.
	 * @return The replaced String.
	 */
	private static String replaceAllWithCaptureGroup1(String originalString, Pattern pattern, String prefix,
			String postfix) {
		Matcher matcher = pattern.matcher(originalString);

		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			String capture = matcher.group(1);
			matcher.appendReplacement(stringBuffer, prefix + capture + postfix);
		}
		matcher.appendTail(stringBuffer);

		return stringBuffer.toString();
	}

	public static void main(String[] args)
			throws IridaWorkflowNotFoundException, JsonGenerationException, JsonMappingException, IOException {

		if (args.length != 1) {
			throw new RuntimeException("Error: invalid number of arguments \n" + usage);
		}

		Path toolsListOutput = Paths.get(args[0]);
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		Map<AnalysisType, IridaWorkflow> workflows = getDefaultWorkflows();

		Map<String, Object> yamlMap = Maps.newHashMap();
		yamlMap.put("api_key", "<Galaxy Admin user API key>");
		yamlMap.put("galaxy_instance", "<IP address for target Galaxy instance>");

		List<Map<String, String>> tools = Lists.newArrayList();
		yamlMap.put("tools", tools);

		for (IridaWorkflow workflow : workflows.values()) {
			logger.info("Adding tools for workflow " + workflow);

			String workflowName = workflow.getWorkflowDescription().getName();
			String workflowVersion = workflow.getWorkflowDescription().getVersion();

			tools.add(ImmutableMap.of(toolsSectionName,
					"### Tools for " + workflowName + " v" + workflowVersion + " ###"));

			String toolPanelSectionId = workflowName.toLowerCase();

			for (IridaWorkflowToolRepository toolRepository : workflow.getWorkflowDescription().getToolRepositories()) {
				tools.add(ImmutableMap.<String, String>builder()
						.put("name", toolRepository.getName())
						.put("owner", toolRepository.getOwner())
						.put("tool_shed_url", toolRepository.getUrl().toString())
						.put("revision", toolRepository.getRevision())
						.put("tool_panel_section_id", toolPanelSectionId)
						.build());
			}
		}

		PrintWriter outputFileWriter = new PrintWriter(toolsListOutput.toFile());

		// Bit of a hack, but does the job. Wanted to add a comment in the YAML
		// file for each tool section for readability but no YAML library
		// supports this. Instead, I do some pattern substitute/replace on a
		// special String "toolsSectionName" to generate the comments.
		String yaml = replaceAllWithCaptureGroup1(mapper.writeValueAsString(yamlMap), regexPattern, "\n  ", "\n");
		outputFileWriter.print(yaml);
		outputFileWriter.close();
		logger.info("Wrote tools list to: " + toolsListOutput.toAbsolutePath());
	}
}
