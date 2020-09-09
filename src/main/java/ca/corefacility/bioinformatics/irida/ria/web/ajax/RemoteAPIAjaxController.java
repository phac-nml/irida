package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.RemoteAPIModel;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIRemoteAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.specification.RemoteAPISpecification;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.rempoteapi.dto.RemoteAPITableModel;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

/**
 * Controller for asynchronous requests for remote api functionality.
 */
@RestController
@RequestMapping("/ajax/remote_api")
public class RemoteAPIAjaxController {
	private final RemoteAPIService remoteAPIService;
	private final ProjectRemoteService projectRemoteService;
	private final UIRemoteAPIService service;

	public static final String VALID_OAUTH_CONNECTION = "valid_token";
	public static final String INVALID_OAUTH_TOKEN = "invalid_token";

	@Autowired
	public RemoteAPIAjaxController(RemoteAPIService remoteAPIService, ProjectRemoteService projectRemoteService, UIRemoteAPIService service) {
		this.remoteAPIService = remoteAPIService;
		this.projectRemoteService = projectRemoteService;
		this.service = service;
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
	@RequestMapping("/status/{apiId}")
	public ResponseEntity<Date> checkAPIStatus(@PathVariable Long apiId) {
		try {
			return ResponseEntity.ok(service.checkAPIStatus(apiId));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@GetMapping("/{remoteId}")
	public ResponseEntity<RemoteAPIModel> getRemoteAPIDetails(@PathVariable long remoteId) {
		return ResponseEntity.ok(service.getRemoteApiDetails(remoteId));
	}

	@DeleteMapping("/{remoteId}/delete")
	public void deleteRemoteAPI(@PathVariable long remoteId) {
		service.deleteRemoteAPI(remoteId);
	}
}
