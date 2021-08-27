import { createAction, createSlice } from "@reduxjs/toolkit";

export const setProject = createAction(`share/setProject`, (projectId) => ({
  payload: { projectId },
}));

export const removeSample = createAction(`share/removeSample`, (sampleId) => ({
  payload: { sampleId },
}));

/**
 * Set up the initial state.  This is pulled from session storage which should
 * be accessed by the key "share".  The stringified object should be of the format:
 * <code>
 *   {
 *     samples: [],
 *     projectId: 1,
 *     timestamp: 1629374711590
 *   }
 * </code>
 * @type {{currentProject, samples}}
 */
const initialState = (() => {
  const stringData = window.sessionStorage.getItem("share");
  const { samples, projectId: currentProject } = JSON.parse(stringData);
  return { originalSamples: samples, samples, currentProject };
})();

const shareSlice = createSlice({
  name: "share",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(setProject, (state, action) => {
      state.projectId = action.payload.projectId;
    });

    builder.addCase(removeSample, (state, action) => {
      state.originalSamples = state.originalSamples.filter(
        (sample) => sample.id !== action.payload.sampleId
      );
    });
  },
});

export default shareSlice.reducer;
