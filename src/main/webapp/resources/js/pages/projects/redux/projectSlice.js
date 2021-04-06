import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  getProjectDetails,
  updateProjectAttribute,
} from "../../../apis/projects/projects";
import { updateProcessingPriority, setDefaultMetadataTemplate } from "../../../apis/projects/settings";

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
  `templates/setDefaultTemplateForProject`,
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
    canManage: window.project?.canManage || false,
    loading: true,
    defaultMetadataTemplateId: window.project?.defaultMetadataTemplateId,
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
    [putPriorityUpdate.fulfilled]: (state, action) => {
      state.priority = action.payload.priority;
    },
    [setDefaultTemplateForProject.fulfilled]: (state, action) => {
      state.defaultMetadataTemplateId = action.payload.templateId;
    },
  },
});

export default projectSlice.reducer;
