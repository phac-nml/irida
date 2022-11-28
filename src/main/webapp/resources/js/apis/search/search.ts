import axios, { AxiosResponse } from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import { TablePaginationConfig } from "antd";
import { SearchProject, SearchSample } from "../../pages/search/SearchLayout";

export type SearchParams = {
  global: boolean;
  pagination: TablePaginationConfig | undefined;
  order: [
    {
      property: string;
      direction: "asc" | "desc";
    }
  ];
  search: [
    {
      property: string;
      value: string;
      operation: "MATCH_IN";
    }
  ];
};

/**
 * Global search for projects
 * @param params
 */
export async function fetchSearchSamples(
  params: SearchParams
): Promise<AxiosResponse<SearchSample[]>> {
  return axios.post<SearchSample[]>(setBaseUrl(`/ajax/search/samples`), params);
}

/**
 * Global search for samples
 * @param params
 */
export async function fetchSearchProjects(
  params: SearchParams
): Promise<AxiosResponse<SearchProject[]>> {
  return await axios.post<SearchProject[]>(
    setBaseUrl(`/ajax/search/projects`),
    params
  );
}
