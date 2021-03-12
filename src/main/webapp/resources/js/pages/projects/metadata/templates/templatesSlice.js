import {createAsyncThunk, createSlice} from "@reduxjs/toolkit";
import {
  createProjectMetadataTemplate,
  deleteMetadataTemplate,
  getProjectMetadataTemplates,
  updateMetadataTemplate,
} from "../../../../apis/metadata/metadata-templates";
import {addKeysToList} from "../../../../utilities/http-utilities";

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

export const templatesSlice = createSlice({
  name: "templates",
  initialState: {
    templates: [],
    loading: true,
    template: undefined,
  },
  reducers: {},
  extraReducers: (builder) => {
    /*
    Occurs on page load to add all templates.
     */
    builder.addCase(
      fetchTemplatesForProject.fulfilled,
      (state, { payload }) => {
        state.templates = payload;
        state.loading = false;
      }
    );

    builder.addCase(
      removeTemplateFromProject.fulfilled,
      (state, { payload }) => {
        return {
          ...state,
          templates: state.templates.filter(
            (template) => template.id !== payload.templateId
          ),
        };
      }
    );
  },

  //   [removeTemplateFromProject.fulfilled]: (state, action) => ({
  //     ...state,
  //     templates: state.templates.filter(
  //       (template) => template.id !== action.payload.templateId
  //     ),
  //   }),
  //   [updateTemplate.fulfilled]: (state, action) => {
  //     const index = state.templates.findIndex(
  //       (template) => template.identifier === action.payload.template.identifier
  //     );
  //     if (index) {
  //       state.templates[index] = action.payload.template;
  //     }
  //   },
  //   [createNewMetadataTemplate.fulfilled]: (state, action) => {
  //     state.templates.shift(action.payload);
  //     return action.payload;
  //   },
  // },
});

export const { setTemplate } = templatesSlice.actions;

export default templatesSlice.reducer;
