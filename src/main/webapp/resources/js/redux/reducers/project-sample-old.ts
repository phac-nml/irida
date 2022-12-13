import {
  createAction,
  createAsyncThunk,
  createReducer,
} from "@reduxjs/toolkit";
import isEqual from "lodash/isEqual";
import { getMinimalSampleDetailsForFilteredProject } from "../../../apis/projects/samples";
import { TableOptions, TableSearch } from "../../../types/ant-design";
import { ProjectMinimal, Sample, SelectedSample } from "../../../types/irida";
import { downloadPost } from "../../../utilities/file-utilities";
import { formatFilterBySampleNames } from "../../../utilities/table-utilities";
import {
  getProjectIdFromUrl,
  setBaseUrl,
} from "../../../utilities/url-utilities";

/**
 * Initial state of the sample table
 */
export const INITIAL_TABLE_STATE = JSON.stringify({
  filters: { associated: null },
  pagination: {
    current: 1,
    pageSize: 10,
  },
  order: [{ property: "sample.modifiedDate", direction: "desc" }],
  search: [],
});

export type SamplesTableState = {
  projectId: string | number | undefined; // TODO: (Josh - 12/7/22) This will be removed in subsequent PR
  options: TableOptions;
  selectedCount: number;
  selected: { [key: string]: SelectedSample };
  loadingLong?: boolean;
  filterByFile?: FilterByFile | null;
};

export type TableSample = {
  sample: Sample;
  project: ProjectMinimal;
  coverage: number;
  key: string;
  owner: boolean;
  qcStatus: string;
};

export type FilterByFile = {
  filename: string;
  fileFilter: TableSearch;
};

const addSelectedSample = createAction<TableSample>(
  "samples/table/selected/add"
);
const clearFilterByFile = createAction("samples/table/clearFilterByFile");
const clearSelectedSamples = createAction("samples/table/selected/clear");
const reloadTable = createAction("samples/table/reload");
const removeSelectedSample = createAction<string>(
  "samples/table/selected/remove"
);
const updateTable = createAction<TableOptions>("samples/table/update");

/**
 * Called when selecting all samples from the Samples Table.
 *
 * This will trigger a "long load" since there might be a little of samples in
 * the table that data need to be gathered for from the server.
 */
const selectAllSamples = createAsyncThunk<
  Pick<SamplesTableState, "selected" | "selectedCount">,
  void,
  { state: { samples: SamplesTableState } }
>("/samples/table/selected/all", async (_, { getState }) => {
  const { options } = getState().samples;

  const data = await getMinimalSampleDetailsForFilteredProject(options);

  const selected = data.reduce(
    (accumulator, value) => ({ ...accumulator, [value.key]: value }),
    {}
  );
  return { selected, selectedCount: data.length };
});

/**
 * Called when downloading samples (sequence files) from the server.
 */
const downloadSamples = createAsyncThunk<
  void,
  void,
  { state: { samples: SamplesTableState } }
>("/samples/table/export/download", async (_, { getState }) => {
  // TODO: (Josh - 12/7/22) This should not be in here, move out in samples page refactor.
  const { selected, projectId } = getState().samples;
  const sampleIds = Object.values(selected).map((s) => s.id);
  await downloadPost(
    setBaseUrl(`/ajax/projects/${projectId}/samples/download`),
    { sampleIds }
  );
});

/**
 * Called when exporting the current state of the samples' table to either
 * a CSV of Excel file.
 */
const exportSamplesToFile = createAsyncThunk<
  void,
  string,
  { state: { samples: SamplesTableState } }
>("/samples/table/export", async (type, { getState }) => {
  // TODO: (Josh - 12/7/22) This should not be in here, move out in samples page refactor.
  const { samples } = getState();
  const options = { ...samples.options };
  if (samples.selectedCount > 0) {
    const sampleNamesFilter = formatFilterBySampleNames(
      Object.values(samples.selected)
    );
    options.search = [...options.search, sampleNamesFilter];
  }

  await downloadPost(
    setBaseUrl(
      `/ajax/projects/${samples.projectId}/samples/export?type=${type}`
    ),
    options
  );
});

const filterByFile = createAction(
  `samples/table/filterByFile`,
  ({
    samples,
    filename,
  }: {
    samples: SelectedSample[];
    filename: string;
  }): {
    payload: FilterByFile;
  } => {
    return {
      payload: {
        filename,
        fileFilter: formatFilterBySampleNames(samples),
      },
    };
  }
);

/**
 * Since the initial table props may need to be reset at some point, store
 * them in a string, so they can't mutate. When the table needs to be reset
 * to it's default state, just reparse by calling this.
 * @returns {object} - default table state
 */
const getInitialTableOptions = () => JSON.parse(INITIAL_TABLE_STATE);

/**
 * Called to format a sample when a sample is selected.
 * Needs to be converted to this format so that it can be used by the share
 * samples page and the cart.
 * @param projectSample - Sample details object returned as part of the table data
 */
const formatSelectedSample = (projectSample: TableSample): SelectedSample => ({
  key: projectSample.key,
  id: projectSample.sample.id,
  projectId: projectSample.project.id,
  sampleName: projectSample.sample.sampleName,
  owner: projectSample.owner,
});

const initialState: SamplesTableState = {
  projectId: getProjectIdFromUrl(), // TODO: (Josh - 12/7/22) This will get cleaned up in future PR
  options: getInitialTableOptions(),
  selected: {},
  selectedCount: 0,
  loadingLong: false,
};

export default createReducer(initialState, (builder) => {
  builder
    .addCase(updateTable, (state, { payload }) => {
      const { options } = state;

      if (
        isEqual(payload.search, options.search) &&
        isEqual(payload.filters, options.filters)
      ) {
        // Just a page change, don't update selected
        state.options = payload;
      } else {
        state.options = payload;
        state.selected = {};
        state.selectedCount = 0;
      }
    })
    .addCase(reloadTable, (state) => {
      const newOptions = getInitialTableOptions();
      newOptions.pagination.pageSize = state.options.pagination.pageSize;
      newOptions.reload = Math.floor(Math.random() * 90000) + 10000; // Unique 5 digit number to trigger reload
      state.options = newOptions;
      state.selected = {};
      state.selectedCount = 0;
    })
    .addCase(addSelectedSample, (state, action) => {
      state.selected[action.payload.key] = formatSelectedSample(action.payload);
      state.selectedCount++;
    })
    .addCase(removeSelectedSample, (state, action) => {
      delete state.selected[action.payload];
      state.selectedCount--;
    })
    .addCase(clearSelectedSamples, (state) => {
      state.selected = {};
      state.selectedCount = 0;
    })
    .addCase(selectAllSamples.pending, (state) => {
      state.loadingLong = true;
    })
    .addCase(selectAllSamples.fulfilled, (state, action) => {
      state.selected = action.payload.selected;
      state.selectedCount = action.payload.selectedCount;
      state.loadingLong = false;
    })
    .addCase(downloadSamples.fulfilled, (state) => {
      state.selected = {};
      state.selectedCount = 0;
    })
    .addCase(exportSamplesToFile.pending, (state) => {
      state.loadingLong = true;
    })
    .addCase(exportSamplesToFile.fulfilled, (state) => {
      state.loadingLong = false;
    })
    .addCase(filterByFile, (state, action) => {
      state.options.search.push(action.payload.fileFilter);
      state.filterByFile = action.payload;
    })
    .addCase(clearFilterByFile, (state) => {
      state.filterByFile = null;
      // Need to specifically remove the filter by file from the search filters.
      state.options.search = state.options.search.filter(
        (filter) => !filter._file
      );
      state.options.reload = Math.floor(Math.random() * 90000) + 10000;
    });
});

export {
  updateTable,
  reloadTable,
  addSelectedSample,
  removeSelectedSample,
  clearSelectedSamples,
  filterByFile,
  clearFilterByFile,
  selectAllSamples,
  downloadSamples,
  exportSamplesToFile,
};
