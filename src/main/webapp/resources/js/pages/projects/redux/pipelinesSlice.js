import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  deleteAnalysisTemplateForProject,
  fetchAnalysisTemplatesForProject
} from "../../../apis/projects/settings";

export const fetchAnalysisTemplates = createAsyncThunk(
  `pipelines/fetchAnalysisTemplates`,
  async (projectId) => {
    return await fetchAnalysisTemplatesForProject(projectId);
  }
);

export const deletePipeline = createAsyncThunk(
    `pipelines/deletePipeline`,
  async ({ templateId, projectId }) => {
    const message = await deleteAnalysisTemplateForProject(templateId, projectId);
    return { message };
  }
);

export const pipelinesSlice = createSlice({
  name: "project/pipelines",
  initialState: {
    loading: true
  },
  reducers: {},
  extraReducers: {
    [fetchAnalysisTemplates.fulfilled]: (state, action) => {
      state.templates = action.payload;
      state.loading = false;
    }
  }
});

export default pipelinesSlice.reducer;