import { createReducer, createAction } from "@reduxjs/toolkit";

const initialState = {}

/*
Redux action for project metadata.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setHeaders = createAction(
  `rootReducers/setHeaders`,
  (headers, sampleNameColumn) => ({
    payload: { headers, sampleNameColumn }
  })
);

export const setSamples = createAction(
  `rootReducers/setSamples`,
  (sampleNames) => ({
    payload: { sampleNames }
  })
);

/*
Redux reducer for project metadata.
For more information on redux reducers see: https://redux-toolkit.js.org/api/createReducer
 */
export const rootReducer = createReducer(
  initialState,
  (builder) => {
    builder
      .addCase(setHeaders, (state, action) => {
        state.headers = action.payload.headers;
        state.sampleNameColumn = action.payload.sampleNameColumn;
      })
      .addCase(setSamples, (state, action) => {
        state.sampleNames = action.payload.sampleNames;
      });
  }
);