package ca.corefacility.bioinformatics.irida.pipeline;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.impl.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.workflow.impl.WorkflowRESTAPIGalaxy;

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
			
			GalaxySample sample = new GalaxySample("TestSample", dataFiles);
			List<GalaxySample> samples = new ArrayList<GalaxySample>();
			samples.add(sample);
	
			WorkflowRESTAPIGalaxy workflowAPI = new WorkflowRESTAPIGalaxy(galaxyURL, apiKey);
			String libraryId = workflowAPI.buildGalaxyLibrary(libraryName);
			if (libraryId != null)
			{		
				System.out.println("Successfully created library '" + libraryName);
				
				if (workflowAPI.uploadFilesToLibrary(samples, libraryId))
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
