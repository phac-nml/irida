import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getProjectMetadataTemplates } from "../../../../apis/metadata/metadata-templates";
import { addKeysToList } from "../../../../utilities/http-utilities";

export const fetchTemplatesForProject = createAsyncThunk(
  `templates/fetchTemplatesForProject`,
  async (projectId) => {
    const templates = await getProjectMetadataTemplates(projectId);
    return addKeysToList(templates, "template", "id");
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
  },
});

export default templatesSlice.reducer;
