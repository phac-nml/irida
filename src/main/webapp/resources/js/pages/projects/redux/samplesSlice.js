import {
  createAction,
  createAsyncThunk,
  createReducer
} from "@reduxjs/toolkit";
import { getProjectIdFromUrl } from "../../../utilities/url-utilities";
import { INITIAL_TABLE_STATE } from "../samples/services/constants";
import { getMinimalSampleDetailsForFilteredProject } from "../../../apis/projects/project-samples";
import { putSampleInCart } from "../../../apis/cart/cart";
import { downloadPost } from "../../../utilities/file-utilities";

const updateTable = createAction("samples/table/update");
const reloadTable = createAction("samples/table/reload");
const addSelectedSample = createAction("samples/table/selected/add");
const removeSelectedSample = createAction("samples/table/selected/remove");
const clearSelectedSamples = createAction("samples/table/selected/clear");

const selectAllSamples = createAsyncThunk(
  "/samples/table/selected/all",
  async ({ projectId, options }, tableState) => {
    return await getMinimalSampleDetailsForFilteredProject(
      projectId,
      options
    ).then(({ data }) => {
      const selected = data.reduce(
        (accumulator, value) => ({ ...accumulator, [value.key]: value }),
        {}
      );
      return { selected, selectedCount: data.length };
    });
  }
);

const addToCart = createAsyncThunk(
  "/samples/table/selected/cart",
  async ({ projectId, selected }) => {
    return await putSampleInCart(projectId, Object.values(selected));
  }
);

const downloadSamples = createAsyncThunk(
  "/samples/table/export/download",
  async (_, { getState }) => {
    const { samples } = getState();
    const sampleIds = Object.values(samples.selected).map(s => s.id);
    return await downloadPost(
      `/ajax/projects/${samples.projectId}/samples/download`,
      { sampleIds }
    );
  }
);

const exportSamplesToFile = createAsyncThunk(
  "/samples/table/export",
  async (type, { getState }) => {
    const { samples } = getState();
    return await downloadPost(
      `/ajax/projects/${samples.projectId}/samples/export?type=${type}`,
      samples.options
    );
  }
);

const getInitialTableOptions = () => JSON.parse(INITIAL_TABLE_STATE);

const formatSelectedSample = projectSample => ({
  key: projectSample.key,
  id: projectSample.sample.id,
  projectId: projectSample.project.id,
  sampleName: projectSample.sample.sampleName,
  owner: projectSample.owner
});

const initialState = {
  projectId: getProjectIdFromUrl(),
  options: getInitialTableOptions(),
  selected: {},
  selectedCount: 0,
  loadingLong: false
};

export default createReducer(initialState, builder => {
  builder
    .addCase(updateTable, (state, action) => {
      // reset selected state when changing filters or search
      if (
        JSON.stringify(action.payload.search) !==
          JSON.stringify(state.options.search) ||
        JSON.stringify(action.payload.filters) !==
          JSON.stringify(state.options.filters)
      ) {
        state.selected = {};
        state.selectedCount = 0;
      }
      state.options = action.payload;
    })
    .addCase(reloadTable, state => {
      const newOptions = getInitialTableOptions();
      newOptions.pagination.pageSize = state.options.pagination.pageSize;
      newOptions.reload = Math.floor(Math.random() * 90000) + 10000; // Unique 5 digit number to trigger reload
      state.options = newOptions;
    })
    .addCase(addSelectedSample, (state, action) => {
      state.selected[action.payload.key] = formatSelectedSample(action.payload);
      state.selectedCount++;
    })
    .addCase(removeSelectedSample, (state, action) => {
      delete state.selected[action.payload];
      state.selectedCount--;
    })
    .addCase(clearSelectedSamples, state => {
      state.selected = {};
      state.selectedCount = 0;
    })
    .addCase(selectAllSamples.pending, state => {
      state.loadingLong = true;
    })
    .addCase(selectAllSamples.fulfilled, (state, action) => {
      state.selected = action.payload.selected;
      state.selectedCount = action.payload.selectedCount;
      state.loadingLong = false;
    })
    .addCase(addToCart.fulfilled, state => {
      state.selected = {};
      state.selectedCount = 0;
    })
    .addCase(downloadSamples.fulfilled, state => {
      state.selected = {};
      state.selectedCount = 0;
    })
    .addCase(exportSamplesToFile.pending, state => {
      state.loadingLong = true;
    })
    .addCase(exportSamplesToFile.fulfilled, state => {
      state.loadingLong = false;
    });
});

export {
  updateTable,
  reloadTable,
  addSelectedSample,
  removeSelectedSample,
  clearSelectedSamples,
  selectAllSamples,
  addToCart,
  downloadSamples,
  exportSamplesToFile
};
