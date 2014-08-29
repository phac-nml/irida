package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.RemoteAPISpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.ExceptionPropertyAndMessage;
import ca.corefacility.bioinformatics.irida.ria.utilities.Formats;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.DataTable;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Controller handling basic operations for listing, viewing, adding, and
 * removing {@link RemoteAPI}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Controller
@RequestMapping("/remote_api")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class RemoteAPIController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(RemoteAPIController.class);

	public static final String CLIENTS_PAGE = "remote_apis/list";
	public static final String DETAILS_PAGE = "remote_apis/remote_api_details";
	public static final String ADD_API_PAGE = "remote_apis/create";
	public static final String STATUS_PAGE = "remote_apis/status";

	public static final String VALID_OAUTH_CONNECTION = "valid";
	public static final String INVALID_OAUTH_TOKEN = "invalid_token";

	private final String SORT_BY_ID = "id";
	private final List<String> SORT_COLUMNS = Lists.newArrayList(SORT_BY_ID, "name", "clientId", "createdDate");
	private static final String SORT_ASCENDING = "asc";

	private final RemoteAPIService remoteAPIService;
	private final UserService userService;
	private final ProjectRemoteService projectRemoteService;
	private final MessageSource messageSource;

	// Map storing the message names for the
	// getErrorsFromDataIntegrityViolationException method
	private Map<String, ExceptionPropertyAndMessage> errorMessages = ImmutableMap.of(
			RemoteAPI.SERVICE_URI_CONSTRAINT_NAME, new ExceptionPropertyAndMessage("serviceURI",
					"remoteapi.create.serviceURIConflict"));

	@Autowired
	public RemoteAPIController(RemoteAPIService remoteAPIService, UserService userService,
			ProjectRemoteService projectRemoteService, MessageSource messageSource) {
		this.remoteAPIService = remoteAPIService;
		this.userService = userService;
		this.projectRemoteService = projectRemoteService;
		this.messageSource = messageSource;
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
	 * @return The name of the remote api details page view
	 */
	@RequestMapping("/{apiId}")
	public String read(@PathVariable Long apiId, Model model) {
		RemoteAPI remoteApi = remoteAPIService.read(apiId);
		model.addAttribute("remoteApi", remoteApi);
		return DETAILS_PAGE;
	}

	/**
	 * Remove a {@link RemoteAPI} with the given id
	 * 
	 * @param id
	 *            The ID to remove
	 * @return redirect to the remote apis list
	 */
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
	 * @return a Map<String,Object> for the table
	 */
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxAPIList(@RequestParam(DataTable.REQUEST_PARAM_START) Integer start,
			@RequestParam(DataTable.REQUEST_PARAM_LENGTH) Integer length,
			@RequestParam(DataTable.REQUEST_PARAM_DRAW) Integer draw,
			@RequestParam(value = DataTable.REQUEST_PARAM_SORT_COLUMN, defaultValue = "0") Integer sortColumn,
			@RequestParam(value = DataTable.REQUEST_PARAM_SORT_DIRECTION, defaultValue = "asc") String direction,
			@RequestParam(DataTable.REQUEST_PARAM_SEARCH_VALUE) String searchValue, Principal principal) {

		String sortString;

		User principalUser = userService.getUserByUsername(principal.getName());

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

			// only pass clientId to admin users
			if (principalUser.getSystemRole().equals(Role.ROLE_ADMIN)) {
				row.put("clientId", api.getClientId());
			}

			row.put("createdDate", Formats.DATE.format(api.getCreatedDate()));

			apiData.add(row);
		}

		Map<String, Object> map = new HashMap<>();
		map.put(DataTable.RESPONSE_PARAM_DRAW, draw);
		map.put(DataTable.RESPONSE_PARAM_RECORDS_TOTAL, search.getTotalElements());
		map.put(DataTable.RESPONSE_PARAM_RECORDS_FILTERED, search.getTotalElements());

		map.put(DataTable.RESPONSE_PARAM_DATA, apiData);
		return map;
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping("/status")
	public String getConnectionStatusPage() {
		return STATUS_PAGE;
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping("/status/{apiId}")
	@ResponseBody
	public String checkApiStatus(@PathVariable Long apiId) {
		RemoteAPI api = remoteAPIService.read(apiId);

		try {
			projectRemoteService.list(api);
			return VALID_OAUTH_CONNECTION;
		} catch (IridaOAuthException ex) {
			logger.debug("Can't connect to API: " + ex.getMessage());
			return INVALID_OAUTH_TOKEN;
		}
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping("/connect/{apiId}")
	public String connect(@PathVariable Long apiId) {
		RemoteAPI api = remoteAPIService.read(apiId);
		projectRemoteService.list(api);

		return "redirect:/remote_api/status";
	}
}
