import axios from "axios";

export interface PageTableFilters {
  [key: string]: string[];
}

export interface PageTableRequest {
  current: number;
  pageSize: number;
  sortColumn: string;
  sortDirection: string;
  search: string;
  filters: PageTableFilters;
}

/**
 * Default function to fetch paged table data.
 * @param url - datasource url for the paged table
 * @param body - table request payload for the paged table
 */
export async function fetchPageTableUpdate<T>(
  url: string,
  body: PageTableRequest
) {
  return axios.post<T>(url, body).then(({ data }) => data);
}
