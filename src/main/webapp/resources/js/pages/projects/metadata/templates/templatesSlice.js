import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  deleteMetadataTemplate,
  getProjectMetadataTemplates,
} from "../../../../apis/metadata/metadata-templates";
import { addKeysToList } from "../../../../utilities/http-utilities";

export const fetchTemplatesForProject = createAsyncThunk(
  `templates/fetchTemplatesForProject`,
  async (projectId) => {
    const templates = await getProjectMetadataTemplates(projectId);
    return addKeysToList(templates, "template", "id");
  }
);

export const removeTemplateFromProject = createAsyncThunk(
  `templates/removeTemplateFromProject`,
  async ({ projectId, templateId }, { rejectWithValue }) => {
    try {
      const message = await deleteMetadataTemplate(projectId, templateId);
      return { templateId, message };
    } catch (e) {
      return rejectWithValue(e);
    }
  }
);

export const templatesSlice = createSlice({
  name: "templates",
  initialState: {
    templates: [],
    loading: true,
  },
  reducers: {},
  extraReducers: {
    [fetchTemplatesForProject.fulfilled]: (state, action) => ({
      ...state,
      templates: action.payload,
      loading: false,
    }),
    [removeTemplateFromProject.fulfilled]: (state, action) => ({
      ...state,
      templates: state.templates.filter(
        (template) => template.id !== action.payload.templateId
      ),
    }),
  },
});

export default templatesSlice.reducer;
