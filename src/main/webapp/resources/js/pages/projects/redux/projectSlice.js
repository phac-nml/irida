import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { setDefaultMetadataTemplate } from "../../../apis/projects/projects";
import { updateProcessingPriority } from "../../../apis/projects/settings";

/**
 * Update the project automated analysis priority.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const putPriorityUpdate = createAsyncThunk(
  `pipelines/putPriorityUpdate`,
  async ({ projectId, priority }, { rejectWithValue }) => {
    try {
      const message = await updateProcessingPriority(projectId, priority);
      return { priority, message };
    } catch (e) {
      rejectWithValue(e);
    }
  }
);

/**
 * Redux Async Thunk for setting a default template for a specific project.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const setDefaultTemplateForProject = createAsyncThunk(
  `project/setDefaultTemplateForProject`,
  async ({ projectId, templateId }, { rejectWithValue }) => {
    try {
      const message = await setDefaultMetadataTemplate(projectId, templateId);
      return { templateId, message };
    } catch (e) {
      return rejectWithValue(e);
    }
  }
);

export const projectSlice = createSlice({
  name: "project",
  initialState: {
    canManage: false,
    roles: [],
  },
  reducers: {},
  extraReducers: {
    [putPriorityUpdate.fulfilled]: (state, action) => {
      state.priority = action.payload.priority;
    },
    [setDefaultTemplateForProject.fulfilled]: (state, action) => {
      state.defaultMetadataTemplateId = action.payload.templateId;
    },
  },
});

export default projectSlice.reducer;
