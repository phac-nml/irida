import axios from "axios";
import {IridaBase} from "../../types/irida";

export interface PageTableFilters {
  [key: string]: string[]
}

export interface PageTableRequest {
  current: number;
  pageSize: number;
  sortColumn: string;
  sortDirection: string;
  search: string;
  filters: PageTableFilters;
}

export interface PageTableResponse {
  dataSource: PageTableModel[];
  total: number
}

export interface PageTableModel extends IridaBase {
  [key: string]: any; // allows any additional keys of any type value
}

/**
 * Default function to fetch paged table data.
 * @param url - datasource url for the paged table
 * @param data - table request payload for the paged table
 */
export async function fetchPageTableUpdate(url: string, data: PageTableRequest): Promise<PageTableResponse> {
  return axios.post(url, data).then(({ data }) => data);
}
