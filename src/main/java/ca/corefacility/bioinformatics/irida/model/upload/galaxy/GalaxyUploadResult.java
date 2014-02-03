package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import java.net.MalformedURLException;
import java.net.URL;

import ca.corefacility.bioinformatics.irida.model.upload.UploadObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;

import com.github.jmchilton.blend4j.galaxy.beans.Library;

public class GalaxyUploadResult implements UploadResult
{
	private String libraryId;
	private GalaxyObjectName libraryName;
	private GalaxyAccountEmail ownerName;
	private URL libraryAPIURL;
	private URL sharedDataURL;

	public GalaxyUploadResult(Library library,
			GalaxyObjectName libraryName,
			GalaxyAccountEmail ownerName,
			String galaxyURL) throws MalformedURLException
	{
		checkNotNull(library, "library is null");
		checkNotNull(libraryName, "libraryName is null");
		checkNotNull(ownerName, "ownerName is null");
		checkNotNull(galaxyURL, "galaxyURL is null");
		
		String actualLibraryName = library.getName();
		
		if (!libraryName.getName().equals(actualLibraryName))
		{
			throw new RuntimeException("Library names are out of sync");
		}
		
		this.libraryId = library.getId();
		this.libraryName = libraryName;
		this.ownerName = ownerName;
		
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
	
	/* (non-Javadoc)
	 * @see ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.impl.UploadResult#getDataLocation()
	 */
	@Override
    public URL getDataLocation()
	{
		return sharedDataURL;
	}
	
	public String getLibraryId()
	{
		return libraryId;
	}
	
	@Override
    public UploadObjectName getLocationName()
	{
		return libraryName;
	}
	
	@Override
    public UploaderAccountName getOwner()
    {
	    return ownerName;
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
	            + ((ownerName == null) ? 0 : ownerName.hashCode());
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
	    if (ownerName == null)
	    {
		    if (other.ownerName != null)
			    return false;
	    } else if (!ownerName.equals(other.ownerName))
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
