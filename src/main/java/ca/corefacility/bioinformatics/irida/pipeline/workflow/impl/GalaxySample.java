package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl;

import java.io.File;
import java.util.List;

/**
 * Represents a Sample to be uploaded to Galaxy
 * @author aaron
 *
 */
public class GalaxySample
{
	private String sampleName;
	private List<File> sampleFiles;
	
	public GalaxySample(String sampleName, List<File> sampleFiles)
	{
		this.sampleName = sampleName;
		this.sampleFiles = sampleFiles;
	}
	
	public String getSampleName()
	{
		return sampleName;
	}
	public void setSampleName(String sampleName)
	{
		this.sampleName = sampleName;
	}
	public List<File> getSampleFiles()
	{
		return sampleFiles;
	}
	public void setSampleFiles(List<File> sampleFiles)
	{
		this.sampleFiles = sampleFiles;
	}
}
