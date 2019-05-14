import cloneDeep from "lodash/cloneDeep";

export const NO_TEMPLATE_INDEX = 0;

export const types = {
  LOAD: "METADATA/TEMPLATES/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/TEMPLATES/LOAD_TEMPLATES_ERROR",
  LOAD_SUCCESS: "METADATA/TEMPLATES/LOAD_TEMPLATES_SUCCESS",
  USE_TEMPLATE: "METADATA/TEMPLATES/USE_TEMPLATE",
  TABLE_MODIFIED: "METADATA/TEMPLATES/TABLE_MODIFIED",
  SAVE_TEMPLATE: "METADATA/TEMPLATES/SAVE_TEMPLATE",
  SAVING_TEMPLATE: "METADATA/TEMPLATES/SAVING_TEMPLATE",
  SAVED_TEMPLATE: "METADATA/TEMPLATES/SAVED_TEMPLATE",
  SAVE_COMPLETE: "METADATA/TEMPLATES/SAVE_COMPLETE",
  TEMPLATE_MODIFIED: "METADATA/TEMPLATES/TEMPLATE_MODIFIED"
};

const initialState = {
  fetching: false, // Whether there is an ajax request to fetch the list of UIMetadataTemplates
  error: false, // Whether there was an error while fetching the UIMetadataTemplates
  templates: [], // The List of UIMetadataTemplates returned.
  current: NO_TEMPLATE_INDEX, // Which template is currently being used.
  saving: false, // Flag whether the current template is being actively saved to the server
  saved: false // Flag whether the current template's save function has been completed.
};

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      /*
      Let the UI know that there has been a request for the MetadataTemplates
       */
      return { ...state, fetching: true, error: false };
    case types.LOAD_SUCCESS:
      /*
      Let the UI know that the templates were fetched successfully, and
      pass the templates along.  Each template should have a modified state.
       */
      return {
        ...state,
        fetching: false,
        templates: action.templates.map(t => {
          t.modified = [];
          return t;
        })
      };
    case types.LOAD_ERROR:
      /*
      Let the UI know that there was an error loading the MetadataTemplates.
       */
      return { ...state, fetching: false, error: true };
    case types.USE_TEMPLATE:
      /*
      Update the current template to show unmodified
      If the action.index == the current template the it will just
      reset the modified state.
       */
      return {
        ...state,
        templates: (() => {
          const t = [...state.templates];
          t[state.current].modified = [];
          return t;
        })(),
        current: action.index
      };
    case types.TABLE_MODIFIED:
      /*
      This is a modification of the current template through ag-grid.
       */
      return {
        ...state,
        templates: (() => {
          const t = [...state.templates];
          t[state.current].modified = action.fields;
          return t;
        })()
      };
    case types.TEMPLATE_MODIFIED:
      /*
      This is a modification of the current template through ag-grid.
       */
      return {
        ...state,
        templates: (() => {
          const templates = [...state.templates];
          const template = templates[state.current];
          // Determine if the current template has been modified or needs to be
          // Updated, then find the correct field and update the hidden status.

          const fields =
            template.modified.length > 0 // Empty array if the template is not modified
              ? [...template.modified]
              : cloneDeep(template.fields); // cloneDeep so we ensure not to overwrite the original fields

          template.modified = fields.map(f => {
            if (f.field === action.field.field) {
              f.hide = !action.field.hide;
            }
            return f;
          });
          return templates;
        })()
      };
    case types.SAVE_TEMPLATE:
      return { ...state, saving: true };
    case types.SAVED_TEMPLATE:
      // Clear modifications to the current template.
      return {
        ...state,
        saving: false,
        saved: true,
        ...(() => {
          const t = [...state.templates];
          t[state.current].modified = [];

          const { template } = action;
          template.modified = [];

          console.log(template);

          let index = t.findIndex(temp => temp.id === template.id);

          if (index > 0) {
            /*
            This was a template update, update the current template.
             */
            t[index] = template;
          } else {
            /*
            New template created, add it to the end of the list and set it as selected.
             */
            t.push(template);
            index = t.length - 1;
          }

          return { templates: t, current: index };
        })()
      };
    case types.SAVE_COMPLETE:
      return { ...state, saved: false };
    default:
      return state;
  }
};

export const actions = {
  load: () => ({ type: types.LOAD }),
  success: templates => ({ type: types.LOAD_SUCCESS, templates }),
  error: error => ({ type: types.LOAD_ERROR, error }),
  use: index => ({ type: types.USE_TEMPLATE, index }),
  tableModified: fields => ({ type: types.TABLE_MODIFIED, fields }),
  saveTemplate: (name, fields, id) => ({
    type: types.SAVE_TEMPLATE,
    data: { name, fields, id }
  }),
  savedTemplate: template => ({ type: types.SAVED_TEMPLATE, template }),
  savedComplete: () => ({ type: types.SAVE_COMPLETE }),
  templateModified: field => ({ type: types.TEMPLATE_MODIFIED, field })
};
