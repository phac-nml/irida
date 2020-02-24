package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.specification.RemoteAPISpecification;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.rempoteapi.dto.RemoteAPITableModel;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

@RestController
@RequestMapping("/ajax/remote_api")
public class RemoteAPIAjaxController {
	private final RemoteAPIService remoteAPIService;
	private final ProjectRemoteService projectRemoteService;

	public static final String VALID_OAUTH_CONNECTION = "valid_token";
	public static final String INVALID_OAUTH_TOKEN = "invalid_token";

	@Autowired
	public RemoteAPIAjaxController(RemoteAPIService remoteAPIService, ProjectRemoteService projectRemoteService) {
		this.remoteAPIService = remoteAPIService;
		this.projectRemoteService = projectRemoteService;
	}

	/**
	 * Get a list of the current page for the Remote API Table
	 *
	 * @param tableRequest - the details for the current page of the Table
	 * @return {@link TableResponse}
	 */
	@RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	TableResponse<RemoteAPITableModel> getAjaxAPIList(@RequestBody TableRequest tableRequest) {
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
	@ResponseBody
	public String checkApiStatus(@PathVariable Long apiId) {
		RemoteAPI api = remoteAPIService.read(apiId);

		try {
			projectRemoteService.getServiceStatus(api);
			return VALID_OAUTH_CONNECTION;
		} catch (IridaOAuthException ex) {
			return INVALID_OAUTH_TOKEN;
		}
	}
}
