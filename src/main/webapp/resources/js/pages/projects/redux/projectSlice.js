import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  getProjectDetails,
  updateProjectAttribute,
} from "../../../apis/projects/projects";

/**
 * Redux Async Thunk for fetching project details
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const fetchProjectDetails = createAsyncThunk(
  `project/details`,
  async (projectId) => {
    return await getProjectDetails(projectId);
  }
);

/**
 * Redux thunk for updating aspects of the project details
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const updateProjectDetails = createAsyncThunk(
  `project/update`,
  async ({ field, value }, { getState, rejectWithValue }) => {
    try {
      const { id: projectId } = getState().project;
      const message = await updateProjectAttribute({
        projectId,
        field,
        value,
      });
      return { field, value, message };
    } catch (e) {
      return rejectWithValue(e);
    }
  }
);

export const projectSlice = createSlice({
  name: "project",
  initialState: {
    canManage: false,
    loading: true,
  },
  reducers: {},
  extraReducers: {
    [fetchProjectDetails.fulfilled]: (state, action) => ({
      ...state,
      ...action.payload,
      loading: false,
    }),
    [updateProjectDetails.fulfilled]: (state, action) => {
      return {
        ...state,
        [action.payload.field]: action.payload.value,
      };
    },
  },
});

export default projectSlice.reducer;
