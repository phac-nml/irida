import { createAction, createSlice } from "@reduxjs/toolkit";

export const setProject = createAction(
  `share/setProject`,
  function setProject(project) {
    return {
      payload: { project },
    };
  }
);

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
