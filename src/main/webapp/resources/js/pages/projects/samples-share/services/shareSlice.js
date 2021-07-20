import { createAction, createAsyncThunk, createSlice } from "@reduxjs/toolkit";

/*
Action to set the target project (project that will receive the samples)
 */
export const setTargetProject = createAction(
  `share/setDestinationProject`,
  (project) => ({ payload: { project } })
);

/*
Action to set the ownership permissions on a sample.  If "locked", samples will
not be modifiable or movable in the target project.
 */
export const setSamplesLockedStatus = createAction(
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

/*
When a user selects the samples they want to copy and selects "Copy Samples", tbe
selected sample information is stored im session storage since it can become quite
large.  When this page loads we are simply retrieving that information out of
the session and converting it back to a json object for use as the initial state.
 */
const storedState = (() => {
  const sharedString = sessionStorage.getItem("share");
  return sharedString ? JSON.parse(sharedString) : {};
})();

export const shareSlice = createSlice({
  name: "share",
  initialState: { ...storedState, locked: false, step: 0 },
  reducers: {
    setNextStep: (state) => {
      state.step = state.step + 1;
    },
    setPreviousStep: (state) => {
      state.step = state.step - 1;
    },
    setTargetProject: (state, action) => {
      state.destination = action.payload.project;
    },
    setSamplesLockedStatus: (state, action) => {
      state.locked = action.payload.locked;
    },
    setFields: (state, action) => {
      state.fields = action.payload.fields;
    },
    updateFields: (state, action) => {
      state.fields[action.payload.index].target.restriction =
        action.payload.value;
    },
    removeSample: (state, action) => {
      const samples = [...state.samples];
      samples.splice(action.payload.index, 1);
      state.samples = samples;
    },
  },
});

export const { setNextStep, setPreviousStep } = shareSlice.actions;
