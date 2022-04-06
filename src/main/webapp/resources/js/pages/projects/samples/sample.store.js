import { createAction, createReducer } from "@reduxjs/toolkit";
import { getProjectIdFromUrl } from "../../../utilities/url-utilities";

const updateTable = createAction("samples/table/update");

const initialState = {
  projectId: getProjectIdFromUrl(),
  options: {
    filters: { associated: null },
    pagination: {
      current: 1,
      pageSize: 10
    },
    order: [{ property: "sample.modifiedDate", direction: "desc" }]
  }
};

export default createReducer(initialState, builder => {
  builder.addCase(updateTable, (state, action) => {
    console.log(action.payload);
    state.options = { ...state.options, ...action.payload };
  });
});

export { updateTable };
