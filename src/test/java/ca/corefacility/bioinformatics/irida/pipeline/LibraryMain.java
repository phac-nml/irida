package ca.corefacility.bioinformatics.irida.pipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ca.corefacility.bioinformatics.irida.pipeline.workflow.Workflow;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.WorkflowSubmissionException;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.impl.WorkflowImpl;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.impl.WorkflowRESTAPIGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.impl.WorkflowSubmitterGalaxy;

public class LibraryMain
{
	public static void main(String[] args)
	{
		String usage = "Usage: " + LibraryMain.class.getName()
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
			String libraryName = "TestLibrary" + System.currentTimeMillis();
			
			File dataFile = new File(Main.class.getResource("testData.fastq").toURI());
			List<File> dataFiles = new ArrayList<File>();
			dataFiles.add(dataFile);
	
			WorkflowRESTAPIGalaxy workflowAPI = new WorkflowRESTAPIGalaxy(galaxyURL, apiKey);
			String libraryId = workflowAPI.buildGalaxyLibrary(libraryName);
			if (libraryId != null)
			{		
				System.out.println("Successfully created library '" + libraryName);
				
				if (workflowAPI.uploadFilesToLibrary(dataFiles, libraryId))
				{
					System.out.println("Successfully uploaded files:");
					for (File file : dataFiles)
					{
						System.out.println(file.getAbsolutePath());
					}
				}
				else
				{
					System.err.println("Error uploading files");
				}
			}
			else
			{
				System.err.println("Error creating library " + libraryName);
			}
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}
}
