package ca.corefacility.bioinformatics.irida.pipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import ca.corefacility.bioinformatics.irida.pipeline.workflow.Workflow;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.impl.WorkflowImpl;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.impl.WorkflowSubmitterGalaxy;

public class Main
{
	public static void main(String[] args)
	{
		String usage = "Usage: " + Main.class.getName()
				+ " [galaxy url] [galaxy api key]";

		if (args.length != 2)
		{
			System.err.println(usage);
			System.exit(1);
		}

		String galaxyURL = args[0];
		String apiKey = args[1];
		
		try
		{
			String workflowJson = readWorkflow(Main.class.getResource("GalaxyWorkflow.ga"));
			Workflow workflow = new WorkflowImpl(workflowJson);
	
			WorkflowSubmitterGalaxy workflowSubmitter = new WorkflowSubmitterGalaxy(galaxyURL, apiKey);
			if (workflowSubmitter.submitWorkflow(workflow))
			{		
				System.out.println("Successfully submitted workflow");
			}
			else
			{
				System.err.println("Error submitting workflow");
			}
		}
		catch (FileNotFoundException | URISyntaxException e)
		{
			e.printStackTrace();
		}
	}
	
	private static String readWorkflow(URL workflowURL) throws FileNotFoundException, URISyntaxException
	{
		File workflowFile = new File(workflowURL.toURI());
		Scanner workflowScanner = new Scanner(workflowFile);
		String workflow = workflowScanner.useDelimiter("\\Z").next();
		workflowScanner.close();
		
		return workflow;
	}
}
