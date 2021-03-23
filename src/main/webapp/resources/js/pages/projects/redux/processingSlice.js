import {createAsyncThunk, createSlice} from "@reduxjs/toolkit";
import {
  fetchProcessingCoverage,
  putProcessingCoverage
} from "../../../apis/projects/settings";

const NOT_SET = i18n("ProcessingCoverage.not-set");

export const fetchProjectCoverage = createAsyncThunk(
  `processing/coverage`,
  async (projectId) => {
    const {minimum, maximum, genomeSize} = await fetchProcessingCoverage(
      projectId
    );
    return {
      minimum: minimum > -1 ? minimum : NOT_SET,
      maximum: maximum > -1 ? maximum : NOT_SET,
      genomeSize: genomeSize > -1 ? genomeSize : NOT_SET,
    }
  }
)

export const updateProcessingCoverage = createAsyncThunk(
  `process/update/coverage`,
  async ({projectId, coverage}) => {
    const message = await putProcessingCoverage(projectId, coverage)
    return {
      ...coverage,
      message
    }
  }
)

export const processingSlice = createSlice({
  name: "project/processing",
  initialState: {},
  reducers: {},
  extraReducers: {
    [fetchProjectCoverage.fulfilled]: (state, action) => ({
      ...state,
      ...action.payload
    }),
    [updateProcessingCoverage.fulfilled]: (state, action) => ({
      ...state,
      ...action.payload
    })
  }
})

export default processingSlice.reducer;