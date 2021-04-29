import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { createProjectMetadataTemplate } from "../../../apis/metadata/metadata-templates";

/**
 * Create a new metadata template
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const createNewMetadataTemplate = createAsyncThunk(
  `templates/create`,
  async ({ template, projectId }, { rejectWithValue }) => {
    try {
      return await createProjectMetadataTemplate(projectId, template);
    } catch (e) {
      return rejectWithValue(e);
    }
  }
);

export const templatesSlice = createSlice({
  name: "templates",
  initialState: {
    templates: [
      {
        name: i18n("MetadataTemplatesList.allFields"),
        label: i18n("MetadataTemplatesList.allFields"),
        description: i18n("MetadataTemplatesList.allFields-description"),
        identifier: 0,
        key: "template-0",
        fields: [],
      },
    ],
    loading: true,
    template: undefined,
    requests: {},
  },
  reducers: {
    // Reducer to update the fields array for the All Fields Template
    updateFieldsForAllFieldsTemplate(state, action) {
      state.templates[state.templates.length - 1].fields = action.payload;
    },
  },
  extraReducers: {
    [createNewMetadataTemplate.fulfilled]: (state, action) => {
      if (state.templates !== undefined) {
        const templates = [...state.templates];
        templates.unshift(action.payload);
        return { ...state, templates };
      }
    },
  },
});

export const { updateFieldsForAllFieldsTemplate } = templatesSlice.actions;

export default templatesSlice.reducer;
