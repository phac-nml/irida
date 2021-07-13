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


/*
Redux reducer for project metadata.
For more information on redux reducers see: https://redux-toolkit.js.org/api/createReducer
 */
export const rootReducer = createReducer(
  initialState,
  (builder) => {
    builder.addCase(setHeaders, (state, action) => {
      state.headers = action.payload.headers;
      state.sampleNameColumn = action.payload.sampleNameColumn;
    });
  }
);