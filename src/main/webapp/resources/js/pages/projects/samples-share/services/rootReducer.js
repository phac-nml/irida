import { createAction, createReducer } from "@reduxjs/toolkit";

export const setDestinationProject = createAction(
  `rootReducer/setDestinationProject`,
  (destinationId) => ({ payload: { destinationId } })
);

export const updatedSamplesOwnerStatus = createAction(
  `rootReducer/updatedSamplesOwnerStatus`,
  (owner) => ({ payload: { owner } })
);

const storedState = (() => {
  const sharedString = sessionStorage.getItem("share");
  return sharedString ? JSON.parse(sharedString) : {};
})();

export const rootReducer = createReducer(
  { ...storedState, owner: true },
  (builder) => {
    builder.addCase(setDestinationProject, (state, action) => {
      state.destinationId = action.payload.destinationId;
    });
    builder.addCase(updatedSamplesOwnerStatus, (state, action) => {
      state.owner = action.payload.owner;
    });
  }
);
