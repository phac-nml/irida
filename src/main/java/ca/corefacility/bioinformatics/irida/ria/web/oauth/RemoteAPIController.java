package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.specification.RemoteAPISpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.Formats;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.DataTable;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;

import com.google.common.collect.Lists;

@Controller
@RequestMapping("/remote_api")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class RemoteAPIController {
	private static final Logger logger = LoggerFactory.getLogger(RemoteAPIController.class);

	public static final String CLIENTS_PAGE = "remote_apis/list";

	private final String SORT_BY_ID = "id";
	private final List<String> SORT_COLUMNS = Lists.newArrayList(SORT_BY_ID, "name", "clientId", "createdDate");
	private static final String SORT_ASCENDING = "asc";

	private final RemoteAPIService remoteAPIService;

	@Autowired
	public RemoteAPIController(RemoteAPIService remoteAPIService) {
		this.remoteAPIService = remoteAPIService;
	}

	@RequestMapping
	public String list() {
		return CLIENTS_PAGE;
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
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxAPIList(@RequestParam(DataTable.REQUEST_PARAM_START) Integer start,
			@RequestParam(DataTable.REQUEST_PARAM_LENGTH) Integer length,
			@RequestParam(DataTable.REQUEST_PARAM_DRAW) Integer draw,
			@RequestParam(value = DataTable.REQUEST_PARAM_SORT_COLUMN, defaultValue = "0") Integer sortColumn,
			@RequestParam(value = DataTable.REQUEST_PARAM_SORT_DIRECTION, defaultValue = "asc") String direction,
			@RequestParam(DataTable.REQUEST_PARAM_SEARCH_VALUE) String searchValue) {

		logger.trace("Listing apis");
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

		List<List<String>> apiData = new ArrayList<>();
		for (RemoteAPI api : search) {

			List<String> row = new ArrayList<>();
			row.add(api.getId().toString());
			row.add(api.getName());
			row.add(api.getClientId());
			row.add(Formats.DATE.format(api.getCreatedDate()));

			apiData.add(row);
		}

		Map<String, Object> map = new HashMap<>();
		map.put(DataTable.RESPONSE_PARAM_DRAW, draw);
		map.put(DataTable.RESPONSE_PARAM_RECORDS_TOTAL, search.getTotalElements());
		map.put(DataTable.RESPONSE_PARAM_RECORDS_FILTERED, search.getTotalElements());

		map.put(DataTable.RESPONSE_PARAM_DATA, apiData);
		return map;
	}
}
