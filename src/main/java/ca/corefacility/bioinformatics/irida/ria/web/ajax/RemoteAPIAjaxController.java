package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.specification.RemoteAPISpecification;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote.CreateRemoteProjectRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote.RemoteAPIModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote.RemoteProjectModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.rempoteapi.dto.RemoteAPITableModel;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIRemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;

/**
 * Controller for asynchronous requests for remote api functionality.
 */
@RestController
@RequestMapping("/ajax/remote_api")
public class RemoteAPIAjaxController {
    private final RemoteAPIService remoteAPIService;
    private final UIRemoteAPIService service;


    @Autowired
    public RemoteAPIAjaxController(RemoteAPIService remoteAPIService, UIRemoteAPIService service) {
        this.remoteAPIService = remoteAPIService;
        this.service = service;
    }

    /**
     * Get a list of the current page for the Remote API Table
     *
     * @param tableRequest - the details for the current page of the Table
     * @return {@link TableResponse}
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public TableResponse<RemoteAPITableModel> getAjaxAPIList(@RequestBody TableRequest tableRequest) {
        Page<RemoteAPI> search = remoteAPIService.search(
                RemoteAPISpecification.searchRemoteAPI(tableRequest.getSearch()), tableRequest.getCurrent(),
                tableRequest.getPageSize(), tableRequest.getSortDirection(), tableRequest.getSortColumn());

        List<RemoteAPITableModel> apiData = search.getContent()
                .stream()
                .map(RemoteAPITableModel::new)
                .collect(Collectors.toList());
        return new TableResponse<>(apiData, search.getTotalElements());
    }

    /**
     * Check the currently logged in user's OAuth2 connection status to a given
     * API
     *
     * @param apiId The ID of the api
     * @return "valid" or "invalid_token" message
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("/status/{apiId}")
    public ResponseEntity<Date> checkAPIStatus(@PathVariable Long apiId) {
        try {
            return ResponseEntity.ok(service.checkAPIStatus(apiId));
        } catch (IridaOAuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Get details about a Remote API Connection
     *
     * @param remoteId Identifier for a Remote API
     * @return {@link ResponseEntity} with the details of the Remote API
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{remoteId}")
    public ResponseEntity<RemoteAPIModel> getRemoteAPIDetails(@PathVariable long remoteId) {
        return ResponseEntity.ok(service.getRemoteApiDetails(remoteId));
    }

    /**
     * Delete a Remote API Connection
     *
     * @param remoteId Identifier for a Remote API.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{remoteId}/delete")
    public void deleteRemoteAPI(@PathVariable long remoteId) {
        service.deleteRemoteAPI(remoteId);
    }

    /**
     * Get a list of all available remote API f
     *
     * @return List of {@link RemoteAPIModel}
     */
    @GetMapping("/apis")
    public ResponseEntity<List<RemoteAPIModel>> getListOfRemoteApis() {
        return ResponseEntity.ok(service.getListOfRemoteApis());
    }

    /**
     * Get a list of project available at a remote API
     *
     * @param remoteId identifier for a remote API
     * @return list of project
     */
    @GetMapping("/{remoteId}/projects")
    public ResponseEntity<List<RemoteProjectModel>> getProjectsForAPI(@PathVariable long remoteId) {
        return ResponseEntity.ok(service.getProjectsForAPI(remoteId));
    }

    /**
     * Create a new synchronized remote project
     *
     * @param request details about the remote project
     * @return status of created the new remote project
     */
    @PostMapping("/project")
    public ResponseEntity<AjaxResponse> createSynchronizedProject(@RequestBody CreateRemoteProjectRequest request) {
        try {
            return ResponseEntity.ok(service.createSynchronizedProject(request));
        } catch (IridaOAuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AjaxErrorResponse(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AjaxErrorResponse(e.getMessage()));
        }
    }
}
