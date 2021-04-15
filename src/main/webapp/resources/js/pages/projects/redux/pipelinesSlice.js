import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  deleteAnalysisTemplateForProject,
  fetchAnalysisTemplatesForProject,
} from "../../../apis/projects/settings";

/**
 * Get all analysis pipelines that can be can be automated on this project.
 * @type {AsyncThunk<unknown, void, {}>}
 */
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

/**
 * Delete an automated analysis pipeline from the project.
 * @type {AsyncThunk<{message: *, templateId: *}, {readonly templateId?: *, readonly projectId?: *}, {}>}
 */
export const deletePipeline = createAsyncThunk(
  `pipelines/deletePipeline`,
  async ({ analysisTemplateId, projectId }) => {
    const message = await deleteAnalysisTemplateForProject(
      analysisTemplateId,
      projectId
    );
    return { message, templateId: analysisTemplateId };
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
  },
});

export default pipelinesSlice.reducer;
