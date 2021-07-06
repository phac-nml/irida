import { createReducer, createAction } from "@reduxjs/toolkit";

const initialState = {}

export const setHeaders = createAction(
  `rootReducers/setHeaders`,
  (headers, sampleNameColumn) => ({
    payload: { headers, sampleNameColumn }
  })
);

export const rootReducer = createReducer(
  initialState,
  (builder) => {
    builder.addCase(setHeaders, (state, action) => {
      state.headers = action.payload.headers;
      state.sampleNameColumn = action.payload.sampleNameColumn;
    });
  }
);