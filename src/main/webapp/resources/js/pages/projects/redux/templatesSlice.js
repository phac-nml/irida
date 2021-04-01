import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  createProjectMetadataTemplate,
  deleteMetadataTemplate,
  getProjectMetadataTemplates,
  setDefaultMetadataTemplate,
  updateMetadataTemplate,
} from "../../../apis/metadata/metadata-templates";
import { addKeysToList } from "../../../utilities/http-utilities";
import { updateProjectDefaultMetadataTemplateId } from "./projectSlice";

/**
 * Redux Async Thunk for fetching all the templates for a specific project.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const fetchTemplatesForProject = createAsyncThunk(
  `templates/fetchTemplatesForProject`,
  async (projectId) => {
    const templates = await getProjectMetadataTemplates(projectId);
    return addKeysToList(templates, "template", "id");
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

/**
 * Redux Async Thunk for setting a default template for a specific project.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const setDefaultTemplateForProject = createAsyncThunk(
  `templates/setDefaultTemplateForProject`,
  async ({ projectId, templateId }, { dispatch, rejectWithValue }) => {
    try {
      const message = await setDefaultMetadataTemplate(projectId, templateId);
      // Update the defaultMetadataTemplateId within the project in the state
      dispatch(updateProjectDefaultMetadataTemplateId(templateId));
      return { templateId, message };
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
        description: i18n("MetadataTemplatesList.allfields-description"),
        identifier: 0,
        key: "template-0",
        fields: [],
      },
    ],
    loading: true,
    template: undefined,
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
    [fetchTemplatesForProject.fulfilled]: (state, { payload }) => {
      return {
        ...state,
        templates: [...payload, ...state.templates],
        loading: false,
      };
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

export const {
  setTemplate,
  updateFieldsForAllFieldsTemplate,
} = templatesSlice.actions;

export default templatesSlice.reducer;
