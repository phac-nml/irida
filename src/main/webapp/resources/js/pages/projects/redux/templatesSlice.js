import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  createProjectMetadataTemplate,
  deleteMetadataTemplate,
  getProjectMetadataTemplates,
  updateMetadataTemplate,
} from "../../../apis/metadata/metadata-templates";
import { addKeysToList } from "../../../utilities/http-utilities";

/**
 * Redux Async Thunk for fetching all the templates for a specific project.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const fetchTemplatesForProject = createAsyncThunk(
  `templates/fetchTemplatesForProject`,
  async (projectId) => {
    const templates = await getProjectMetadataTemplates(projectId);
    return addKeysToList(templates, "template", "identifier");
  },
  {
    condition(projectId, { getState }) {
      const { templates } = getState();
      if (templates.requests[projectId]) {
        // Already fetched or in progress, don't need to re-fetch
        return false;
      }
    },
  }
);

/**
 * Redux Async Thunk for removing a template from a specific project.
 * @type {AsyncThunk<unknown, void, {}>}
 */
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

/**
 * Update details within a template (name, description, and fields)
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const updateTemplate = createAsyncThunk(
  `templates/updateTemplate`,
  async (template, { rejectWithValue }) => {
    try {
      const message = await updateMetadataTemplate(template);
      return { message, template };
    } catch (e) {
      return rejectWithValue(e);
    }
  }
);

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
        name: i18n("MetadataTemplates.allFields"),
        label: i18n("MetadataTemplates.allFields"),
        description: i18n("MetadataTemplates.allFields-description"),
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
    /*
    Successful fetching of metadata templates for the current project.
     */
    [fetchTemplatesForProject.fulfilled]: (state, { meta, payload }) => {
      const requests = { ...state.requests };
      delete requests[meta.args];
      return {
        ...state,
        templates: [...payload, ...state.templates],
        loading: false,
        requests,
      };
    },
    [fetchTemplatesForProject.pending]: (state, { meta }) => {
      state.requests[meta.arg] = meta.requestId;
    },
    [removeTemplateFromProject.fulfilled]: (state, { payload }) => {
      const templates = state.templates.filter(
        (template) => template.identifier !== payload.templateId
      );
      return { ...state, templates };
    },
    [createNewMetadataTemplate.fulfilled]: (state, action) => {
      if (state.templates !== undefined) {
        const templates = [...state.templates];
        templates.unshift(action.payload);
        return { ...state, templates };
      }
    },
    [updateTemplate.fulfilled]: (state, action) => {
      const index = state.templates.findIndex(
        (template) => template.identifier === action.payload.template.identifier
      );
      if (index >= 0) {
        state.templates[index] = action.payload.template;
      }
      return state;
    },
  },
});

export const { updateFieldsForAllFieldsTemplate } = templatesSlice.actions;

export default templatesSlice.reducer;
