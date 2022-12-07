import {
  createAction,
  createAsyncThunk,
  createReducer,
} from "@reduxjs/toolkit";
import { getMinimalSampleDetailsForFilteredProject } from "../../../apis/projects/samples";
import { putSampleInCart } from "../../../apis/cart/cart";
import { downloadPost } from "../../../utilities/file-utilities";
import { formatFilterBySampleNames } from "../../../utilities/table-utilities";
import isEqual from "lodash/isEqual";
import { setBaseUrl } from "../../utilities/url-utilities";
import { TableFilters, TableOptions } from "../../types/ant-design";
import { Sample } from "../../types/irida";

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

const reloadTable = createAction<null>("samples/table/reload");
const addSelectedSample = createAction("samples/table/selected/add");
const removeSelectedSample = createAction("samples/table/selected/remove");
const clearSelectedSamples = createAction("samples/table/selected/clear");
const clearFilterByFile = createAction("samples/table/clearFilterByFile");

type UpdateTableProps = {
  filters: TableFilters;
};

type SelectedSample = Pick<Sample, "id" | "name">;

/**
 * Updates the state of the table filters and search, which triggers
 * the re-render of the samples table.
 * @type {AsyncThunk<unknown, void, {}>}
 */
const updateTable = createAsyncThunk<
  string,
  UpdateTableProps,
  {
    state: {
      samples: {
        selected: SelectedSample[];
        options: { filters: TableFilters };
        selectedCount: number;
      };
    };
  }
>("samples/table/update", async (values, { getState }) => {
  const { options, selected, selectedCount } = getState().samples;
  if (
    isEqual(values?.search, options.search) &&
    isEqual(values?.filters, options.filters)
  ) {
    // Just a page change, don't update selected
    return { options: values, selected, selectedCount };
  }
  // Filters applied therefore need to clear any selections
  return { options: values, selected: {}, selectedCount: 0 };
});

/**
 * Called when selecting all samples from the Samples Table.
 *
 * This will trigger a "long load" since there might be a little of samples in
 * the table that data needs to be gathered for from the server.
 */
const selectAllSamples = createAsyncThunk(
  "/samples/table/selected/all",
  async (_, { getState }) => {
    const { samples } = getState();
    return await getMinimalSampleDetailsForFilteredProject(
      samples.options
    ).then((data) => {
      const selected = data.reduce(
        (accumulator, value) => ({ ...accumulator, [value.key]: value }),
        {}
      );
      return { selected, selectedCount: data.length };
    });
  }
);

/**
 * Called when adding samples to the cart
 */
const addToCart = createAsyncThunk(
  "/samples/table/selected/cart",
  async (_, { getState }) => {
    const { samples } = getState();
    // Sort by project id
    const samplesList = Object.values(samples.selected);
    const projects = samplesList.reduce((prev, current) => {
      if (!prev[current.projectId]) prev[current.projectId] = [];
      prev[current.projectId].push(current);
      return prev;
    }, {});

    const promises = [];
    for (const projectId in projects) {
      promises.push(putSampleInCart(projectId, projects[projectId]));
    }

    return Promise.all(promises).then((responses) => responses.pop());
  }
);

/**
 * Called when downloading samples (sequence files) from the server.
 */
const downloadSamples = createAsyncThunk(
  "/samples/table/export/download",
  async (_, { getState }) => {
    const { samples } = getState();
    const sampleIds = Object.values(samples.selected).map((s) => s.id);
    return await downloadPost(
      setBaseUrl(`/ajax/projects/${samples.projectId}/samples/download`),
      { sampleIds }
    );
  }
);

/**
 * Called when exporting the current state of the samples' table to either
 * a CSV of Excel file.
 */
const exportSamplesToFile = createAsyncThunk(
  "/samples/table/export",
  async (type, { getState }) => {
    const { samples } = getState();
    const options = { ...samples.options };
    if (samples.selectedCount > 0) {
      const sampleNamesFilter = formatFilterBySampleNames(
        Object.values(samples.selected)
      );
      options.search = [...options.search, sampleNamesFilter];
    }

    return await downloadPost(
      setBaseUrl(
        `/ajax/projects/${samples.projectId}/samples/export?type=${type}`
      ),
      options
    );
  }
);

const filterByFile = createAction(
  `samples/table/filterByFile`,
  ({ samples, filename }) => {
    return {
      payload: {
        filename,
        fileFilter: formatFilterBySampleNames(samples),
      },
    };
  }
);

/**
 * Since the initial table props may need to be reset at some point, we store
 * them in a string so they cannot be mutated.  When the table needs to be reset
 * to it's default state, just re-parse by calling this.
 * @returns {object} - default table state
 */
const getInitialTableOptions = (): TableOptions =>
  JSON.parse(INITIAL_TABLE_STATE);

/**
 * Called to format a sample when a sample is selected.
 * Needs to be converted to this format so that it can be used by the share
 * samples page and the cart.
 * @param projectSample - Sample details object returned as part of the table data
 * @returns {{sampleName: (Document.mergeForm.sampleName|Document.sampleName|string), owner: *, id: string, projectId: *, key: *}}
 */
const formatSelectedSample = (projectSample) => ({
  key: projectSample.key,
  id: projectSample.sample.id,
  projectId: projectSample.project.id,
  sampleName: projectSample.sample.sampleName,
  owner: projectSample.owner,
});

const initialState = {
  options: getInitialTableOptions(),
  selected: {},
  selectedCount: 0,
  loadingLong: false,
};

export default createReducer(initialState, (builder) => {
  builder
    .addCase(updateTable.fulfilled, (state, action) => {
      state.options = action.payload.options;
      state.selected = action.payload.selected;
      state.selectedCount = action.payload.selectedCount;
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
    .addCase(addToCart.fulfilled, (state) => {
      state.selected = {};
      state.selectedCount = 0;
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
  addToCart,
  downloadSamples,
  exportSamplesToFile,
};
