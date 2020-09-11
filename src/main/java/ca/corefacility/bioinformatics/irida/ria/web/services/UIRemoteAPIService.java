package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.RemoteAPIModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SelectOption;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

/**
 * UI Service to handle request for Remote APIs
 */
@Component
public class UIRemoteAPIService {
    private final RemoteAPIService remoteAPIService;
    private final RemoteAPITokenService tokenService;
    private final ProjectRemoteService projectRemoteService;

    @Autowired
    public UIRemoteAPIService(RemoteAPIService remoteAPIService, RemoteAPITokenService tokenService,
            ProjectRemoteService projectRemoteService) {
        this.remoteAPIService = remoteAPIService;
        this.tokenService = tokenService;
        this.projectRemoteService = projectRemoteService;
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

    public List<RemoteAPIModel> getListOfRemoteApis() {
        Iterable<RemoteAPI> remotes =  remoteAPIService.findAll();
        List<RemoteAPIModel> remoteAPIModels = new ArrayList<>();
        remotes.forEach(r -> remoteAPIModels.add(new RemoteAPIModel(r)));
        return remoteAPIModels;
    }

    public List<SelectOption> getProjectsForAPI(Long remoteId) {
        RemoteAPI api = remoteAPIService.read(remoteId);
        List<Project> projects = projectRemoteService.listProjectsForAPI(api);
        return projects.stream().map(project -> {
            SelectOption option = new SelectOption();
            option.setValue(project.getId());
            option.setText(project.getLabel());
            return option;
        }).collect(Collectors.toUnmodifiableList());
    }
}
