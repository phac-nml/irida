import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { fetchProcessingCoverage, } from "../../../apis/projects/settings";

/**
 * Internationalized term if the processing coverage is not set.
 * @constant
 * @type {string}
 */
const NOT_SET = i18n("ProcessingCoverage.not-set");

/**
 * Redux async thunk for fetching the project coverage details (minimum, maximum, and genome size).
 * Condition: will only be called once since this does not need to be updated on every view.
 * @type {AsyncThunk<{genomeSize: *|string, maximum: *|string, minimum: *|string}, void, {}>}
 */
export const fetchProjectCoverage = createAsyncThunk(
  `processing/fetchProjectCoverage`,
  /**
   * Get the processing coverage for a specific project
   * @param {number} projectId - identifier for the current project;
   * @returns {Promise<{genomeSize: (*|string), maximum: (*|string), minimum: (*|string)}>}
   */
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

/**
 * Redux async thunk for updating the processing coverage on a project.
 * @type {AsyncThunk<{[p: string]: *}, {readonly coverage?: *, readonly projectId?: *}, {}>}
 */
export const updateProcessingCoverage = createAsyncThunk(
  `processing/updateProcessingCoverage`,
  /**
   * Update processing coverage on a project
   * @param {number} projectId - identifier for the current project
   * @param {Object} coverage - Details about the new coverage (minimum, maximum, genomeSize)
   * @returns {Promise<{[p: string]: *}>}
   */
  async ({ projectId, coverage }) => {
    const message = await updateProcessingCoverage(projectId, coverage);
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
