import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  createProjectMetadataTemplate,
  deleteMetadataTemplate,
  getProjectDefaultMetadataTemplate,
  getProjectMetadataTemplates,
  removeDefaultMetadataTemplate,
  setDefaultMetadataTemplate,
  updateMetadataTemplate,
} from "../../../../../apis/metadata/metadata-templates";
import { addKeysToList } from "../../../../../utilities/http-utilities";

/**
 * Redux Async Thunk for fetching all the templates for a specific project.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const fetchTemplatesForProject = createAsyncThunk(
  `templates/fetchTemplatesForProject`,
  async (projectId) => {
    const templates = await getProjectMetadataTemplates(projectId);
    return addKeysToList(templates, "template", "identifier");
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
  async ({ projectId, templateId }, { rejectWithValue }) => {
    try {
      const message = await setDefaultMetadataTemplate(projectId, templateId);
      return { templateId, message };
    } catch (e) {
      return rejectWithValue(e);
    }
  }
);

/**
 * Redux Async Thunk for getting a default template for a specific project.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const getDefaultTemplateForProject = createAsyncThunk(
  `templates/getDefaultTemplateForProject`,
  async ({ projectId }, { rejectWithValue }) => {
    try {
      return await getProjectDefaultMetadataTemplate(projectId);
    } catch (e) {
      return rejectWithValue(e);
    }
  }
);

/**
 * Redux Async Thunk for removing a default template for a specific project.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const removeDefaultTemplateForProject = createAsyncThunk(
  `templates/removeDefaultTemplateForProject`,
  async ({ projectId }, { rejectWithValue }) => {
    try {
      const message = await removeDefaultMetadataTemplate(projectId);
      return { message };
    } catch (e) {
      return rejectWithValue(e);
    }
  }
);

export const templatesSlice = createSlice({
  name: "templates",
  initialState: {
    templates: undefined,
    loading: true,
    template: undefined,
    defaultTemplate: undefined,
  },
  reducers: {},
  extraReducers: {
    /*
    Successful fetching of metadata templates for the current project.
     */
    [fetchTemplatesForProject.fulfilled]: (state, { payload }) => {
      return {
        ...state,
        templates: payload,
        loading: false,
      };
    },
    [removeTemplateFromProject.fulfilled]: (state, { payload }) => {
      const templates = state.templates.filter(
        (template) => template.identifier !== payload.templateId
      );
      state.defaultTemplate = 0;
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
    [setDefaultTemplateForProject.fulfilled]: (state, { payload }) => {
      const index = state.templates.findIndex(
        (template) => template.identifier === payload.templateId
      );
      if (index >= 0) {
        state.defaultTemplate = payload.templateId;
      }
      return state;
    },
    [getDefaultTemplateForProject.fulfilled]: (state, { payload }) => {
      if (payload !== null) {
        state.defaultTemplate = payload.identifier;
      } else {
        state.defaultTemplate = 0;
      }
      return state;
    },
    [removeDefaultTemplateForProject.fulfilled]: (state) => {
      state.defaultTemplate = 0;
      return state;
    },
  },
});

export const { setTemplate } = templatesSlice.actions;

export default templatesSlice.reducer;
