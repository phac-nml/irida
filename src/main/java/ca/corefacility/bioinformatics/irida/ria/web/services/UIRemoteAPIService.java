package ca.corefacility.bioinformatics.irida.ria.web.services;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.RemoteAPIModel;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * UI Service to handle request for Remote APIs
 */
@Component
public class UIRemoteAPIService {
    private final RemoteAPIService remoteAPIService;
    private final RemoteAPITokenService tokenService;

    @Autowired
    public UIRemoteAPIService(RemoteAPIService remoteAPIService, RemoteAPITokenService tokenService) {
        this.remoteAPIService = remoteAPIService;
        this.tokenService = tokenService;
    }

    /**
     * Check the status of a {@link RemoteAPI}
     *
     * @param remoteId identifier for a {@link RemoteAPI}
     * @return expiration {@link Date} of the REmote API Token
     */
    public Date checkAPIStatus(long remoteId) {
        RemoteAPI api = remoteAPIService.read(remoteId);
        RemoteAPIToken token = tokenService.getToken(api);
        return token.getExpiryDate();
    }

    /**
     * Get details about a specific {@link RemoteAPI}
     *
     * @param remoteId identifier for the {@link RemoteAPI}
     * @return {@link RemoteAPIModel} containing the details of the {@link RemoteAPI}
     */
    public RemoteAPIModel getRemoteApiDetails(long remoteId) {
        RemoteAPI remoteAPI = remoteAPIService.read(remoteId);
        return new RemoteAPIModel(remoteAPI);
    }

    /**
     * Delete a specific {@link RemoteAPI}
     *
     * @param remoteId Identifier for the {@link RemoteAPI} to delete
     */
    public void deleteRemoteAPI(long remoteId) {
        remoteAPIService.delete(remoteId);
    }
}
