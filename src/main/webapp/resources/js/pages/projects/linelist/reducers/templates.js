import { List, fromJS } from "immutable";

export const MODIFIED_SELECT_INDEX = -2;
export const NO_TEMPLATE_INDEX = -1;

export const types = {
  LOAD: "METADATA/TEMPLATES/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/TEMPLATES/LOAD_TEMPLATES_ERROR",
  LOAD_SUCCESS: "METADATA/TEMPLATES/LOAD_TEMPLATES_SUCCESS",
  USE_TEMPLATE: "METADATA/TEMPLATES/USE_TEMPLATE",
  TEMPLATE_MODIFIED: "METADATA/TEMPLATES/TEMPLATE_MODIFIED"
};

const initialState = fromJS({
  fetching: false,
  error: false,
  templates: List(),
  current: -1,
  modified: false
});

function setModifiedTemplate(state, fields) {
  const current = state.get("current");

  // Check to see if it is a current template being modified
  if (current > -1) {
    const template = state.get("templates").get(current);
    return state.set("modified", {
      name: template.name,
      id: template.id,
      fields
    });
  }
  return state.set("modified", { name: "", fields });
}

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return state.set("fetching", true).set("error", false);
    case types.LOAD_SUCCESS:
      return state
        .set("fetching", false)
        .set("templates", fromJS(action.templates));
    case types.LOAD_ERROR:
      return state.set("fetching", false).set("error", true);
    case types.USE_TEMPLATE:
      return state.set("current", action.index);
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
  modified: fields => ({ type: types.TEMPLATE_MODIFIED, fields })
};
