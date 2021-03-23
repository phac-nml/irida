import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  deleteAnalysisTemplateForProject,
  fetchAnalysisTemplatesForProject,
  fetchProcessingInformation,
  updateProcessingPriority,
} from "../../../apis/projects/settings";

export const fetchAnalysisTemplates = createAsyncThunk(
  `pipelines/fetchAnalysisTemplates`,
  async (projectId) => {
    return await fetchAnalysisTemplatesForProject(projectId);
  },
  {
    condition(_args, { getState }) {
      /*
      We only want to get the data if it has not already been fetched.
       */
      const { pipelines } = getState();
      if ("templates" in pipelines) {
        return false;
      }
    },
  }
);

export const deletePipeline = createAsyncThunk(
  `pipelines/deletePipeline`,
  async ({ templateId, projectId }) => {
    const message = await deleteAnalysisTemplateForProject(
      templateId,
      projectId
    );
    return { message, templateId };
  }
);

export const fetchPipelinePriorityInfo = createAsyncThunk(
  `pipelines/fetchPipelinePriorityInfo`,
  async (projectId) => {
    return await fetchProcessingInformation(projectId);
  },
  {
    condition(_args, { getState }) {
      /*
      We only want to get the data if it has not already been fetched.
       */
      const { pipelines } = getState();
      if ("priorities" in pipelines) {
        return false;
      }
    },
  }
);

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

export const pipelinesSlice = createSlice({
  name: "project/pipelines",
  initialState: {
    loading: true,
  },
  reducers: {},
  extraReducers: {
    [fetchAnalysisTemplates.fulfilled]: (state, action) => {
      state.templates = action.payload;
      state.loading = false;
    },
    [deletePipeline.fulfilled]: (state, action) => {
      state.templates = state.templates.filter(
        (template) => template.id !== action.payload.templateId
      );
    },
    [fetchPipelinePriorityInfo.fulfilled]: (state, action) => {
      state.priority = action.payload.priority;
      state.priorities = action.payload.priorities;
    },
    [putPriorityUpdate.fulfilled]: (state, action) => {
      state.priority = action.payload.priority;
    },
  },
});

export default pipelinesSlice.reducer;
