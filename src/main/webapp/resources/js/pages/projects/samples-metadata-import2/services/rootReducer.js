import { createReducer, createAction } from "@reduxjs/toolkit";

const initialState = {}

export const setHeaders = createAction(
  `rootReducers/setHeaders`,
  (headers, nameColumn) => ({
    payload: { headers, nameColumn }
  })
);

export const rootReducer = createReducer(
  initialState,
  (builder) => {
    builder.addCase(setHeaders, (state, action) => {
      state.headers = action.payload.headers;
      state.nameColumn = action.payload.nameColumn;
    });
  }
);