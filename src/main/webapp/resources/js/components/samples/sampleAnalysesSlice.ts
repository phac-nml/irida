import { createAction, createSlice } from "@reduxjs/toolkit";
import { SampleAnalyses } from "../../apis/samples/samples";

/**
 * Action to set the target sample analyses
 */
export const setSampleAnalyses = createAction(
  `sampleAnalyses/setSampleAnalyses`,
  ({ analyses }) => ({
    payload: { analyses },
  })
);

/**
 * Set up the initial state.
 */
const initialState: {
  analyses: SampleAnalyses[];
  loading: boolean;
} = (() => {
  return {
    analyses: [],
    loading: true,
  };
})();

const sampleAnalysesSlice = createSlice({
  reducers: {},
  name: "sampleAnalyses",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(setSampleAnalyses, (state, action) => {
      state.analyses = action.payload.analyses;
      state.loading = false;
    });
  },
});

export default sampleAnalysesSlice.reducer;
