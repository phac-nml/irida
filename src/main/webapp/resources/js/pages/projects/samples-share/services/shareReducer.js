import {
  createAction,
  createAsyncThunk,
  createReducer,
} from "@reduxjs/toolkit";

export const setDestinationProject = createAction(
  `shareReducer/setDestinationProject`,
  (project) => ({ payload: { project } })
);

export const updatedSamplesOwnerStatus = createAction(
  `shareReducer/updatedSamplesOwnerStatus`,
  (owner) => ({ payload: { owner } })
);

export const setFields = createAction(
  `shareReducer/setFieldRestrictions`,
  (fields) => ({
    payload: { fields },
  })
);

export const updateFields = createAction(
  `shareReducer/updateFields`,
  (index, value) => ({ payload: { index, value } })
);

export const setNextStep = createAction(`shareReducer/nextStep`);

export const setPreviousStep = createAction(`shareReducer/previousStep`);

export const removeSample = createAction(
  `shareReducer/removeSample`,
  (index) => ({
    payload: { index },
  })
);

export const copySamples = createAsyncThunk(
  `shareReducer/copySamples`,
  async (_, { getState }) => {
    const state = getState();
    console.log(state);
  }
);

const storedState = (() => {
  const sharedString = sessionStorage.getItem("share");
  return sharedString ? JSON.parse(sharedString) : {};
})();

export default createReducer(
  { ...storedState, owner: true, step: 0 },
  (builder) => {
    builder
      .addCase(setDestinationProject, (state, action) => {
        state.destination = action.payload.project;
      })
      .addCase(updatedSamplesOwnerStatus, (state, action) => {
        state.owner = action.payload.owner;
      })
      .addCase(setFields, (state, action) => {
        state.fields = action.payload.fields;
      })
      .addCase(updateFields, (state, action) => {
        state.fields[action.payload.index].target.restriction =
          action.payload.value;
      })
      .addCase(setNextStep, (state) => {
        console.log("HELLO");
        state.step = state.step + 1;
      })
      .addCase(setPreviousStep, (state) => {
        state.step = state.step - 1;
      })
      .addCase(removeSample, (state, action) => {
        const samples = [...state.samples];
        samples.splice(action.payload.index, 1);
        state.samples = samples;
      });
  }
);
