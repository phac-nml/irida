package ca.corefacility.bioinformatics.irida.service.analysis;

import java.net.URL;

import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;

/**
 * Defines connection information to an execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface ExecutionManager {

	public Long getId();
	public void setId(Long id);
	
	public URL getLocation();
	public void setLocation(URL location);
	
	public UploaderAccountName getAdminEmail();
	public void setAdminEmail(UploaderAccountName email);
	
	public String getAdminAPIKey();
	public void setAdminAPIKey(String apiKey);
	
	public DataStorage getDataStorage();
	public void setDataStorage(DataStorage dataStorage);
}
