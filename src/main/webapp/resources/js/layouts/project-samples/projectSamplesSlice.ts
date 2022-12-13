import { createAction, createSlice } from "@reduxjs/toolkit";
import type { TableOptions, TableSearch } from "../../types/ant-design";
import { SelectedSample } from "../../types/irida";

export type FilterByFile = {
  filename: string;
  fileFilter: TableSearch;
};

export type SamplesTableState = {
  filters?: { associated: number[] };
  options: TableOptions;
  selectedCount: number;
  selected: { [key: string]: SelectedSample };
  loadingLong?: boolean;
  filterByFile?: FilterByFile | null;
};

const INITIAL_TABLE_OPTIONS: TableOptions = {
  filters: {},
  pagination: {
    current: 1,
    pageSize: 10,
  },
  order: [{ property: "sample.modifiedDate", direction: "desc" }],
  search: [],
};

const slice = createSlice({
  name: `project-samples`,
  initialState: { options: INITIAL_TABLE_OPTIONS },
  reducers: {
    tableUpdated: (state, action) => {
      console.log(action);
    },
  },
});

export default slice.reducer;

export const { tableUpdated } = slice.actions;
