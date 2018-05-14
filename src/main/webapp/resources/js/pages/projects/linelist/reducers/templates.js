import { List, fromJS } from "immutable";

const { i18n } = window.PAGE;

export const MODIFIED_SELECT_INDEX = -1;
export const NO_TEMPLATE_INDEX = 0;

export const types = {
  LOAD: "METADATA/TEMPLATES/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/TEMPLATES/LOAD_TEMPLATES_ERROR",
  LOAD_SUCCESS: "METADATA/TEMPLATES/LOAD_TEMPLATES_SUCCESS",
  USE_TEMPLATE: "METADATA/TEMPLATES/USE_TEMPLATE",
  TEMPLATE_MODIFIED: "METADATA/TEMPLATES/TEMPLATE_MODIFIED",
  SAVE_TEMPLATE: "METADATA/TEMPLATES/SAVE_TEMPLATE",
  SAVING_TEMPLATE: "METADATA/TEMPLATES/SAVING_TEMPLATE"
};

const NO_TEMPLATE = {
  name: i18n.linelist.templates.Select.none,
  id: -1,
  fields: []
};

const initialState = fromJS({
  fetching: false,
  error: false,
  templates: List(),
  current: NO_TEMPLATE_INDEX,
  modified: null
});

function setModifiedTemplate(state, fields) {
  const current = state.get("current");
  const template =
    current === NO_TEMPLATE_INDEX
      ? { name: "", id: null }
      : state
          .get("templates")
          .get(current)
          .toJS();
  return state.set("modified", {
    name: template.name,
    id: template.id,
    fields
  });
}

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return state.set("fetching", true).set("error", false);
    case types.LOAD_SUCCESS:
      return state
        .set("fetching", false)
        .set("templates", fromJS([NO_TEMPLATE, ...action.templates]));
    case types.LOAD_ERROR:
      return state.set("fetching", false).set("error", true);
    case types.USE_TEMPLATE:
      return state.set("current", action.index).set("modified", null);
    case types.TEMPLATE_MODIFIED:
      return setModifiedTemplate(state, action.fields);
    default:
      return state;
  }
};

export const actions = {
  load: () => ({ type: types.LOAD }),
  success: templates => ({ type: types.LOAD_SUCCESS, templates }),
  error: error => ({ type: types.LOAD_ERROR, error }),
  use: index => ({ type: types.USE_TEMPLATE, index }),
  modified: fields => ({ type: types.TEMPLATE_MODIFIED, fields }),
  saveTemplate: (name, fields, id) => ({
    type: types.SAVE_TEMPLATE,
    data: { name, fields, id }
  })
};
