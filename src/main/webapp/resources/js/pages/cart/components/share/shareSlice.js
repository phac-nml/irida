import { createAction, createSlice } from "@reduxjs/toolkit";

/**
 * Redux action to set the project to share samples with
 * @type {PayloadActionCreator<ReturnType<function(*): {payload: {project: *}}>["payload"], "share/setProject", function(*): {payload: {project: *}}>}
 */
export const setProject = createAction(
  `share/setProject`,
  function setProject(project) {
    return {
      payload: { project },
    };
  }
);

/**
 * Redux slice for storing state for sharing sample with another project
 * @type {Slice<{current: number}, {nextStep: reducers.nextStep}, string>}
 */
const shareSlice = createSlice({
  name: `share`,
  initialState: { current: 0 },
  reducers: {
    nextStep: (state) => {
      state.current += 1;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(setProject, (state, action) => {
      state.project = action.payload.project;
    });
  },
});

export const { nextStep } = shareSlice.actions;

export default shareSlice.reducer;
