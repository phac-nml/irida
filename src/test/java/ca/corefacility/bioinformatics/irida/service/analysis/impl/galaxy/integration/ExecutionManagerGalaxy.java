package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration;

import java.net.URL;

import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;
import ca.corefacility.bioinformatics.irida.service.analysis.ExecutionManager;

public class ExecutionManagerGalaxy implements ExecutionManager {
	
	private Long id;
	private URL location;
	private UploaderAccountName adminEmail;
	private String adminAPIKey;
	private DataStorage dataStorage;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public URL getLocation() {
		return location;
	}
	public void setLocation(URL location) {
		this.location = location;
	}
	public UploaderAccountName getAdminEmail() {
		return adminEmail;
	}
	public void setAdminEmail(UploaderAccountName adminEmail) {
		this.adminEmail = adminEmail;
	}
	public String getAdminAPIKey() {
		return adminAPIKey;
	}
	public void setAdminAPIKey(String adminAPIKey) {
		this.adminAPIKey = adminAPIKey;
	}
	public DataStorage getDataStorage() {
		return dataStorage;
	}
	public void setDataStorage(DataStorage dataStorage) {
		this.dataStorage = dataStorage;
	}
}
