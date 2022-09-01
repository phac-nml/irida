import { createAction, createReducer } from "@reduxjs/toolkit";

const initialState = {
  sampleNameColumn: "",
  headers: [],
  metadata: [],
};

/*
Redux action for setting the sample name column.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setSampleNameColumn = createAction(
  `importReducer/setSampleNameColumn`,
  (sampleNameColumn) => ({
    payload: { sampleNameColumn },
  })
);

/*
Redux action for setting the metadata headers.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setHeaders = createAction(
  `importReducer/setHeaders`,
  (headers) => ({
    payload: { headers },
  })
);

/*
Redux action for setting the project metadata.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setMetadata = createAction(
  `importReducer/setMetadata`,
  (metadata) => ({
    payload: { metadata },
  })
);

/*
Redux reducer for project metadata.
For more information on redux reducers see: https://redux-toolkit.js.org/api/createReducer
 */
export const importReducer = createReducer(initialState, (builder) => {
  builder.addCase(setSampleNameColumn, (state, action) => {
    state.sampleNameColumn = action.payload.sampleNameColumn;
  });
  builder.addCase(setHeaders, (state, action) => {
    state.headers = action.payload.headers;
  });
  builder.addCase(setMetadata, (state, action) => {
    state.metadata = action.payload.metadata;
  });
});
