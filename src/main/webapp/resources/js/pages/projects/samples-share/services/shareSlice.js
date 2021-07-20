import { createAction, createAsyncThunk, createSlice } from "@reduxjs/toolkit";

export const setDestinationProject = createAction(
  `share/setDestinationProject`,
  (project) => ({ payload: { project } })
);

export const updatedSamplesOwnerStatus = createAction(
  `share/updatedSamplesOwnerStatus`,
  (owner) => ({ payload: { owner } })
);

export const setFields = createAction(
  `share/setFieldRestrictions`,
  (fields) => ({
    payload: { fields },
  })
);

export const updateFields = createAction(
  `share/updateFields`,
  (index, value) => ({ payload: { index, value } })
);

export const removeSample = createAction(`share/removeSample`, (index) => ({
  payload: { index },
}));

export const copySamples = createAsyncThunk(
  `share/copySamples`,
  async (_, { getState }) => {
    const state = getState();
    console.log(state);
  }
);

const storedState = (() => {
  const sharedString = sessionStorage.getItem("share");
  return sharedString ? JSON.parse(sharedString) : {};
})();

export const shareSlice = createSlice({
  name: "share",
  initialState: { ...storedState, owner: true, step: 0 },
  reducers: {
    setDestinationProject: (state, action) => {
      state.destination = action.payload.project;
    },
    updatedSamplesOwnerStatus: (state, action) => {
      state.owner = action.payload.owner;
    },
    setFields: (state, action) => {
      state.fields = action.payload.fields;
    },
    updateFields: (state, action) => {
      state.fields[action.payload.index].target.restriction =
        action.payload.value;
    },
    setNextStep: (state) => {
      state.step = state.step + 1;
    },
    setPreviousStep: (state) => {
      state.step = state.step - 1;
    },
    removeSample: (state, action) => {
      const samples = [...state.samples];
      samples.splice(action.payload.index, 1);
      state.samples = samples;
    },
  },
});

export const { setNextStep, setPreviousStep } = shareSlice.actions;
