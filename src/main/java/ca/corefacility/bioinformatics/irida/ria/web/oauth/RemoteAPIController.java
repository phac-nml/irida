package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.RemoteAPISpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.ExceptionPropertyAndMessage;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.rempoteapi.dto.RemoteAPIModel;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;

/**
 * Controller handling basic operations for listing, viewing, adding, and
 * removing {@link RemoteAPI}s
 * 
 *
 */
@Controller
@RequestMapping("/remote_api")
public class RemoteAPIController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(RemoteAPIController.class);

	public static final String CLIENTS_PAGE = "remote_apis/list";
	public static final String DETAILS_PAGE = "remote_apis/remote_api_details";
	public static final String ADD_API_PAGE = "remote_apis/create";
	public static final String PARENT_FRAME_RELOAD_PAGE = "remote_apis/parent_reload";

	public static final String VALID_OAUTH_CONNECTION = "valid_token";
	public static final String INVALID_OAUTH_TOKEN = "invalid_token";

	private final String SORT_BY_ID = "id";

	private final RemoteAPIService remoteAPIService;
	private final ProjectRemoteService projectRemoteService;
	private final RemoteAPITokenService tokenService;
	private final UserService userService;
	private final OltuAuthorizationController authController;
	private final MessageSource messageSource;
	private final Formatter<Date> dateFormatter;
	private final Formatter<Date> dateTimeFormatter;

	// Map storing the message names for the
	// getErrorsFromDataIntegrityViolationException method
	private Map<String, ExceptionPropertyAndMessage> errorMessages = ImmutableMap.of(
			RemoteAPI.SERVICE_URI_CONSTRAINT_NAME, new ExceptionPropertyAndMessage("serviceURI",
					"remoteapi.create.serviceURIConflict"));

	@Autowired
	public RemoteAPIController(RemoteAPIService remoteAPIService, ProjectRemoteService projectRemoteService, UserService userService,
			RemoteAPITokenService tokenService, OltuAuthorizationController authController, MessageSource messageSource) {
		this.remoteAPIService = remoteAPIService;
		this.projectRemoteService = projectRemoteService;
		this.tokenService = tokenService;
		this.userService = userService;
		this.authController = authController;
		this.messageSource = messageSource;
		this.dateFormatter = new DateFormatter();

		// creating second DateFormatter for date/time displays
		DateFormatter dateFormatter2 = new DateFormatter();
		// setting date/time formatter stile to medium date, small time. Will
		// display full date and basic time info.
		dateFormatter2.setStylePattern("MS");
		dateTimeFormatter = dateFormatter2;

	}

	/**
	 * Get the remote apis listing page
	 * 
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
	 * Get an individual remote API's page
	 * 
	 * @param apiId
	 *            The ID of the api
	 * @param model
	 *            Model for the view
	 * @param locale
	 *            the locale specified by the browser.
	 * @return The name of the remote api details page view
	 */
	@RequestMapping("/{apiId}")
	public String read(@PathVariable Long apiId, Model model, Locale locale) {
		RemoteAPI remoteApi = remoteAPIService.read(apiId);
		model.addAttribute("remoteApi", remoteApi);

		try {
			RemoteAPIToken token = tokenService.getToken(remoteApi);
			model.addAttribute("tokenExpiry", dateTimeFormatter.print(token.getExpiryDate(), locale));
		} catch (EntityNotFoundException ex) {
			// Not returning a token here is acceptable. The view will have to
			// handle a state if the token does not exist
			logger.trace("No token for service " + remoteApi);
		}

		return DETAILS_PAGE;
	}

	/**
	 * Remove a {@link RemoteAPI} with the given id
	 * 
	 * @param id
	 *            The ID to remove
	 * @return redirect to the remote apis list
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public String removeClient(@RequestParam Long id) {
		logger.trace("Deleting remote client " + id);
		remoteAPIService.delete(id);

		return "redirect:/remote_api";
	}

	/**
	 * Get the create client page
	 * 
	 * @param model
	 *            Model for the view
	 * @return The name of the create client page
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String getAddRemoteAPIPage(Model model) {
		if (!model.containsAttribute("errors")) {
			model.addAttribute("errors", new HashMap<String, String>());
		}

		return ADD_API_PAGE;
	}

	/**
	 * Create a new client
	 * 
	 * @param client
	 *            The client to add
	 * @param model
	 *            Model for the view
	 * @param locale
	 *            Locale of the current user session
	 * @return Redirect to the newly created client page, or back to the
	 *         creation page in case of an error.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String postCreateRemoteAPI(RemoteAPI client, Model model, Locale locale) {

		Map<String, String> errors = new HashMap<>();
		String responsePage = null;
		try {
			RemoteAPI create = remoteAPIService.create(client);
			responsePage = "redirect:/remote_api/" + create.getId();
		} catch (ConstraintViolationException ex) {
			logger.error("Error creating api: " + ex.getMessage());
			errors.putAll(getErrorsFromViolationException(ex));
		} catch (DataIntegrityViolationException ex) {
			logger.error("Error creating api: " + ex.getMessage());
			errors.putAll(getErrorsFromDataIntegrityViolationException(ex, errorMessages, messageSource, locale));
		}

		if (!errors.isEmpty()) {
			model.addAttribute("errors", errors);

			model.addAttribute("given_name", client.getName());
			model.addAttribute("given_clientId", client.getClientId());
			model.addAttribute("given_clientSecret", client.getClientSecret());
			model.addAttribute("given_serviceURI", client.getServiceURI());

			responsePage = getAddRemoteAPIPage(model);
		}

		return responsePage;
	}

	/**
	 * Get a list of the current page for the Remote API Table
	 *
	 * @param tableRequest - the details for the current page of the Table
	 * @return {@link TableResponse}
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TableResponse<RemoteAPIModel> getAjaxAPIList(@RequestBody TableRequest tableRequest) {
		Page<RemoteAPI> search = remoteAPIService.search(
				RemoteAPISpecification.searchRemoteAPI(tableRequest.getSearch()), tableRequest.getCurrent(),
				tableRequest.getPageSize(), tableRequest.getSortDirection(), tableRequest.getSortColumn());

		List<RemoteAPIModel> apiData = search.getContent()
				.stream()
				.map(RemoteAPIModel::new)
				.collect(Collectors.toList());
		return new TableResponse<>(apiData, search.getTotalElements());
	}

	/**
	 * Check the currently logged in user's OAuth2 connection status to a given
	 * API
	 * 
	 * @param apiId
	 *            The ID of the api
	 * @return "valid" or "invalid_token" message
	 */
	@RequestMapping("/status/{apiId}")
	@ResponseBody
	public String checkApiStatus(@PathVariable Long apiId) {
		RemoteAPI api = remoteAPIService.read(apiId);

		try {
			projectRemoteService.getServiceStatus(api);
			return VALID_OAUTH_CONNECTION;
		} catch (IridaOAuthException ex) {
			logger.debug("Can't connect to API: " + ex.getMessage());
			return INVALID_OAUTH_TOKEN;
		}
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
	 * @param apiId
	 *            the ID of the api to connect to
	 * @param model
	 *            the model to add attributes to.
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
	 * @param request
	 *            The incoming request method
	 * @param ex
	 *            The thrown exception
	 * @return A redirect to the {@link OltuAuthorizationController}'s
	 *         authentication
	 * @throws OAuthSystemException
	 *             if the request cannot be authenticated.
	 */
	@ExceptionHandler(IridaOAuthException.class)
	public String handleOAuthException(HttpServletRequest request, IridaOAuthException ex) throws OAuthSystemException {
		logger.debug("Caught IridaOAuthException.  Beginning OAuth2 authentication token flow.");
		String requestURI = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

		return authController.authenticate(ex.getRemoteAPI(), requestURI);
	}

}
