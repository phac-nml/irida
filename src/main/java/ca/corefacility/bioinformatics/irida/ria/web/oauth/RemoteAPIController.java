package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.ria.utilities.ExceptionPropertyAndMessage;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Map;

/**
 * Controller handling basic operations for listing, viewing, adding, and removing {@link RemoteAPI}s
 */
@Controller
@RequestMapping("/remote_api")
public class RemoteAPIController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(RemoteAPIController.class);

    public static final String CLIENTS_PAGE = "remote_apis/list";
    public static final String PARENT_FRAME_RELOAD_PAGE = "remote_apis/parent_reload";

    private final RemoteAPIService remoteAPIService;
    private final ProjectRemoteService projectRemoteService;
    private final OltuAuthorizationController authController;

    // Map storing the message names for the
    // getErrorsFromDataIntegrityViolationException method
    private final Map<String, ExceptionPropertyAndMessage> errorMessages = ImmutableMap.of(
            RemoteAPI.SERVICE_URI_CONSTRAINT_NAME,
            new ExceptionPropertyAndMessage("serviceURI", "remoteapi.create.serviceURIConflict"));

    @Autowired
    public RemoteAPIController(RemoteAPIService remoteAPIService, ProjectRemoteService projectRemoteService,
            OltuAuthorizationController authController) {
        this.remoteAPIService = remoteAPIService;
        this.projectRemoteService = projectRemoteService;
        this.authController = authController;
    }

    /**
     * Get the remote apis listing page
     *
     * @return The view name of the remote apis listing page
     */
    @RequestMapping
    public String list() {
        return CLIENTS_PAGE;
    }

    /**
     * Initiate a token request on a remote api if one does not yet exist. Works with
     * {@link #handleOAuthException(HttpServletRequest, IridaOAuthException)} to initiate the request.
     *
     * @param apiId the ID of the api to connect to
     * @param model the model to add attributes to.
     * @return The name of the PARENT_FRAME_RELOAD_PAGE view
     */
    @RequestMapping("/connect/{apiId}")
    public String connectToAPI(@PathVariable Long apiId, Model model) {
        RemoteAPI api = remoteAPIService.read(apiId);
        projectRemoteService.getServiceStatus(api);
        model.addAttribute("remoteApi", api);

        return PARENT_FRAME_RELOAD_PAGE;
    }

    /**
     * Handle an {@link IridaOAuthException} by launching an authentication flow
     *
     * @param request The incoming request method
     * @param ex      The thrown exception
     * @return A redirect to the {@link OltuAuthorizationController}'s authentication
     * @throws OAuthSystemException    if the request cannot be authenticated.
     * @throws JsonProcessingException if the request cannot be created.
     */
    @ExceptionHandler(IridaOAuthException.class)
    public String handleOAuthException(HttpServletRequest request, IridaOAuthException ex)
            throws JsonProcessingException, OAuthSystemException {
        logger.debug("Caught IridaOAuthException.  Beginning OAuth2 authentication token flow.");
        String requestURI = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        return authController.authenticate(ex.getRemoteAPI(), requestURI);
    }

}
