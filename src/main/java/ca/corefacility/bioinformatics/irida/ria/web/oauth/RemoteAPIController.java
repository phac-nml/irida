package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import java.security.Principal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
import ca.corefacility.bioinformatics.irida.repositories.specification.RemoteAPISpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.ExceptionPropertyAndMessage;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.DataTable;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

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
	private final List<String> SORT_COLUMNS = Lists.newArrayList(SORT_BY_ID, "name", "clientId", "createdDate");
	private static final String SORT_ASCENDING = "asc";

	private final RemoteAPIService remoteAPIService;
	private final ProjectRemoteService projectRemoteService;
	private final RemoteAPITokenService tokenService;
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
	public RemoteAPIController(RemoteAPIService remoteAPIService, ProjectRemoteService projectRemoteService,
			RemoteAPITokenService tokenService, OltuAuthorizationController authController, MessageSource messageSource) {
		this.remoteAPIService = remoteAPIService;
		this.projectRemoteService = projectRemoteService;
		this.tokenService = tokenService;
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
	public String list() {
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
	 * Ajax request page for getting a list of all {@link RemoteAPI}s
	 * 
	 * @param start
	 *            The start element of the page
	 * @param length
	 *            The page length
	 * @param draw
	 *            Whether to draw the table
	 * @param sortColumn
	 *            The column to sort on
	 * @param direction
	 *            The direction of the sort
	 * @param searchValue
	 *            The string search value for the table
	 * @param principal
	 *            a reference to the logged in user.
	 * @param locale
	 *            the locale specified by the browser.
	 * @return a Map for the table
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxAPIList(@RequestParam(DataTable.REQUEST_PARAM_START) Integer start,
			@RequestParam(DataTable.REQUEST_PARAM_LENGTH) Integer length,
			@RequestParam(DataTable.REQUEST_PARAM_DRAW) Integer draw,
			@RequestParam(value = DataTable.REQUEST_PARAM_SORT_COLUMN, defaultValue = "0") Integer sortColumn,
			@RequestParam(value = DataTable.REQUEST_PARAM_SORT_DIRECTION, defaultValue = "asc") String direction,
			@RequestParam(DataTable.REQUEST_PARAM_SEARCH_VALUE) String searchValue, Principal principal, Locale locale) {

		String sortString;

		try {
			sortString = SORT_COLUMNS.get(sortColumn);
		} catch (IndexOutOfBoundsException ex) {
			sortString = SORT_BY_ID;
		}

		Sort.Direction sortDirection = direction.equals(SORT_ASCENDING) ? Sort.Direction.ASC : Sort.Direction.DESC;

		int pageNum = start / length;

		Page<RemoteAPI> search = remoteAPIService.search(RemoteAPISpecification.searchRemoteAPI(searchValue), pageNum,
				length, sortDirection, sortString);

		List<Map<String, String>> apiData = new ArrayList<>();
		for (RemoteAPI api : search) {
			Map<String, String> row = new HashMap<>();
			row.put("id", api.getId().toString());
			row.put("name", api.getName());
			row.put("createdDate", dateFormatter.print(api.getCreatedDate(), locale));

			apiData.add(row);
		}

		Map<String, Object> map = new HashMap<>();
		map.put(DataTable.RESPONSE_PARAM_DRAW, draw);
		map.put(DataTable.RESPONSE_PARAM_RECORDS_TOTAL, search.getTotalElements());
		map.put(DataTable.RESPONSE_PARAM_RECORDS_FILTERED, search.getTotalElements());

		map.put(DataTable.RESPONSE_PARAM_DATA, apiData);
		return map;
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
	 * Check the currently logged in user's OAuth2 connection status to a given
	 * API and return the proper html to the user.
	 *
	 * @param apiId The ID of the api
	 * @return html fragment for current connection state.
	 */
	@RequestMapping("/status/web/{apiId}")
	public String checkWebApiStatus(@PathVariable Long apiId) {
		String status = checkApiStatus(apiId);
		return "remote_apis/fragments.html :: #" + status;
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
