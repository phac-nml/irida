import {
  createAction,
  createAsyncThunk,
  createReducer,
} from "@reduxjs/toolkit";
import {
  getProjectIdFromUrl,
  setBaseUrl,
} from "../../../utilities/url-utilities";
import { INITIAL_TABLE_STATE } from "../samples/services/constants";
import { getMinimalSampleDetailsForFilteredProject } from "../../../apis/projects/samples";
import { putSampleInCart } from "../../../apis/cart/cart";
import { downloadPost } from "../../../utilities/file-utilities";
import { formatFilterBySampleNames } from "../../../utilities/table-utilities";
import isEqual from "lodash/isEqual";

const reloadTable = createAction("samples/table/reload");
const addSelectedSample = createAction("samples/table/selected/add");
const removeSelectedSample = createAction("samples/table/selected/remove");
const clearSelectedSamples = createAction("samples/table/selected/clear");
const clearFilterByFile = createAction("samples/table/clearFilterByFile");

const updateTable = createAsyncThunk(
  "samples/table/update",
  async (values, { getState }) => {
    const {
      samples: { options, selected, selectedCount },
    } = getState();
    if (
      isEqual(values?.search, options.search) &&
      isEqual(values?.filters, options.filters)
    ) {
      // Just a page change, don't update selected
      return { options: values, selected, selectedCount };
    }
    // Filters applied therefore need to clear any selections
    return { options: values, selected: {}, selectedCount: 0 };
  }
);

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
    return await putSampleInCart(
      samples.projectId,
      Object.values(samples.selected)
    );
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
const getInitialTableOptions = () => JSON.parse(INITIAL_TABLE_STATE);

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
  projectId: getProjectIdFromUrl(),
  options: getInitialTableOptions(),
  selected: {},
  selectedCount: 0,
  loadingLong: false,
};

export default createReducer(initialState, (builder) => {
  builder
    .addCase(updateTable.fulfilled, (state, action) => {
      console.log("FULFILLED", action.payload);
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
