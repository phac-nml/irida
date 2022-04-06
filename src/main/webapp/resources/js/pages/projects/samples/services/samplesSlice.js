import { createAction, createReducer } from "@reduxjs/toolkit";
import { getProjectIdFromUrl } from "../../../../utilities/url-utilities";
import { INITIAL_TABLE_STATE } from "../constants";

const updateTable = createAction("samples/table/update");
const addSelectedSample = createAction("samples/table/selected/add");
const removeSelectedSample = createAction("samples/table/selected/remove");

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
    });
});

export { updateTable, addSelectedSample, removeSelectedSample };
