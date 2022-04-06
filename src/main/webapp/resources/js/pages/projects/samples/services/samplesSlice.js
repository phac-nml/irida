import {
  createAction,
  createAsyncThunk,
  createReducer,
} from "@reduxjs/toolkit";
import { getProjectIdFromUrl } from "../../../../utilities/url-utilities";
import { INITIAL_TABLE_STATE } from "../constants";
import { getAllSampleIds } from "../../../../apis/projects/project-samples";

const updateTable = createAction("samples/table/update");
const addSelectedSample = createAction("samples/table/selected/add");
const removeSelectedSample = createAction("samples/table/selected/remove");
const clearSelectedSamples = createAction("samples/table/selected/clear");

const selectAllSamples = createAsyncThunk(
  "/samples/table/selected/all",
  async (projectId, tableState) => {
    return await getAllSampleIds(projectId, tableState).then(({ data }) =>
      data.reduce(
        (accumulator, value) => ({ ...accumulator, [value.key]: value }),
        {}
      )
    );
  }
);

const formatSelectedSample = (sample) => ({
  key: sample.key,
  id: sample.sample.id,
  projectId: sample.project.id,
  sampleName: sample.sample.sampleName,
  owner: sample.owner,
});

const initialState = {
  projectId: getProjectIdFromUrl(),
  options: { ...INITIAL_TABLE_STATE },
  selected: {},
};

export default createReducer(initialState, (builder) => {
  builder
    .addCase(updateTable, (state, action) => {
      state.options = { ...state.options, ...action.payload };
    })
    .addCase(addSelectedSample, (state, action) => {
      state.selected[action.payload.key] = formatSelectedSample(action.payload);
    })
    .addCase(removeSelectedSample, (state, action) => {
      delete state.selected[action.payload];
    })
    .addCase(clearSelectedSamples, (state, action) => {
      state.selected = {};
    })
    .addCase(selectAllSamples.fulfilled, (state, action) => {
      state.selected = action.payload;
    });
});

export {
  updateTable,
  addSelectedSample,
  removeSelectedSample,
  clearSelectedSamples,
  selectAllSamples,
};
