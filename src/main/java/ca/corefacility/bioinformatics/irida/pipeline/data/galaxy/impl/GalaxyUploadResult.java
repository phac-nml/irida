package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl;

import static com.google.common.base.Preconditions.*;

import java.net.MalformedURLException;
import java.net.URL;

import com.github.jmchilton.blend4j.galaxy.beans.Library;

public class GalaxyUploadResult
{
	private String libraryId;
	private String libraryName;
	private URL libraryAPIURL;
	private URL sharedDataURL;

	public GalaxyUploadResult(Library library, String galaxyURL) throws MalformedURLException
	{
		checkNotNull(library, "library is null");
		checkNotNull(galaxyURL, "galaxyURL is null");
		
		this.libraryId = library.getId();
		this.libraryName = library.getName();
		
		this.libraryAPIURL = libraryToAPIURL(library, galaxyURL);
		this.sharedDataURL = galaxyURLToLibraryURL(galaxyURL);
	}
	
	private URL libraryToAPIURL(Library library, String galaxyURL) throws MalformedURLException
	{
		String urlPath = library.getUrl();
		String domainPath = galaxyURL;
		
		if (domainPath.endsWith("/"))
		{
			domainPath = domainPath.substring(0, domainPath.length() -1);
		}
		
		if (urlPath.startsWith("/"))
		{
			urlPath = urlPath.substring(1);
		}
		
		return new URL(domainPath + "/" + urlPath);
	}
	
	private URL galaxyURLToLibraryURL(String galaxyURL) throws MalformedURLException
	{
		String domainPath = galaxyURL;
		
		if (domainPath.endsWith("/"))
		{
			domainPath = domainPath.substring(0, domainPath.length() -1);
		}
		
		return new URL(domainPath + "/library");
	}
	
	public URL getLibraryAPIURL()
	{
		return libraryAPIURL;
	}
	
	public URL getDataLocation()
	{
		return sharedDataURL;
	}
	
	public String getLibraryId()
	{
		return libraryId;
	}
	
	public String getLibraryName()
	{
		return libraryName;
	}

	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result
	            + ((libraryAPIURL == null) ? 0 : libraryAPIURL.hashCode());
	    result = prime * result
	            + ((libraryId == null) ? 0 : libraryId.hashCode());
	    result = prime * result
	            + ((libraryName == null) ? 0 : libraryName.hashCode());
	    result = prime * result
	            + ((sharedDataURL == null) ? 0 : sharedDataURL.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj)
    {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    GalaxyUploadResult other = (GalaxyUploadResult) obj;
	    if (libraryAPIURL == null)
	    {
		    if (other.libraryAPIURL != null)
			    return false;
	    } else if (!libraryAPIURL.equals(other.libraryAPIURL))
		    return false;
	    if (libraryId == null)
	    {
		    if (other.libraryId != null)
			    return false;
	    } else if (!libraryId.equals(other.libraryId))
		    return false;
	    if (libraryName == null)
	    {
		    if (other.libraryName != null)
			    return false;
	    } else if (!libraryName.equals(other.libraryName))
		    return false;
	    if (sharedDataURL == null)
	    {
		    if (other.sharedDataURL != null)
			    return false;
	    } else if (!sharedDataURL.equals(other.sharedDataURL))
		    return false;
	    return true;
    }
}
