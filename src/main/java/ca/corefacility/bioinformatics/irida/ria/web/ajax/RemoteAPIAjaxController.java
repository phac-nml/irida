package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.RemoteAPISpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.ExceptionPropertyAndMessage;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxCreateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxFormErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote.CreateRemoteProjectRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote.RemoteAPIModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote.RemoteProjectModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.rempoteapi.dto.RemoteAPITableAdminModel;
import ca.corefacility.bioinformatics.irida.ria.web.rempoteapi.dto.RemoteAPITableModel;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIRemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for asynchronous requests for remote api functionality.
 */
@RestController
@RequestMapping("/ajax/remote_api")
public class RemoteAPIAjaxController extends BaseController {
    private final RemoteAPIService remoteAPIService;
    private final UIRemoteAPIService service;
    private final MessageSource messageSource;
    private final UserService userService;

    // Map storing the message names for the
    // getErrorsFromDataIntegrityViolationException method
    private final Map<String, ExceptionPropertyAndMessage> errorMessages = ImmutableMap.of(
            RemoteAPI.SERVICE_URI_CONSTRAINT_NAME,
            new ExceptionPropertyAndMessage("serviceURI", "remoteapi.create.serviceURIConflict"));

    @Autowired
    public RemoteAPIAjaxController(RemoteAPIService remoteAPIService, UIRemoteAPIService service,
            MessageSource messageSource, UserService userService) {
        this.remoteAPIService = remoteAPIService;
        this.service = service;
        this.messageSource = messageSource;
        this.userService = userService;
    }

    /**
     * Get a list of the current page for the Remote API Table
     *
     * @param tableRequest - the details for the current page of the Table
     * @return {@link TableResponse}
     */
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public TableResponse<RemoteAPITableModel> getAjaxAPIList(@RequestBody TableRequest tableRequest) {
        Page<RemoteAPI> search = remoteAPIService.search(
                RemoteAPISpecification.searchRemoteAPI(tableRequest.getSearch()), tableRequest.getCurrent(),
                tableRequest.getPageSize(), tableRequest.getSortDirection(), tableRequest.getSortColumn());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        boolean isAdmin = user.getSystemRole().equals(Role.ROLE_ADMIN);

        List<RemoteAPITableModel> apiData = search.getContent()
                .stream()
                .map(api -> isAdmin ? new RemoteAPITableAdminModel(api) : new RemoteAPITableModel(api))
                .collect(Collectors.toList());
        return new TableResponse<>(apiData, search.getTotalElements());
    }

    /**
     * Check the currently logged in user's OAuth2 connection status to a given API
     *
     * @param apiId The ID of the api
     * @return "valid" or "invalid_token" message
     */
    @RequestMapping("/status/{apiId}")
    public ResponseEntity<Date> checkAPIStatus(@PathVariable Long apiId) {
        try {
            return ResponseEntity.ok(service.checkAPIStatus(apiId));
        } catch (IridaOAuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AjaxErrorResponse(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AjaxErrorResponse(e.getMessage()));
        }
    }

    /**
     * Create a new client
     *
     * @param client The client to add
     * @param locale Locale of the current user session
     * @return result of creating the remote api
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/create")
    public ResponseEntity<AjaxResponse> postCreateRemoteAPI(RemoteAPI client, Locale locale) {
        Map<String, String> errors;
        try {
            RemoteAPI remoteAPI = remoteAPIService.create(client);
            return ResponseEntity.ok(new AjaxCreateItemSuccessResponse(remoteAPI.getId()));
        } catch (ConstraintViolationException e) {
            errors = getErrorsFromViolationException(e);
        } catch (DataIntegrityViolationException e) {
            errors = getErrorsFromDataIntegrityViolationException(e, errorMessages, messageSource, locale);
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new AjaxFormErrorResponse(errors));
    }
}
