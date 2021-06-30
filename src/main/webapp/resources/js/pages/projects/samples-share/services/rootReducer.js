import { createAction, createReducer } from "@reduxjs/toolkit";

export const setDestinationProject = createAction(
  `rootReducer/setDestinationProject`,
  (project) => ({ payload: { project } })
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

export const setNextStep = createAction(`rootReducer/nextStep`);

export const setPreviousStep = createAction(`rootReducer/previousStep`);

export const removeSample = createAction(
  `rootReducer/removeSample`,
  (index) => ({
    payload: { index },
  })
);

const storedState = (() => {
  const sharedString = sessionStorage.getItem("share");
  return sharedString ? JSON.parse(sharedString) : {};
})();

export const rootReducer = createReducer(
  { ...storedState, owner: true, step: 0 },
  (builder) => {
    builder.addCase(setDestinationProject, (state, action) => {
      state.destination = action.payload.project;
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
    builder.addCase(setNextStep, (state) => {
      state.step = state.step + 1;
    });
    builder.addCase(setPreviousStep, (state) => {
      state.step = state.step - 1;
    });
    builder.addCase(removeSample, (state, action) => {
      const samples = [...state.samples];
      samples.splice(action.payload.index, 1);
      state.samples = samples;
    });
  }
);
