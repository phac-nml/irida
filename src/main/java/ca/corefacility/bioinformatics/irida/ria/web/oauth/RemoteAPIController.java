package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.utilities.ExceptionPropertyAndMessage;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;

/**
 * Controller handling basic operations for listing, viewing, adding, and
 * removing {@link RemoteAPI}s
 */
@Controller
@RequestMapping("/remote_api")
public class RemoteAPIController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(RemoteAPIController.class);

    public static final String CLIENTS_PAGE = "remote_apis/list";
    public static final String DETAILS_PAGE = "remote_apis/remote_api_details";
    public static final String ADD_API_PAGE = "remote_apis/create";
    public static final String PARENT_FRAME_RELOAD_PAGE = "remote_apis/parent_reload";

    private final RemoteAPIService remoteAPIService;
    private final ProjectRemoteService projectRemoteService;
    private final UserService userService;
    private final OltuAuthorizationController authController;
    private final MessageSource messageSource;

    // Map storing the message names for the
    // getErrorsFromDataIntegrityViolationException method
    private final Map<String, ExceptionPropertyAndMessage> errorMessages = ImmutableMap.of(
            RemoteAPI.SERVICE_URI_CONSTRAINT_NAME, new ExceptionPropertyAndMessage("serviceURI",
                    "remoteapi.create.serviceURIConflict"));

    @Autowired
    public RemoteAPIController(RemoteAPIService remoteAPIService, ProjectRemoteService projectRemoteService, UserService userService, OltuAuthorizationController authController, MessageSource messageSource) {
        this.remoteAPIService = remoteAPIService;
        this.projectRemoteService = projectRemoteService;
        this.userService = userService;
        this.authController = authController;
        this.messageSource = messageSource;
    }

    /**
     * Get the remote apis listing page
     *
     * @param model     {@link Model}
     * @param principal {@link Principal} currently logged in user
     * @return The view name of the remote apis listing page
     */
    @RequestMapping
    public String list(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        model.addAttribute("isAdmin", user.getSystemRole()
                .equals(Role.ROLE_ADMIN));
        return CLIENTS_PAGE;
    }

    /**
     * Get the HTML modal for connecting to a remote API
     *
     * @param apiId Identifier for the remote API to connect to.
     * @return {@link String} Path to the modal template.
     */
    @RequestMapping("/modal/{apiId}")
    public String getApiConnectModal(@PathVariable Long apiId) {
        return "remote_apis/fragments.html :: #connect-modal";
    }

    /**
     * Initiate a token request on a remote api if one does not yet exist. Works
     * with
     * {@link #handleOAuthException(HttpServletRequest, IridaOAuthException)} to
     * initiate the request.
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
     * @return A redirect to the {@link OltuAuthorizationController}'s
     * authentication
     * @throws OAuthSystemException if the request cannot be authenticated.
     */
    @ExceptionHandler(IridaOAuthException.class)
    public String handleOAuthException(HttpServletRequest request, IridaOAuthException ex) throws OAuthSystemException {
        logger.debug("Caught IridaOAuthException.  Beginning OAuth2 authentication token flow.");
        String requestURI = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        return authController.authenticate(ex.getRemoteAPI(), requestURI);
    }

}
