import moment from "moment";
import {
  TableFilters,
  TableOperation,
  TableSearch,
  TableSortOrder,
} from "../types/ant-design";
import { SelectedSample } from "../types/irida";
import { SorterResult } from "antd/es/table/interface";
import { ProjectSample } from "../redux/endpoints/project-samples";
import { direction } from "@antv/matrix-util/lib/ext";

/**
 * Format Sort Order from the Ant Design sorter object
 * @param {array | object} sorter Ant Design sorter object
 * @returns array of Sort Order objects
 */
export function formatSort(
  sorter: SorterResult<ProjectSample> | SorterResult<ProjectSample>[]
): TableSortOrder[] | undefined {
  const formatProperty = (property: string[]): string => property.join(".");

  const fromSorter = (item: SorterResult<ProjectSample>): TableSortOrder => ({
    property: Array.isArray(item.field)
      ? formatProperty(item.field)
      : (item.field as string),
    direction: item.order === "descend" ? "desc" : "asc",
  });

  if (Array.isArray(sorter)) {
    return sorter.map(fromSorter);
  } else if (typeof sorter.order === "undefined") {
    return undefined;
  }
  return [fromSorter(sorter)];
}

/**
 * Format Search from the Ant Design filters object
 * @param {array | object} filters Ant Design filters object
 * @returns array of Search objects
 */
export function formatSearch(filters: TableFilters): TableSearch[] {
  const defaultOperation: TableOperation = "MATCH";
  const formattedSearch = [];

  for (const filter in filters) {
    for (const index in filters[filter]) {
      const value = filters[filter][index];

      // if we have two values, and they are both moment objects then add searches for date range.
      if (
        Array.isArray(value) &&
        value.length === 2 &&
        moment.isMoment(value[0]) &&
        moment.isMoment(value[1])
      ) {
        formattedSearch.push({
          property: filter,
          value: value[0].unix(),
          operation: "GREATER_THAN_EQUAL",
        });
        formattedSearch.push({
          property: filter,
          value: value[1].unix(),
          operation: "LESS_THAN_EQUAL",
        });
      } else {
        // if more than one value is provided use "IN" operation, otherwise use "MATCH" operation
        formattedSearch.push({
          property: filter,
          value,
          operation: Array.isArray(value) ? "MATCH_IN" : defaultOperation,
        });
      }
    }
  }

  console.log(formattedSearch);

  return formattedSearch;
}

/**
 * Format the filter by samples names value to send to the server
 * @param samples
 */
export function formatFilterBySampleNames(
  samples: SelectedSample[]
): TableSearch {
  return {
    property: "sample.sampleName",
    value: samples.map((sample) => sample.sampleName),
    operation: "IN",
    _file: true,
  };
}

export const stringSorter = (property) => (a, b) =>
  a[property].localeCompare(b[property], window.TL.LANGUAGE_TAG, {
    sensitivity: "base",
  });
