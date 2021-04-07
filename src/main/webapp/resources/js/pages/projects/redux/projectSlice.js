import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { setDefaultMetadataTemplate } from "../../../apis/metadata/metadata-templates";

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
    canManage: window.project?.canManage || false,
    defaultMetadataTemplateId: window.project?.defaultMetadataTemplateId,
  },
  reducers: {},
  extraReducers: {
    [setDefaultTemplateForProject.fulfilled]: (state, action) => {
      state.defaultMetadataTemplateId = action.payload.templateId;
    },
  },
});

export default projectSlice.reducer;
