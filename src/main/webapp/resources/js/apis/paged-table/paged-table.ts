import axios from "axios";

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

export interface PageTableModel {
  id: number;
  key: string;
  name: string;
  createdDate: Date;
  modifiedDate: Date;
  [key: string]: any; // allows any additional keys of any type value
}

export interface PageTableResponse {
  dataSource: PageTableModel[];
  total: number
}

/**
 * Default function to fetch paged table data.
 * @param url - datasource url for the paged table
 * @param data - table request payload for the paged table
 */
export async function fetchPageTableUpdate(url: string, data: PageTableRequest): Promise<PageTableResponse> {
  return axios.post(url, data).then(({ data }) => data);
}
