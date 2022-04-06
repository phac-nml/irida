import { createAction, createReducer } from "@reduxjs/toolkit";
import { getProjectIdFromUrl } from "../../../utilities/url-utilities";
import { INITIAL_TABLE_STATE } from "./constants";

const updateTable = createAction("samples/table/update");

const initialState = {
  projectId: getProjectIdFromUrl(),
  options: { ...INITIAL_TABLE_STATE },
};

export default createReducer(initialState, (builder) => {
  builder.addCase(updateTable, (state, action) => {
    state.options = { ...state.options, ...action.payload };
  });
});

export { updateTable };
