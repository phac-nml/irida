package ca.corefacility.bioinformatics.irida.model.galaxy;

import static com.google.common.base.Preconditions.*;

import java.io.File;
import java.util.List;

/**
 * Represents a Sample to be uploaded to Galaxy
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxySample
{
	private String sampleName;
	private List<File> sampleFiles;
	
	public GalaxySample(String sampleName, List<File> sampleFiles)
	{
		checkNotNull(sampleName, "sampleName is null");
		checkNotNull(sampleFiles, "sampleFiles is null");
		
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
