import { createAction, createReducer } from "@reduxjs/toolkit";

export const setDestinationProject = createAction(
  `rootReducer/setDestinationProject`,
  (destinationId) => {
    return { payload: { destinationId } };
  }
);

const initialState = (() => {
  const sharedString = sessionStorage.getItem("share");
  return sharedString ? JSON.parse(sharedString) : {};
})();

export const rootReducer = createReducer(initialState, (builder) => {
  builder.addCase(setDestinationProject, (state, action) => {
    state.destinationId = action.payload.destinationId;
  });
});
