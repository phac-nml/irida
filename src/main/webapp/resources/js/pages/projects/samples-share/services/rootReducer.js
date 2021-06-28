import { createAction, createReducer } from "@reduxjs/toolkit";

export const setDestinationProject = createAction(
  `rootReducer/setDestinationProject`,
  (destinationId) => ({ payload: { destinationId } })
);

export const updatedSamplesOwnerStatus = createAction(
  `rootReducer/updatedSamplesOwnerStatus`,
  (owner) => ({ payload: { owner } })
);

export const setFields = createAction(
  `rootReducer/setFieldRestrictions`,
  (fields) => ({
    payload: { fields },
  })
);

export const updateFields = createAction(
  `rootReducer/updateFields`,
  (index, value) => ({ payload: { index, value } })
);

export const setStep = createAction(`rootReducer/setStep`, (step) => ({
  payload: { step },
}));

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
    builder.addCase(setFields, (state, action) => {
      state.fields = action.payload.fields;
    });
    builder.addCase(updateFields, (state, action) => {
      state.fields[action.payload.index].target.restriction =
        action.payload.value;
    });
    builder.addCase(setStep, (state, action) => {
      state.step = action.payload.step;
    });
  }
);
