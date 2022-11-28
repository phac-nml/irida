import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import { TablePaginationConfig } from "antd";

export type SearchParams = {
  global: boolean;
  pagination: TablePaginationConfig | undefined;
  order: [
    {
      property: string;
      direction: "asc" | "desc";
    }
  ];
  query: string | null;
};

/**
 * Global search for projects
 * @param params
 */
async function fetchSearchSamples(params: SearchParams) {
  return axios.post(setBaseUrl(`/ajax/search/samples`), {
    global,
    pagination: params.pagination,
    order: params.order,
    search: [
      {
        property: `name`,
        value: params.query,
        operation: "MATCH_IN",
      },
    ],
  });
}

/**
 * Global search for samples
 * @param params
 */
async function fetchSearchProjects(params: SearchParams) {
  return axios.post(setBaseUrl(`/ajax/search/projects`), {
    global,
    pagination: params.pagination,
    order: params.order,
    search: [
      {
        property: `name`,
        value: params.query,
        operation: "MATCH_IN",
      },
    ],
  });
}

export async function fetchSearchResults(params: SearchParams) {
  const promises = [];
  promises.push(fetchSearchProjects(params));
  promises.push(fetchSearchSamples(params));
  return Promise.all(promises).then(
    ([{ data: projects }, { data: samples }]) => ({ projects, samples })
  );
}
