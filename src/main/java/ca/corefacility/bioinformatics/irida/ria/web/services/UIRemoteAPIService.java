package ca.corefacility.bioinformatics.irida.ria.web.services;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
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

    public Date checkAPIStatus(long remoteId) {
        RemoteAPI api = remoteAPIService.read(remoteId);
        RemoteAPIToken token = tokenService.getToken(api);
        return token.getExpiryDate();
    }

    public RemoteAPIModel getRemoteApiDetails(long remoteId) {
        RemoteAPI remoteAPI = remoteAPIService.read(remoteId);
        return new RemoteAPIModel(remoteAPI);
    }

    public void deleteRemoteAPI(long remoteId) {
        remoteAPIService.delete(remoteId);
    }
}
