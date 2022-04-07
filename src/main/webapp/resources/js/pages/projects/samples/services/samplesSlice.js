import {
  createAction,
  createAsyncThunk,
  createReducer,
} from "@reduxjs/toolkit";
import { getProjectIdFromUrl } from "../../../../utilities/url-utilities";
import { INITIAL_TABLE_STATE } from "../constants";
import { getMinimalSampleDetailsForFilteredProject } from "../../../../apis/projects/project-samples";

const updateTable = createAction("samples/table/update");
const addSelectedSample = createAction("samples/table/selected/add");
const removeSelectedSample = createAction("samples/table/selected/remove");
const clearSelectedSamples = createAction("samples/table/selected/clear");

const selectAllSamples = createAsyncThunk(
  "/samples/table/selected/all",
  async (projectId, tableState) => {
    return await getMinimalSampleDetailsForFilteredProject(
      projectId,
      tableState
    ).then(({ data }) =>
      data.reduce(
        (accumulator, value) => ({ ...accumulator, [value.key]: value }),
        {}
      )
    );
  }
);

const getInitialTableOptions = () => JSON.parse(INITIAL_TABLE_STATE);

const formatSelectedSample = (sample) => ({
  key: sample.key,
  id: sample.sample.id,
  projectId: sample.project.id,
  sampleName: sample.sample.sampleName,
  owner: sample.owner,
});

const initialState = {
  projectId: getProjectIdFromUrl(),
  options: getInitialTableOptions(),
  selected: {},
  loadingLong: false,
};

export default createReducer(initialState, (builder) => {
  builder
    .addCase(updateTable, (state, action) => {
      state.options = action.payload;
      state.selected = {};
    })
    .addCase(addSelectedSample, (state, action) => {
      state.selected[action.payload.key] = formatSelectedSample(action.payload);
    })
    .addCase(removeSelectedSample, (state, action) => {
      delete state.selected[action.payload];
    })
    .addCase(clearSelectedSamples, (state) => {
      state.selected = {};
    })
    .addCase(selectAllSamples.pending, (state) => {
      state.loadingLong = true;
    })
    .addCase(selectAllSamples.fulfilled, (state, action) => {
      state.selected = action.payload;
      state.loadingLong = false;
    });
});

export {
  updateTable,
  addSelectedSample,
  removeSelectedSample,
  clearSelectedSamples,
  selectAllSamples,
};