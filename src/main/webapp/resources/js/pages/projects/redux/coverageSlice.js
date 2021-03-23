import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  fetchProcessingCoverage,
  putProcessingCoverage,
} from "../../../apis/projects/settings";

const NOT_SET = i18n("ProcessingCoverage.not-set");

export const fetchProjectCoverage = createAsyncThunk(
  `processing/fetchProjectCoverage`,
  async (projectId) => {
    const { minimum, maximum, genomeSize } = await fetchProcessingCoverage(
      projectId
    );
    return {
      minimum: minimum > -1 ? minimum : NOT_SET,
      maximum: maximum > -1 ? maximum : NOT_SET,
      genomeSize: genomeSize > -1 ? genomeSize : NOT_SET,
    };
  },
  {
    condition(_args, { getState }) {
      /*
      We only want to get the data if it has not already been fetched.
       */
      const { coverage } = getState();
      if ("maximum" in coverage) {
        return false;
      }
    },
  }
);

export const updateProcessingCoverage = createAsyncThunk(
  `processing/updateProcessingCoverage`,
  async ({ projectId, coverage }) => {
    const message = await putProcessingCoverage(projectId, coverage);
    return {
      ...coverage,
      message,
    };
  }
);

export const coverageSlice = createSlice({
  name: "project/processing",
  initialState: {
    loading: true,
  },
  reducers: {},
  extraReducers: {
    [fetchProjectCoverage.fulfilled]: (state, action) => ({
      ...state,
      ...action.payload,
      loading: false,
    }),
    [updateProcessingCoverage.fulfilled]: (state, action) => ({
      ...state,
      ...action.payload,
    }),
  },
});

export default coverageSlice.reducer;
